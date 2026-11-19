package com.Ritzava1.Mewlup.presentation.campaign

import com.Ritzava1.Mewlup.domain.model.CampaignMap
import com.Ritzava1.Mewlup.domain.model.ChallengeId
import com.Ritzava1.Mewlup.domain.model.ChallengeScenario
import com.Ritzava1.Mewlup.domain.model.Faction

data class CampaignMapCard(
    val map: CampaignMap,
    val cleared: Boolean,
    val locked: Boolean,
    val seed: Long
)

data class ChallengeCard(
    val scenario: ChallengeScenario,
    val sealedOff: Boolean
)

data class CampaignUiState(
    val challengeTab: Boolean = false,
    val maps: List<CampaignMapCard> = emptyList(),
    val challenges: List<ChallengeCard> = emptyList(),
    val selectedMap: Int = 0,
    val selectedChallenge: ChallengeId? = null,
    val faction: Faction = Faction.CRIMSON,
    val startEnabled: Boolean = true,
    val usedFallbackList: Boolean = false
)
