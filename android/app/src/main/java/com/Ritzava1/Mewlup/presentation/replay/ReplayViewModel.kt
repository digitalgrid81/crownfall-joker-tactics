package com.Ritzava1.Mewlup.presentation.replay

import androidx.lifecycle.ViewModel
import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.repository.ProgressRepository
import com.Ritzava1.Mewlup.domain.repository.ReplayRepository
import com.Ritzava1.Mewlup.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReplayViewModel(
    private val replayRepository: ReplayRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReplayUiState())
    val state: StateFlow<ReplayUiState> = _state.asStateFlow()

    private var loadedFor: Long? = null

    fun prepare(recordId: Long) {
        if (loadedFor == recordId) return
        loadedFor = recordId
        val records = replayRepository.records()
        val selected = records.firstOrNull { it.id == recordId }?.id
            ?: records.firstOrNull()?.id
            ?: 0L
        _state.update {
            it.copy(ready = true, records = records, selectedId = selected)
        }
    }

    fun select(recordId: Long) {
        _state.update { it.copy(selectedId = recordId) }
    }

    fun freshMatchRequest(): MatchRequest {
        val options = settingsRepository.settings()
        val progress = progressRepository.progress()
        val nextMap = (0 until GameConfig.CAMPAIGN_MAPS)
            .firstOrNull { it !in progress.clearedMaps } ?: 0
        return MatchRequest(
            mapIndex = nextMap,
            attempt = progress.matchesPlayed + 1,
            faction = options.faction,
            difficulty = options.difficulty
        )
    }
}
