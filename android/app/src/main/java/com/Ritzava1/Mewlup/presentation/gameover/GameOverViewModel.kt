package com.Ritzava1.Mewlup.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.model.MatchSummary
import com.Ritzava1.Mewlup.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameOverUiState(
    val ready: Boolean = false,
    val summary: MatchSummary? = null,
    val bestInfluence: Int = 0,
    val courtsWon: Int = 0,
    val totalDecoysCalled: Int = 0
)

class GameOverViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var loaded: MatchSummary? = null

    fun prepare(summary: MatchSummary) {
        if (loaded == summary) return
        loaded = summary
        val progress = progressRepository.progress()
        _state.update {
            it.copy(
                ready = true,
                summary = summary,
                bestInfluence = progress.bestInfluence,
                courtsWon = progress.courtsWon,
                totalDecoysCalled = progress.decoysCalled
            )
        }
    }

    fun rematchRequest(): MatchRequest? {
        val summary = _state.value.summary ?: return null
        return summary.request.copy(attempt = summary.request.attempt + 1)
    }
}
