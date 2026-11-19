package com.Ritzava1.Mewlup.data.repository

import com.Ritzava1.Mewlup.data.local.AppPreferences
import com.Ritzava1.Mewlup.domain.model.ChallengeId
import com.Ritzava1.Mewlup.domain.model.MatchOutcome
import com.Ritzava1.Mewlup.domain.model.MatchResultEntry
import com.Ritzava1.Mewlup.domain.model.PlayerProgress
import com.Ritzava1.Mewlup.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val preferences: AppPreferences) : ProgressRepository {

    override fun progress(): PlayerProgress = PlayerProgress(
        bestInfluence = preferences.readInt(AppPreferences.KEY_BEST_INFLUENCE, 0),
        courtsWon = preferences.readInt(AppPreferences.KEY_COURTS_WON, 0),
        decoysCalled = preferences.readInt(AppPreferences.KEY_DECOYS_CALLED, 0),
        matchesPlayed = preferences.readInt(AppPreferences.KEY_MATCHES_PLAYED, 0),
        clearedMaps = decodeInts(preferences.readText(AppPreferences.KEY_CLEARED_MAPS, "")),
        sealedChallenges = decodeChallenges(
            preferences.readText(AppPreferences.KEY_SEALED_CHALLENGES, "")
        )
    )

    override fun record(entry: MatchResultEntry) {
        val current = progress()
        preferences.writeInt(AppPreferences.KEY_MATCHES_PLAYED, current.matchesPlayed + 1)
        if (entry.influence > current.bestInfluence) {
            preferences.writeInt(AppPreferences.KEY_BEST_INFLUENCE, entry.influence)
        }
        if (entry.outcome == MatchOutcome.WIN) {
            preferences.writeInt(AppPreferences.KEY_COURTS_WON, current.courtsWon + 1)
            if (entry.challenge == null) {
                val cleared = current.clearedMaps + entry.mapIndex
                preferences.writeText(
                    AppPreferences.KEY_CLEARED_MAPS,
                    cleared.sorted().joinToString(SEPARATOR)
                )
            }
        }
        preferences.writeInt(
            AppPreferences.KEY_DECOYS_CALLED,
            current.decoysCalled + entry.decoysCalled
        )
        val challenge = entry.challenge
        if (challenge != null && entry.challengeSealed) {
            val sealedSet = current.sealedChallenges + challenge
            preferences.writeText(
                AppPreferences.KEY_SEALED_CHALLENGES,
                sealedSet.joinToString(SEPARATOR) { it.name }
            )
        }
    }

    private fun decodeInts(raw: String): Set<Int> = raw
        .split(SEPARATOR)
        .mapNotNull { it.trim().toIntOrNull() }
        .toSet()

    private fun decodeChallenges(raw: String): Set<ChallengeId> = raw
        .split(SEPARATOR)
        .mapNotNull { token -> ChallengeId.entries.firstOrNull { it.name == token.trim() } }
        .toSet()

    private companion object {
        const val SEPARATOR = ","
    }
}
