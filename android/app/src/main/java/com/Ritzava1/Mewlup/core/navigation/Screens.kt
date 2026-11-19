package com.Ritzava1.Mewlup.core.navigation

import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.model.MatchSummary

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Campaign(val challengeTab: Boolean) : Screen
    data class Game(val request: MatchRequest) : Screen
    data class GameOver(val summary: MatchSummary) : Screen
    data class Replay(val recordId: Long) : Screen
    data object Rules : Screen
}
