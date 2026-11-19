package com.Ritzava1.Mewlup.domain.usecase

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.ChallengeId
import com.Ritzava1.Mewlup.domain.model.MatchOutcome
import com.Ritzava1.Mewlup.domain.model.MatchState

class EvaluateChallengeUseCase {

    operator fun invoke(challenge: ChallengeId?, state: MatchState, outcome: MatchOutcome): Boolean {
        if (challenge == null) return false
        return when (challenge) {
            ChallengeId.SILENT_COURT ->
                state.playerInfluence - state.rivalInfluence >= GameConfig.SILENT_COURT_MARGIN

            ChallengeId.HOLD_THE_CATHEDRAL ->
                state.cathedralStreak >= GameConfig.CATHEDRAL_STREAK_TARGET

            ChallengeId.NO_JESTERS ->
                outcome == MatchOutcome.WIN && !state.jokerDeployed

            ChallengeId.ONE_GOLDEN_HOUR ->
                outcome == MatchOutcome.WIN && state.turn <= GameConfig.GOLDEN_HOUR_TURNS
        }
    }
}
