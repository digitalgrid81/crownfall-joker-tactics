package com.Ritzava1.Mewlup.domain.model

import com.Ritzava1.Mewlup.core.config.GameConfig

enum class EventCard { CURFEW, MASQUERADE_BALL, BREAD_RIOT, ROYAL_PARADE, TREASON, OPEN_GATES }

enum class MatchPhase { ORDERS, RESOLVING, EVENT, FINISHED }

enum class MatchOutcome { WIN, LOSS, DRAW, FORFEIT }

enum class Difficulty { COURTIER, MARSHAL, REGENT }

enum class ChallengeId { SILENT_COURT, HOLD_THE_CATHEDRAL, NO_JESTERS, ONE_GOLDEN_HOUR }

data class MatchRequest(
    val mapIndex: Int,
    val attempt: Int,
    val faction: Faction,
    val difficulty: Difficulty,
    val challenge: ChallengeId? = null
)

fun seedOf(request: MatchRequest): Long {
    val challenge = request.challenge
    if (challenge != null) {
        return (challenge.ordinal + 1) * GameConfig.SEED_PRIME_MAP
    }
    return (request.mapIndex + 1) * GameConfig.SEED_PRIME_MAP xor
        (request.attempt * GameConfig.SEED_PRIME_ATTEMPT)
}

data class OrderSummary(
    val kind: OrderKind? = null,
    val figure: FigureType? = null,
    val districtCode: String = ""
)

data class ReplayTurn(
    val turn: Int,
    val event: EventCard?,
    val player: OrderSummary,
    val rival: OrderSummary,
    val playerDelta: Int,
    val rivalDelta: Int,
    val decoyCalled: Boolean
)

enum class TakeawayKind { DECOYS_CALLED, NO_CHAIN, EVENT_COST, SOLID_HOLD }

data class Takeaway(val kind: TakeawayKind, val value: Int)

data class ReplayRecord(
    val id: Long,
    val seed: Long,
    val mapLabel: String,
    val outcome: MatchOutcome,
    val playerInfluence: Int,
    val rivalInfluence: Int,
    val turns: List<ReplayTurn>,
    val takeaway: Takeaway
)

data class MatchSummary(
    val outcome: MatchOutcome,
    val playerInfluence: Int,
    val rivalInfluence: Int,
    val districtsHeld: Int,
    val decoysCalled: Int,
    val replayId: Long,
    val request: MatchRequest,
    val challengeSealed: Boolean
)

data class MatchState(
    val districts: List<District>,
    val seed: Long,
    val turn: Int = 1,
    val phase: MatchPhase = MatchPhase.ORDERS,
    val playerFavor: Int = GameConfig.START_FAVOR,
    val rivalFavor: Int = GameConfig.START_FAVOR,
    val playerInfluence: Int = 0,
    val rivalInfluence: Int = 0,
    val playerOrders: List<OrderMarker> = emptyList(),
    val rivalOrders: List<OrderMarker> = emptyList(),
    val activeEvent: EventCard? = null,
    val decoysCalled: Int = 0,
    val decoysPlaced: Int = 0,
    val jokerDeployed: Boolean = false,
    val cathedralStreak: Int = 0,
    val playerHome: Int = 0,
    val rivalHome: Int = 0,
    val outcome: MatchOutcome? = null,
    val turns: List<ReplayTurn> = emptyList()
)
