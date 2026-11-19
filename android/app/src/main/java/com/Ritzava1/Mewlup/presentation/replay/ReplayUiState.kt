package com.Ritzava1.Mewlup.presentation.replay

import com.Ritzava1.Mewlup.domain.model.ReplayRecord

data class ReplayUiState(
    val ready: Boolean = false,
    val records: List<ReplayRecord> = emptyList(),
    val selectedId: Long = 0L
) {
    val selected: ReplayRecord?
        get() = records.firstOrNull { it.id == selectedId } ?: records.firstOrNull()

    val isEmpty: Boolean
        get() = records.isEmpty()
}
