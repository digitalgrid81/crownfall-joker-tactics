package com.Ritzava1.Mewlup.domain.usecase

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.BoardLayout
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.DistrictName
import com.Ritzava1.Mewlup.domain.model.Owner
import com.Ritzava1.Mewlup.domain.model.SpecialSite
import kotlin.math.abs
import kotlin.random.Random

class GenerateBoardUseCase {

    operator fun invoke(seed: Long, names: List<DistrictName>): BoardLayout {
        val random = Random(seed)
        var attempt = 0
        while (attempt < GameConfig.MAX_BOARD_ATTEMPTS) {
            val layout = build(random, names)
            if (isSolvable(layout)) return layout
            attempt += 1
        }
        return fallback(names)
    }

    private fun build(random: Random, names: List<DistrictName>): BoardLayout {
        val pool = names.shuffled(random).take(GameConfig.DISTRICT_COUNT)
        val values = IntArray(GameConfig.DISTRICT_COUNT) { random.nextInt(1, 4) }
        val sites = arrayOfNulls<SpecialSite>(GameConfig.DISTRICT_COUNT)
        val special = listOf(SpecialSite.CATHEDRAL, SpecialSite.TREASURY, SpecialSite.BARRACKS)
        val taken = mutableSetOf<Int>()
        for (site in special) {
            var spot = random.nextInt(GameConfig.DISTRICT_COUNT)
            while (spot in taken) spot = random.nextInt(GameConfig.DISTRICT_COUNT)
            taken.add(spot)
            sites[spot] = site
        }
        val playerHome = (GameConfig.GRID - 1) * GameConfig.GRID + random.nextInt(GameConfig.GRID)
        val rivalHome = random.nextInt(GameConfig.GRID)
        val districts = List(GameConfig.DISTRICT_COUNT) { index ->
            val name = pool.getOrElse(index) { DistrictName(FALLBACK_CODE, FALLBACK_NAME) }
            District(
                index = index,
                code = name.code,
                name = name.name,
                value = values[index],
                site = sites[index] ?: SpecialSite.NONE,
                owner = when (index) {
                    playerHome -> Owner.PLAYER
                    rivalHome -> Owner.RIVAL
                    else -> Owner.NEUTRAL
                }
            )
        }
        return BoardLayout(districts, playerHome, rivalHome, false)
    }

    private fun isSolvable(layout: BoardLayout): Boolean {
        if (layout.playerHome == layout.rivalHome) return false
        if (areAdjacent(layout.playerHome, layout.rivalHome)) return false
        val total = layout.districts.sumOf { it.value }
        if (total < GameConfig.MIN_BOARD_INFLUENCE) return false
        if (total > GameConfig.MAX_BOARD_INFLUENCE) return false
        val rich = layout.districts.count { it.value >= 3 }
        if (rich < GameConfig.MIN_HIGH_VALUE_DISTRICTS) return false
        val homeSites = layout.districts.filter { it.site != SpecialSite.NONE }.map { it.index }
        if (layout.playerHome in homeSites || layout.rivalHome in homeSites) return false
        return isConnected(layout.districts)
    }

    private fun isConnected(districts: List<District>): Boolean {
        val open = districts.filter { !it.burned }.map { it.index }.toSet()
        if (open.isEmpty()) return false
        val seen = mutableSetOf(open.first())
        val queue = ArrayDeque<Int>()
        queue.addLast(open.first())
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (next in neighbours(current)) {
                if (next in open && seen.add(next)) queue.addLast(next)
            }
        }
        return seen.size == open.size
    }

    private fun fallback(names: List<DistrictName>): BoardLayout {
        val playerHome = GameConfig.DISTRICT_COUNT - 3
        val rivalHome = 2
        val districts = List(GameConfig.DISTRICT_COUNT) { index ->
            val name = names.getOrElse(index) { DistrictName(FALLBACK_CODE, FALLBACK_NAME) }
            District(
                index = index,
                code = name.code,
                name = name.name,
                value = FALLBACK_VALUES[index % FALLBACK_VALUES.size],
                site = when (index) {
                    12 -> SpecialSite.CATHEDRAL
                    6 -> SpecialSite.TREASURY
                    18 -> SpecialSite.BARRACKS
                    else -> SpecialSite.NONE
                },
                owner = when (index) {
                    playerHome -> Owner.PLAYER
                    rivalHome -> Owner.RIVAL
                    else -> Owner.NEUTRAL
                }
            )
        }
        return BoardLayout(districts, playerHome, rivalHome, true)
    }

    private companion object {
        const val FALLBACK_CODE = "CPT"
        const val FALLBACK_NAME = "CAPITAL WARD"
        val FALLBACK_VALUES = intArrayOf(1, 2, 3, 2, 1)
    }
}

fun neighbours(index: Int): List<Int> {
    val row = index / GameConfig.GRID
    val column = index % GameConfig.GRID
    val result = mutableListOf<Int>()
    if (row > 0) result.add(index - GameConfig.GRID)
    if (row < GameConfig.GRID - 1) result.add(index + GameConfig.GRID)
    if (column > 0) result.add(index - 1)
    if (column < GameConfig.GRID - 1) result.add(index + 1)
    return result
}

fun areAdjacent(first: Int, second: Int): Boolean {
    val rowGap = abs(first / GameConfig.GRID - second / GameConfig.GRID)
    val columnGap = abs(first % GameConfig.GRID - second % GameConfig.GRID)
    return rowGap + columnGap == 1
}

fun isInnerDistrict(index: Int): Boolean {
    val row = index / GameConfig.GRID
    val column = index % GameConfig.GRID
    return row in 1..GameConfig.GRID - 2 && column in 1..GameConfig.GRID - 2
}
