package com.Ritzava1.Mewlup.presentation.campaign

import androidx.lifecycle.ViewModel
import com.Ritzava1.Mewlup.data.sample.SampleData
import com.Ritzava1.Mewlup.domain.model.ChallengeId
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.model.seedOf
import com.Ritzava1.Mewlup.domain.repository.ProgressRepository
import com.Ritzava1.Mewlup.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CampaignViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CampaignUiState())
    val state: StateFlow<CampaignUiState> = _state.asStateFlow()

    private var initialised = false

    fun prepare(challengeTab: Boolean) {
        if (initialised) return
        initialised = true
        _state.update { it.copy(challengeTab = challengeTab) }
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.progress()
        val options = settingsRepository.settings()
        val maps = SampleData.campaignMaps.map { map ->
            CampaignMapCard(
                map = map,
                cleared = map.index in progress.clearedMaps,
                locked = map.index > 0 && (map.index - 1) !in progress.clearedMaps,
                seed = seedOf(
                    MatchRequest(map.index, 1, options.faction, options.difficulty)
                )
            )
        }
        val firstOpen = maps.firstOrNull { !it.locked }?.map?.index ?: 0
        val challenges = SampleData.challenges.map { scenario ->
            ChallengeCard(scenario, scenario.id in progress.sealedChallenges)
        }
        _state.update { current ->
            val selected = if (maps.getOrNull(current.selectedMap)?.locked != false) {
                firstOpen
            } else {
                current.selectedMap
            }
            current.copy(
                maps = maps,
                challenges = challenges,
                selectedMap = selected,
                selectedChallenge = current.selectedChallenge ?: challenges.firstOrNull()?.scenario?.id,
                faction = options.faction,
                startEnabled = maps.getOrNull(selected)?.locked != true,
                usedFallbackList = maps.isEmpty()
            )
        }
    }

    fun selectTab(challengeTab: Boolean) {
        _state.update { it.copy(challengeTab = challengeTab, startEnabled = true) }
        if (!challengeTab) refreshStartState()
    }

    fun selectMap(index: Int) {
        val card = _state.value.maps.getOrNull(index) ?: return
        if (card.locked) return
        _state.update { it.copy(selectedMap = index, startEnabled = true) }
    }

    fun selectChallenge(id: ChallengeId) {
        _state.update { it.copy(selectedChallenge = id, startEnabled = true) }
    }

    fun selectFaction(faction: Faction) {
        val options = settingsRepository.settings().copy(faction = faction)
        settingsRepository.update(options)
        _state.update { it.copy(faction = faction) }
    }

    fun buildRequest(): MatchRequest {
        val current = _state.value
        val options = settingsRepository.settings()
        val progress = progressRepository.progress()
        return if (current.challengeTab) {
            MatchRequest(
                mapIndex = 0,
                attempt = progress.matchesPlayed + 1,
                faction = current.faction,
                difficulty = options.difficulty,
                challenge = current.selectedChallenge
            )
        } else {
            MatchRequest(
                mapIndex = current.selectedMap,
                attempt = progress.matchesPlayed + 1,
                faction = current.faction,
                difficulty = options.difficulty
            )
        }
    }

    private fun refreshStartState() {
        val current = _state.value
        _state.update {
            it.copy(startEnabled = current.maps.getOrNull(current.selectedMap)?.locked != true)
        }
    }
}
