package com.Ritzava1.Mewlup.domain.model

data class CampaignMap(
    val index: Int,
    val name: String,
    val brief: String
)

data class ChallengeScenario(
    val id: ChallengeId,
    val name: String,
    val rule: String,
    val target: String
)

data class AppSettings(
    val difficulty: Difficulty = Difficulty.MARSHAL,
    val threatOverlay: Boolean = true,
    val confirmEndTurn: Boolean = false,
    val reduceMotion: Boolean = false,
    val faction: Faction = Faction.CRIMSON
)

data class PlayerProgress(
    val bestInfluence: Int = 0,
    val courtsWon: Int = 0,
    val decoysCalled: Int = 0,
    val matchesPlayed: Int = 0,
    val clearedMaps: Set<Int> = emptySet(),
    val sealedChallenges: Set<ChallengeId> = emptySet()
)

data class MatchResultEntry(
    val outcome: MatchOutcome,
    val influence: Int,
    val decoysCalled: Int,
    val mapIndex: Int,
    val challenge: ChallengeId?,
    val challengeSealed: Boolean
)
