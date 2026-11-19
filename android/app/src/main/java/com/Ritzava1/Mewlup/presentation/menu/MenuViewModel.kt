package com.Ritzava1.Mewlup.presentation.menu

import androidx.lifecycle.ViewModel
import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.data.sample.SampleData
import com.Ritzava1.Mewlup.domain.model.AppSettings
import com.Ritzava1.Mewlup.domain.model.Difficulty
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.repository.ProgressRepository
import com.Ritzava1.Mewlup.domain.repository.ReplayRepository
import com.Ritzava1.Mewlup.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val replayRepository: ReplayRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.progress()
        val options = settingsRepository.settings()
        val nextMap = (0 until GameConfig.CAMPAIGN_MAPS)
            .firstOrNull { it !in progress.clearedMaps } ?: 0
        _state.update {
            it.copy(
                bestInfluence = progress.bestInfluence,
                courtsWon = progress.courtsWon,
                decoysCalled = progress.decoysCalled,
                clearedMaps = progress.clearedMaps.size,
                sealedChallenges = progress.sealedChallenges.size,
                totalChallenges = SampleData.challenges.size,
                storedMatches = replayRepository.records().size,
                settings = options,
                quickMatch = MatchRequest(
                    mapIndex = nextMap,
                    attempt = progress.matchesPlayed + 1,
                    faction = options.faction,
                    difficulty = options.difficulty
                )
            )
        }
    }

    fun openSettings() {
        _state.update { it.copy(settingsOpen = true) }
    }

    fun closeSettings() {
        _state.update { it.copy(settingsOpen = false) }
    }

    fun applySettings(settings: AppSettings) {
        settingsRepository.update(settings)
        _state.update { it.copy(settings = settings) }
    }

    fun selectDifficulty(difficulty: Difficulty) {
        applySettings(_state.value.settings.copy(difficulty = difficulty))
    }

    fun toggleThreatOverlay() {
        val current = _state.value.settings
        applySettings(current.copy(threatOverlay = !current.threatOverlay))
    }

    fun toggleConfirmEndTurn() {
        val current = _state.value.settings
        applySettings(current.copy(confirmEndTurn = !current.confirmEndTurn))
    }

    fun toggleReduceMotion() {
        val current = _state.value.settings
        applySettings(current.copy(reduceMotion = !current.reduceMotion))
    }
}
