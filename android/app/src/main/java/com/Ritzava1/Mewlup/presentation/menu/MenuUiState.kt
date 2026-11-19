package com.Ritzava1.Mewlup.presentation.menu

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.AppSettings
import com.Ritzava1.Mewlup.domain.model.Difficulty
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.model.MatchRequest

data class MenuUiState(
    val bestInfluence: Int = 0,
    val courtsWon: Int = 0,
    val decoysCalled: Int = 0,
    val clearedMaps: Int = 0,
    val totalMaps: Int = GameConfig.CAMPAIGN_MAPS,
    val sealedChallenges: Int = 0,
    val totalChallenges: Int = 0,
    val storedMatches: Int = 0,
    val settings: AppSettings = AppSettings(),
    val settingsOpen: Boolean = false,
    val quickMatch: MatchRequest = MatchRequest(0, 1, Faction.CRIMSON, Difficulty.MARSHAL)
) {
    val statCount: Int
        get() = listOf(bestInfluence, courtsWon, decoysCalled).count { it > 0 }
}
