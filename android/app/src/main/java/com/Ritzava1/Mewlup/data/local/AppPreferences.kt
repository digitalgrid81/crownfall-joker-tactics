package com.Ritzava1.Mewlup.data.local

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val store: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = store.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        store.edit().putInt(key, value).apply()
    }

    fun readLong(key: String, fallback: Long): Long = store.getLong(key, fallback)

    fun writeLong(key: String, value: Long) {
        store.edit().putLong(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = store.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        store.edit().putBoolean(key, value).apply()
    }

    fun readText(key: String, fallback: String): String = store.getString(key, fallback) ?: fallback

    fun writeText(key: String, value: String) {
        store.edit().putString(key, value).apply()
    }

    companion object {
        private const val STORE_NAME = "crownfall_court"
        const val KEY_BEST_INFLUENCE = "best_influence"
        const val KEY_COURTS_WON = "courts_won"
        const val KEY_DECOYS_CALLED = "decoys_called"
        const val KEY_MATCHES_PLAYED = "matches_played"
        const val KEY_CLEARED_MAPS = "cleared_maps"
        const val KEY_SEALED_CHALLENGES = "sealed_challenges"
        const val KEY_DIFFICULTY = "difficulty"
        const val KEY_THREAT_OVERLAY = "threat_overlay"
        const val KEY_CONFIRM_END_TURN = "confirm_end_turn"
        const val KEY_REDUCE_MOTION = "reduce_motion"
        const val KEY_FACTION = "faction"
        const val KEY_REPLAYS = "replays"
        const val KEY_REPLAY_SEQUENCE = "replay_sequence"
    }
}
