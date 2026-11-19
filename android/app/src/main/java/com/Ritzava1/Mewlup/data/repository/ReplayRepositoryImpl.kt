package com.Ritzava1.Mewlup.data.repository

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.data.local.AppPreferences
import com.Ritzava1.Mewlup.domain.model.EventCard
import com.Ritzava1.Mewlup.domain.model.FigureType
import com.Ritzava1.Mewlup.domain.model.MatchOutcome
import com.Ritzava1.Mewlup.domain.model.OrderKind
import com.Ritzava1.Mewlup.domain.model.OrderSummary
import com.Ritzava1.Mewlup.domain.model.ReplayRecord
import com.Ritzava1.Mewlup.domain.model.ReplayTurn
import com.Ritzava1.Mewlup.domain.model.Takeaway
import com.Ritzava1.Mewlup.domain.model.TakeawayKind
import com.Ritzava1.Mewlup.domain.repository.ReplayRepository

class ReplayRepositoryImpl(private val preferences: AppPreferences) : ReplayRepository {

    override fun records(): List<ReplayRecord> = preferences
        .readText(AppPreferences.KEY_REPLAYS, "")
        .split(RECORD_SEPARATOR)
        .filter { it.isNotBlank() }
        .mapNotNull { decodeRecord(it) }
        .sortedByDescending { it.id }

    override fun add(record: ReplayRecord) {
        val kept = (listOf(record) + records())
            .distinctBy { it.id }
            .sortedByDescending { it.id }
            .take(GameConfig.REPLAY_LIMIT)
        preferences.writeText(
            AppPreferences.KEY_REPLAYS,
            kept.joinToString(RECORD_SEPARATOR) { encodeRecord(it) }
        )
    }

    override fun nextId(): Long {
        val next = preferences.readLong(AppPreferences.KEY_REPLAY_SEQUENCE, 0L) + 1L
        preferences.writeLong(AppPreferences.KEY_REPLAY_SEQUENCE, next)
        return next
    }

    private fun encodeRecord(record: ReplayRecord): String = listOf(
        record.id.toString(),
        record.seed.toString(),
        record.mapLabel,
        record.outcome.name,
        record.playerInfluence.toString(),
        record.rivalInfluence.toString(),
        record.takeaway.kind.name,
        record.takeaway.value.toString(),
        record.turns.joinToString(TURN_SEPARATOR) { encodeTurn(it) }
    ).joinToString(FIELD_SEPARATOR)

    private fun encodeTurn(turn: ReplayTurn): String = listOf(
        turn.turn.toString(),
        turn.event?.name ?: NONE,
        turn.player.kind?.name ?: NONE,
        turn.player.figure?.name ?: NONE,
        turn.player.districtCode.ifBlank { NONE },
        turn.rival.kind?.name ?: NONE,
        turn.rival.figure?.name ?: NONE,
        turn.rival.districtCode.ifBlank { NONE },
        turn.playerDelta.toString(),
        turn.rivalDelta.toString(),
        if (turn.decoyCalled) "1" else "0"
    ).joinToString(CELL_SEPARATOR)

    private fun decodeRecord(raw: String): ReplayRecord? {
        val parts = raw.split(FIELD_SEPARATOR)
        if (parts.size < 9) return null
        val id = parts[0].toLongOrNull() ?: return null
        val outcome = MatchOutcome.entries.firstOrNull { it.name == parts[3] } ?: return null
        val kind = TakeawayKind.entries.firstOrNull { it.name == parts[6] } ?: TakeawayKind.SOLID_HOLD
        return ReplayRecord(
            id = id,
            seed = parts[1].toLongOrNull() ?: 0L,
            mapLabel = parts[2],
            outcome = outcome,
            playerInfluence = parts[4].toIntOrNull() ?: 0,
            rivalInfluence = parts[5].toIntOrNull() ?: 0,
            takeaway = Takeaway(kind, parts[7].toIntOrNull() ?: 0),
            turns = parts[8]
                .split(TURN_SEPARATOR)
                .filter { it.isNotBlank() }
                .mapNotNull { decodeTurn(it) }
        )
    }

    private fun decodeTurn(raw: String): ReplayTurn? {
        val cells = raw.split(CELL_SEPARATOR)
        if (cells.size < 11) return null
        return ReplayTurn(
            turn = cells[0].toIntOrNull() ?: return null,
            event = EventCard.entries.firstOrNull { it.name == cells[1] },
            player = OrderSummary(
                kind = OrderKind.entries.firstOrNull { it.name == cells[2] },
                figure = FigureType.entries.firstOrNull { it.name == cells[3] },
                districtCode = if (cells[4] == NONE) "" else cells[4]
            ),
            rival = OrderSummary(
                kind = OrderKind.entries.firstOrNull { it.name == cells[5] },
                figure = FigureType.entries.firstOrNull { it.name == cells[6] },
                districtCode = if (cells[7] == NONE) "" else cells[7]
            ),
            playerDelta = cells[8].toIntOrNull() ?: 0,
            rivalDelta = cells[9].toIntOrNull() ?: 0,
            decoyCalled = cells[10] == "1"
        )
    }

    private companion object {
        const val RECORD_SEPARATOR = "\n"
        const val FIELD_SEPARATOR = "|"
        const val TURN_SEPARATOR = ";"
        const val CELL_SEPARATOR = ","
        const val NONE = "-"
    }
}
