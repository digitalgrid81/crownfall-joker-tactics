package com.Ritzava1.Mewlup.domain.usecase

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.EventCard
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.model.FigureType
import com.Ritzava1.Mewlup.domain.model.Owner
import com.Ritzava1.Mewlup.domain.model.SpecialSite

class ScoreInfluenceUseCase {

    operator fun invoke(
        districts: List<District>,
        owner: Owner,
        activeEvent: EventCard?
    ): Int {
        var total = 0
        for (district in districts) {
            if (district.owner != owner || district.burned) continue
            if (activeEvent == EventCard.CURFEW && isInnerDistrict(district.index)) continue
            total += district.value
            if (district.figure == FigureType.HERALD && district.figureOwner == owner) total += 1
        }
        total += chainsOf(districts, owner).size * GameConfig.CHAIN_FAVOR
        if (activeEvent == EventCard.ROYAL_PARADE) {
            val cathedral = districts.firstOrNull { it.site == SpecialSite.CATHEDRAL }
            if (cathedral != null && cathedral.owner == owner && !cathedral.burned) {
                total += GameConfig.PARADE_BONUS
            }
        }
        return total
    }
}

fun chainsOf(districts: List<District>, owner: Owner): List<List<Int>> {
    val held = districts.filter { it.owner == owner && !it.burned }.map { it.index }.toSet()
    val result = mutableListOf<List<Int>>()
    for (row in 0 until GameConfig.GRID) {
        for (column in 0..GameConfig.GRID - GameConfig.CHAIN_LENGTH) {
            val line = (0 until GameConfig.CHAIN_LENGTH).map { row * GameConfig.GRID + column + it }
            if (line.all { it in held }) result.add(line)
        }
    }
    for (column in 0 until GameConfig.GRID) {
        for (row in 0..GameConfig.GRID - GameConfig.CHAIN_LENGTH) {
            val line = (0 until GameConfig.CHAIN_LENGTH).map { (row + it) * GameConfig.GRID + column }
            if (line.all { it in held }) result.add(line)
        }
    }
    return result
}

fun collectFavor(
    districts: List<District>,
    owner: Owner,
    faction: Faction?,
    activeEvent: EventCard?,
    current: Int
): Int {
    var gain = districts.count { it.owner == owner && !it.burned }
    val treasury = districts.firstOrNull { it.site == SpecialSite.TREASURY }
    if (treasury != null && treasury.owner == owner && !treasury.burned) {
        gain += GameConfig.TREASURY_FAVOR
    }
    gain += chainsOf(districts, owner).size * GameConfig.CHAIN_FAVOR
    if (faction == Faction.PARCHMENT) gain += GameConfig.LEDGER_FAVOR
    if (activeEvent == EventCard.OPEN_GATES) gain += 2
    return (current + gain).coerceAtMost(GameConfig.FAVOR_CAP)
}
