package com.Ritzava1.Mewlup.domain.repository

import com.Ritzava1.Mewlup.domain.model.AppSettings
import com.Ritzava1.Mewlup.domain.model.MatchResultEntry
import com.Ritzava1.Mewlup.domain.model.PlayerProgress
import com.Ritzava1.Mewlup.domain.model.ReplayRecord

interface ProgressRepository {
    fun progress(): PlayerProgress
    fun record(entry: MatchResultEntry)
}

interface SettingsRepository {
    fun settings(): AppSettings
    fun update(settings: AppSettings)
}

interface ReplayRepository {
    fun records(): List<ReplayRecord>
    fun add(record: ReplayRecord)
    fun nextId(): Long
}
