package com.Ritzava1.Mewlup.presentation.game

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.ChallengeId
import com.Ritzava1.Mewlup.domain.model.CourtFigure
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.EventCard
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.model.FigureType
import com.Ritzava1.Mewlup.domain.model.MatchPhase
import com.Ritzava1.Mewlup.domain.model.MatchSummary
import com.Ritzava1.Mewlup.domain.model.OrderMarker

enum class GameHint { PICK_FIGURE, SEAL_ORDER, FLIP_ORDERS, NEXT_TURN }

enum class CtaLabel { DEPLOY, FLIP_ORDERS, END_TURN }

sealed interface GameEvent {
    data class Finished(val summary: MatchSummary) : GameEvent
}

data class GameUiState(
    val ready: Boolean = false,
    val districts: List<District> = emptyList(),
    val turn: Int = 1,
    val totalTurns: Int = GameConfig.TOTAL_TURNS,
    val phase: MatchPhase = MatchPhase.ORDERS,
    val seed: Long = 0L,
    val faction: Faction = Faction.CRIMSON,
    val favor: Int = GameConfig.START_FAVOR,
    val influence: Int = 0,
    val rivalInfluence: Int = 0,
    val roster: List<CourtFigure> = emptyList(),
    val spentFigures: Set<FigureType> = emptySet(),
    val selectedFigure: FigureType? = null,
    val selectedDistrict: Int? = null,
    val playerOrders: List<OrderMarker> = emptyList(),
    val rivalOrders: List<OrderMarker> = emptyList(),
    val revealed: Boolean = false,
    val threatened: Set<Int> = emptySet(),
    val chainCells: Set<Int> = emptySet(),
    val activeEvent: EventCard? = null,
    val bannerEvent: EventCard? = null,
    val hint: GameHint = GameHint.PICK_FIGURE,
    val cta: CtaLabel = CtaLabel.DEPLOY,
    val ctaEnabled: Boolean = false,
    val canDecoy: Boolean = false,
    val canUnmask: Boolean = false,
    val decoyPrice: Int = GameConfig.DECOY_COST,
    val unmaskPrice: Int = GameConfig.UNMASK_COST,
    val paused: Boolean = false,
    val confirmVisible: Boolean = false,
    val threatOverlay: Boolean = true,
    val reduceMotion: Boolean = false,
    val challenge: ChallengeId? = null,
    val finalTurn: Boolean = false
)
