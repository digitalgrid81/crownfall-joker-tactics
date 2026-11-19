package com.Ritzava1.Mewlup.presentation.rules

import androidx.lifecycle.ViewModel
import com.Ritzava1.Mewlup.data.sample.SampleData
import com.Ritzava1.Mewlup.domain.model.CourtFigure
import com.Ritzava1.Mewlup.domain.model.EventCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RulesUiState(
    val roster: List<CourtFigure> = emptyList(),
    val events: List<EventCard> = emptyList()
)

class RulesViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        RulesUiState(
            roster = SampleData.roster,
            events = EventCard.entries.toList()
        )
    )
    val state: StateFlow<RulesUiState> = _state.asStateFlow()
}
