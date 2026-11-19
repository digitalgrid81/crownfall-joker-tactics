package com.Ritzava1.Mewlup.data.repository

import com.Ritzava1.Mewlup.data.local.AppPreferences
import com.Ritzava1.Mewlup.domain.model.AppSettings
import com.Ritzava1.Mewlup.domain.model.Difficulty
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: AppPreferences) : SettingsRepository {

    override fun settings(): AppSettings {
        val fallback = AppSettings()
        return AppSettings(
            difficulty = Difficulty.entries.getOrElse(
                preferences.readInt(AppPreferences.KEY_DIFFICULTY, fallback.difficulty.ordinal)
            ) { fallback.difficulty },
            threatOverlay = preferences.readBoolean(
                AppPreferences.KEY_THREAT_OVERLAY,
                fallback.threatOverlay
            ),
            confirmEndTurn = preferences.readBoolean(
                AppPreferences.KEY_CONFIRM_END_TURN,
                fallback.confirmEndTurn
            ),
            reduceMotion = preferences.readBoolean(
                AppPreferences.KEY_REDUCE_MOTION,
                fallback.reduceMotion
            ),
            faction = Faction.entries.getOrElse(
                preferences.readInt(AppPreferences.KEY_FACTION, fallback.faction.ordinal)
            ) { fallback.faction }
        )
    }

    override fun update(settings: AppSettings) {
        preferences.writeInt(AppPreferences.KEY_DIFFICULTY, settings.difficulty.ordinal)
        preferences.writeBoolean(AppPreferences.KEY_THREAT_OVERLAY, settings.threatOverlay)
        preferences.writeBoolean(AppPreferences.KEY_CONFIRM_END_TURN, settings.confirmEndTurn)
        preferences.writeBoolean(AppPreferences.KEY_REDUCE_MOTION, settings.reduceMotion)
        preferences.writeInt(AppPreferences.KEY_FACTION, settings.faction.ordinal)
    }
}
