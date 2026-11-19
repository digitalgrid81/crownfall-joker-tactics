package com.Ritzava1.Mewlup.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.core.di.ServiceLocator
import com.Ritzava1.Mewlup.data.sample.SampleData
import com.Ritzava1.Mewlup.domain.model.AppSettings
import com.Ritzava1.Mewlup.domain.model.ChallengeId
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.EventCard
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.model.FigureType
import com.Ritzava1.Mewlup.domain.model.MatchOutcome
import com.Ritzava1.Mewlup.domain.model.MatchPhase
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.model.MatchResultEntry
import com.Ritzava1.Mewlup.domain.model.MatchState
import com.Ritzava1.Mewlup.domain.model.MatchSummary
import com.Ritzava1.Mewlup.domain.model.OrderKind
import com.Ritzava1.Mewlup.domain.model.OrderMarker
import com.Ritzava1.Mewlup.domain.model.OrderSummary
import com.Ritzava1.Mewlup.domain.model.Owner
import com.Ritzava1.Mewlup.domain.model.ReplayRecord
import com.Ritzava1.Mewlup.domain.model.ReplayTurn
import com.Ritzava1.Mewlup.domain.model.SpecialSite
import com.Ritzava1.Mewlup.domain.model.seedOf
import com.Ritzava1.Mewlup.domain.repository.ProgressRepository
import com.Ritzava1.Mewlup.domain.repository.ReplayRepository
import com.Ritzava1.Mewlup.domain.repository.SettingsRepository
import com.Ritzava1.Mewlup.domain.usecase.chainsOf
import com.Ritzava1.Mewlup.domain.usecase.collectFavor
import com.Ritzava1.Mewlup.domain.usecase.decoyCost
import com.Ritzava1.Mewlup.domain.usecase.deployCost
import com.Ritzava1.Mewlup.domain.usecase.neighbours
import com.Ritzava1.Mewlup.domain.usecase.unmaskCost
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val replayRepository: ReplayRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var request: MatchRequest? = null
    private var match: MatchState? = null
    private var options: AppSettings = AppSettings()
    private var random: Random = Random(0)
    private var rivalUnmaskChance = 0
    private var pendingEvent: EventCard? = null
    private var resolveJob: Job? = null

    fun prepare(incoming: MatchRequest) {
        if (request == incoming && match != null) return
        resolveJob?.cancel()
        resolveJob = null
        request = incoming
        options = settingsRepository.settings()
        val seed = seedOf(incoming)
        random = Random(seed)
        pendingEvent = null
        val layout = ServiceLocator.generateBoard(seed, SampleData.districtNames)
        match = MatchState(
            districts = layout.districts,
            seed = seed,
            playerHome = layout.playerHome,
            rivalHome = layout.rivalHome
        )
        _state.value = GameUiState(
            ready = true,
            seed = seed,
            faction = incoming.faction,
            roster = SampleData.roster,
            threatOverlay = options.threatOverlay,
            reduceMotion = options.reduceMotion,
            challenge = incoming.challenge
        )
        beginTurn()
    }

    fun selectFigure(type: FigureType) {
        val current = _state.value
        if (current.phase != MatchPhase.ORDERS || current.paused) return
        if (type in current.spentFigures) return
        if (current.challenge == ChallengeId.NO_JESTERS && type == FigureType.JOKER) return
        val figure = current.roster.firstOrNull { it.type == type } ?: return
        if (deployCost(figure.cost, current.activeEvent) > current.favor) return
        val next = if (current.selectedFigure == type) null else type
        _state.value = current.copy(selectedFigure = next)
        publish()
    }

    fun selectDistrict(index: Int) {
        val current = _state.value
        if (current.phase != MatchPhase.ORDERS || current.paused) return
        val district = current.districts.getOrNull(index) ?: return
        if (district.burned) return
        val next = if (current.selectedDistrict == index) null else index
        _state.value = current.copy(selectedDistrict = next)
        publish()
    }

    fun primaryAction() {
        when (_state.value.cta) {
            CtaLabel.DEPLOY -> deploy()
            CtaLabel.FLIP_ORDERS -> requestFlip()
            CtaLabel.END_TURN -> endTurn()
        }
    }

    fun decoy() {
        val current = _state.value
        val state = match ?: return
        if (current.phase != MatchPhase.ORDERS || current.paused) return
        val target = current.selectedDistrict ?: return
        if (state.playerOrders.size >= GameConfig.ORDERS_PER_TURN) return
        if (state.playerOrders.any { it.districtIndex == target }) return
        val price = decoyPrice(current)
        if (price > state.playerFavor) return
        match = state.copy(
            playerFavor = state.playerFavor - price,
            playerOrders = state.playerOrders +
                OrderMarker(target, OrderKind.DECOY, null, Owner.PLAYER),
            decoysPlaced = state.decoysPlaced + 1
        )
        _state.value = current.copy(selectedDistrict = null)
        publish()
    }

    fun unmask() {
        val current = _state.value
        val state = match ?: return
        if (current.phase != MatchPhase.ORDERS || current.paused) return
        val target = current.selectedDistrict ?: return
        val marker = state.rivalOrders.firstOrNull { it.districtIndex == target } ?: return
        val price = unmaskCost(state.activeEvent)
        if (price > state.playerFavor) return
        val updated = if (marker.kind == OrderKind.DEPLOY) {
            state.copy(
                rivalOrders = state.rivalOrders.map {
                    if (it.districtIndex == target) it.copy(stunned = true) else it
                }
            )
        } else {
            state.copy(
                playerFavor = state.playerFavor - price,
                rivalFavor = (state.rivalFavor + 1).coerceAtMost(GameConfig.FAVOR_CAP)
            )
        }
        match = updated
        _state.value = current.copy(selectedDistrict = null)
        publish()
    }

    fun confirmFlip() {
        _state.value = _state.value.copy(confirmVisible = false)
        flipOrders()
    }

    fun dismissConfirm() {
        _state.value = _state.value.copy(confirmVisible = false)
    }

    fun pause() {
        resolveJob?.cancel()
        resolveJob = null
        val current = _state.value
        _state.value = current.copy(
            paused = true,
            revealed = false,
            phase = if (current.phase == MatchPhase.RESOLVING) MatchPhase.ORDERS else current.phase
        )
        publish()
    }

    fun resume() {
        _state.value = _state.value.copy(paused = false)
        publish()
    }

    private fun deploy() {
        val current = _state.value
        val state = match ?: return
        if (current.paused) return
        val type = current.selectedFigure ?: return
        val target = current.selectedDistrict ?: return
        if (state.playerOrders.size >= GameConfig.ORDERS_PER_TURN) return
        if (state.playerOrders.any { it.districtIndex == target }) return
        val figure = current.roster.firstOrNull { it.type == type } ?: return
        val price = deployCost(figure.cost, state.activeEvent)
        if (price > state.playerFavor) return
        match = state.copy(
            playerFavor = state.playerFavor - price,
            playerOrders = state.playerOrders +
                OrderMarker(target, OrderKind.DEPLOY, type, Owner.PLAYER),
            jokerDeployed = state.jokerDeployed || type == FigureType.JOKER
        )
        _state.value = current.copy(
            selectedFigure = null,
            selectedDistrict = null,
            spentFigures = current.spentFigures + type
        )
        publish()
    }

    private fun requestFlip() {
        val current = _state.value
        val state = match ?: return
        if (current.paused) return
        if (options.confirmEndTurn && state.playerOrders.size < GameConfig.ORDERS_PER_TURN) {
            _state.value = current.copy(confirmVisible = true)
            return
        }
        flipOrders()
    }

    private fun flipOrders() {
        if (resolveJob != null) return
        resolveJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                phase = MatchPhase.RESOLVING,
                revealed = true,
                ctaEnabled = false,
                selectedFigure = null,
                selectedDistrict = null,
                hint = GameHint.FLIP_ORDERS
            )
            applyRivalUnmask()
            val markers = (match?.playerOrders?.size ?: 0) + (match?.rivalOrders?.size ?: 0)
            var flipped = 0
            while (flipped < markers) {
                delay(GameConfig.FLIP_STAGGER_MS)
                flipped += 1
            }
            delay(GameConfig.RESOLVE_LOCKOUT_MS)
            resolveJob = null
            applyResolution()
        }
    }

    private fun applyRivalUnmask() {
        val state = match ?: return
        if (rivalUnmaskChance <= 0) return
        if (state.rivalFavor < unmaskCost(state.activeEvent)) return
        if (random.nextInt(100) >= rivalUnmaskChance) return
        val marker = state.playerOrders.randomOrNull(random) ?: return
        val price = unmaskCost(state.activeEvent)
        match = if (marker.kind == OrderKind.DEPLOY) {
            state.copy(
                playerOrders = state.playerOrders.map {
                    if (it.districtIndex == marker.districtIndex) it.copy(stunned = true) else it
                }
            )
        } else {
            state.copy(
                rivalFavor = state.rivalFavor - price,
                playerFavor = (state.playerFavor + 1).coerceAtMost(GameConfig.FAVOR_CAP),
                decoysCalled = state.decoysCalled + 1
            )
        }
    }

    private fun applyResolution() {
        var state = match ?: return
        val decoyCalledThisTurn = state.decoysCalled
        val resolution = ServiceLocator.resolveTurn(
            districts = state.districts,
            playerOrders = state.playerOrders,
            rivalOrders = state.rivalOrders,
            roster = SampleData.rosterByType,
            playerHome = state.playerHome
        )
        var districts = resolution.districts
        val playerDelta = ServiceLocator.scoreInfluence(districts, Owner.PLAYER, state.activeEvent)
        val rivalDelta = ServiceLocator.scoreInfluence(districts, Owner.RIVAL, state.activeEvent)
        val cathedralHeld = districts.firstOrNull { it.site == SpecialSite.CATHEDRAL }
            ?.let { it.owner == Owner.PLAYER && !it.burned } == true

        val drawn = ServiceLocator.drawEventCard(random)
        districts = ServiceLocator.drawEventCard.applyCard(drawn, districts, random)
        pendingEvent = drawn

        val turnRecord = ReplayTurn(
            turn = state.turn,
            event = drawn,
            player = summaryOf(state.playerOrders.firstOrNull(), state.districts),
            rival = summaryOf(state.rivalOrders.firstOrNull(), state.districts),
            playerDelta = playerDelta,
            rivalDelta = rivalDelta,
            decoyCalled = state.decoysCalled > decoyCalledThisTurn
        )

        state = state.copy(
            districts = districts,
            playerInfluence = state.playerInfluence + playerDelta,
            rivalInfluence = state.rivalInfluence + rivalDelta,
            cathedralStreak = if (cathedralHeld) state.cathedralStreak + 1 else 0,
            phase = MatchPhase.EVENT,
            turns = state.turns + turnRecord
        )
        match = state

        val ending = outcomeFor(state, resolution.forfeited)
        if (ending != null) {
            finish(ending)
            return
        }
        _state.value = _state.value.copy(
            phase = MatchPhase.EVENT,
            bannerEvent = drawn,
            revealed = true
        )
        publish()
    }

    private fun outcomeFor(state: MatchState, forfeited: Boolean): MatchOutcome? {
        val playerHeld = state.districts.count { it.owner == Owner.PLAYER && !it.burned }
        val rivalHeld = state.districts.count { it.owner == Owner.RIVAL && !it.burned }
        if (playerHeld == 0) return if (forfeited) MatchOutcome.FORFEIT else MatchOutcome.LOSS
        if (rivalHeld == 0) return MatchOutcome.WIN
        val challenge = request?.challenge
        if (challenge == ChallengeId.HOLD_THE_CATHEDRAL &&
            state.cathedralStreak >= GameConfig.CATHEDRAL_STREAK_TARGET
        ) {
            return MatchOutcome.WIN
        }
        val limit = if (challenge == ChallengeId.ONE_GOLDEN_HOUR) {
            GameConfig.GOLDEN_HOUR_TURNS
        } else {
            GameConfig.TOTAL_TURNS
        }
        if (state.turn >= limit) {
            return when {
                state.playerInfluence > state.rivalInfluence -> MatchOutcome.WIN
                state.playerInfluence < state.rivalInfluence -> MatchOutcome.LOSS
                else -> MatchOutcome.DRAW
            }
        }
        return null
    }

    private fun endTurn() {
        val state = match ?: return
        if (_state.value.paused) return
        val next = state.copy(
            turn = state.turn + 1,
            activeEvent = pendingEvent,
            playerOrders = emptyList(),
            rivalOrders = emptyList()
        )
        match = next
        pendingEvent = null
        _state.value = _state.value.copy(
            bannerEvent = null,
            revealed = false,
            spentFigures = emptySet(),
            phase = MatchPhase.ORDERS
        )
        beginTurn()
    }

    private fun beginTurn() {
        var state = match ?: return
        val faction = request?.faction ?: Faction.CRIMSON
        state = state.copy(
            playerFavor = collectFavor(
                state.districts,
                Owner.PLAYER,
                faction,
                state.activeEvent,
                state.playerFavor
            ),
            rivalFavor = collectFavor(
                state.districts,
                Owner.RIVAL,
                null,
                state.activeEvent,
                state.rivalFavor
            ),
            playerOrders = emptyList(),
            phase = MatchPhase.ORDERS
        )
        val plan = ServiceLocator.rivalCourtAi(
            districts = state.districts,
            favor = state.rivalFavor,
            difficulty = options.difficulty,
            roster = SampleData.roster,
            activeEvent = state.activeEvent,
            random = random
        )
        rivalUnmaskChance = plan.unmaskChance
        state = state.copy(
            rivalOrders = plan.orders,
            rivalFavor = (state.rivalFavor - plan.favorSpent).coerceAtLeast(0)
        )
        match = state
        publish()
    }

    private fun finish(outcome: MatchOutcome) {
        val state = match ?: return
        val incoming = request ?: return
        val sealedOff = ServiceLocator.evaluateChallenge(incoming.challenge, state, outcome)
        val held = state.districts.count { it.owner == Owner.PLAYER && !it.burned }
        progressRepository.record(
            MatchResultEntry(
                outcome = outcome,
                influence = state.playerInfluence,
                decoysCalled = state.decoysCalled,
                mapIndex = incoming.mapIndex,
                challenge = incoming.challenge,
                challengeSealed = sealedOff
            )
        )
        val recordId = replayRepository.nextId()
        replayRepository.add(
            ReplayRecord(
                id = recordId,
                seed = state.seed,
                mapLabel = labelOf(incoming),
                outcome = outcome,
                playerInfluence = state.playerInfluence,
                rivalInfluence = state.rivalInfluence,
                turns = state.turns,
                takeaway = ServiceLocator.buildTakeaway(state)
            )
        )
        match = state.copy(phase = MatchPhase.FINISHED, outcome = outcome)
        _state.value = _state.value.copy(phase = MatchPhase.FINISHED, ctaEnabled = false)
        viewModelScope.launch {
            eventChannel.send(
                GameEvent.Finished(
                    MatchSummary(
                        outcome = outcome,
                        playerInfluence = state.playerInfluence,
                        rivalInfluence = state.rivalInfluence,
                        districtsHeld = held,
                        decoysCalled = state.decoysCalled,
                        replayId = recordId,
                        request = incoming,
                        challengeSealed = sealedOff
                    )
                )
            )
        }
    }

    private fun labelOf(incoming: MatchRequest): String {
        val challenge = incoming.challenge
        if (challenge != null) {
            return SampleData.challenges.first { it.id == challenge }.name
        }
        return SampleData.campaignMaps.getOrElse(incoming.mapIndex) {
            SampleData.campaignMaps.first()
        }.name
    }

    private fun summaryOf(marker: OrderMarker?, districts: List<District>): OrderSummary {
        if (marker == null) return OrderSummary()
        return OrderSummary(
            kind = marker.kind,
            figure = marker.figure,
            districtCode = districts.getOrNull(marker.districtIndex)?.code.orEmpty()
        )
    }

    private fun decoyPrice(current: GameUiState): Int = decoyCost(
        current.activeEvent,
        (request?.faction ?: Faction.CRIMSON) == Faction.CRIMSON,
        current.turn
    )

    private fun publish() {
        val state = match ?: return
        val current = _state.value
        val ordersLeft = GameConfig.ORDERS_PER_TURN - state.playerOrders.size
        val price = decoyCost(
            state.activeEvent,
            (request?.faction ?: Faction.CRIMSON) == Faction.CRIMSON,
            state.turn
        )
        val unmaskPrice = unmaskCost(state.activeEvent)
        val selection = current.selectedDistrict
        val canDeploy = current.selectedFigure != null &&
            selection != null &&
            ordersLeft > 0 &&
            state.playerOrders.none { it.districtIndex == selection }
        val cta = when (current.phase) {
            MatchPhase.EVENT -> CtaLabel.END_TURN
            MatchPhase.RESOLVING -> CtaLabel.FLIP_ORDERS
            MatchPhase.FINISHED -> CtaLabel.END_TURN
            MatchPhase.ORDERS -> if (canDeploy) CtaLabel.DEPLOY else CtaLabel.FLIP_ORDERS
        }
        val hint = when {
            current.phase == MatchPhase.EVENT -> GameHint.NEXT_TURN
            current.phase == MatchPhase.RESOLVING -> GameHint.FLIP_ORDERS
            canDeploy -> GameHint.SEAL_ORDER
            state.playerOrders.isNotEmpty() -> GameHint.FLIP_ORDERS
            else -> GameHint.PICK_FIGURE
        }
        val threatened = state.districts
            .filter { it.owner == Owner.RIVAL && !it.burned }
            .flatMap { neighbours(it.index) }
            .filter { state.districts[it].owner != Owner.RIVAL && !state.districts[it].burned }
            .toSet()
        _state.value = current.copy(
            ready = true,
            districts = state.districts,
            turn = state.turn,
            phase = current.phase,
            seed = state.seed,
            favor = state.playerFavor,
            influence = state.playerInfluence,
            rivalInfluence = state.rivalInfluence,
            playerOrders = state.playerOrders,
            rivalOrders = state.rivalOrders,
            activeEvent = state.activeEvent,
            threatened = threatened,
            chainCells = chainsOf(state.districts, Owner.PLAYER).flatten().toSet(),
            cta = cta,
            ctaEnabled = current.phase != MatchPhase.RESOLVING &&
                current.phase != MatchPhase.FINISHED &&
                !current.paused,
            canDecoy = current.phase == MatchPhase.ORDERS &&
                selection != null &&
                ordersLeft > 0 &&
                price <= state.playerFavor &&
                state.playerOrders.none { it.districtIndex == selection },
            canUnmask = current.phase == MatchPhase.ORDERS &&
                selection != null &&
                state.rivalOrders.any { it.districtIndex == selection } &&
                unmaskPrice <= state.playerFavor,
            decoyPrice = price,
            unmaskPrice = unmaskPrice,
            hint = hint,
            finalTurn = state.turn >= GameConfig.TOTAL_TURNS
        )
    }
}
