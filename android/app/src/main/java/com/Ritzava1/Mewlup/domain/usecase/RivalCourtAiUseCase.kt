package com.Ritzava1.Mewlup.domain.usecase

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.CourtFigure
import com.Ritzava1.Mewlup.domain.model.Difficulty
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.EventCard
import com.Ritzava1.Mewlup.domain.model.OrderKind
import com.Ritzava1.Mewlup.domain.model.OrderMarker
import com.Ritzava1.Mewlup.domain.model.Owner
import kotlin.random.Random

data class RivalPlan(
    val orders: List<OrderMarker>,
    val favorSpent: Int,
    val unmaskChance: Int
)

class RivalCourtAiUseCase {

    operator fun invoke(
        districts: List<District>,
        favor: Int,
        difficulty: Difficulty,
        roster: List<CourtFigure>,
        activeEvent: EventCard?,
        random: Random
    ): RivalPlan {
        val held = districts.filter { it.owner == Owner.RIVAL && !it.burned }.map { it.index }
        val frontier = held
            .flatMap { neighbours(it) }
            .distinct()
            .filter { !districts[it].burned && districts[it].owner != Owner.RIVAL }
        val targets = if (frontier.isEmpty()) {
            districts.filter { !it.burned && it.owner != Owner.RIVAL }.map { it.index }
        } else {
            frontier
        }
        if (targets.isEmpty()) return RivalPlan(emptyList(), 0, 0)

        val ranked = when (difficulty) {
            Difficulty.COURTIER -> targets.shuffled(random)
            Difficulty.MARSHAL -> targets.sortedByDescending { scoreTarget(districts, it, false) }
            Difficulty.REGENT -> targets.sortedByDescending { scoreTarget(districts, it, true) }
        }

        val orders = mutableListOf<OrderMarker>()
        var purse = favor
        var slot = 0
        while (orders.size < GameConfig.ORDERS_PER_TURN && slot < ranked.size) {
            val target = ranked[slot]
            slot += 1
            if (orders.any { it.districtIndex == target }) continue
            val affordable = roster
                .filter { deployCost(it.cost, activeEvent) <= purse }
                .sortedByDescending { it.strength }
            if (affordable.isEmpty()) break
            val pick = when (difficulty) {
                Difficulty.COURTIER -> affordable[random.nextInt(affordable.size)]
                else -> affordable.first()
            }
            val cost = deployCost(pick.cost, activeEvent)
            if (random.nextInt(100) < bluffChance(difficulty) &&
                purse >= decoyCost(activeEvent, false, 0)
            ) {
                purse -= decoyCost(activeEvent, false, 0)
                orders.add(OrderMarker(target, OrderKind.DECOY, null, Owner.RIVAL))
            } else {
                purse -= cost
                orders.add(OrderMarker(target, OrderKind.DEPLOY, pick.type, Owner.RIVAL))
            }
        }
        return RivalPlan(orders, favor - purse, unmaskChance(difficulty))
    }

    private fun scoreTarget(districts: List<District>, index: Int, withChains: Boolean): Int {
        val district = districts[index]
        var score = district.value * 2
        score += neighbours(index).count { districts[it].owner == Owner.RIVAL }
        if (district.owner == Owner.PLAYER) score += 3
        if (withChains) {
            val projected = districts.map {
                if (it.index == index) it.copy(owner = Owner.RIVAL) else it
            }
            score += chainsOf(projected, Owner.RIVAL).size * 4
        }
        return score
    }

    private fun bluffChance(difficulty: Difficulty): Int = when (difficulty) {
        Difficulty.COURTIER -> 40
        Difficulty.MARSHAL -> 25
        Difficulty.REGENT -> 15
    }

    private fun unmaskChance(difficulty: Difficulty): Int = when (difficulty) {
        Difficulty.COURTIER -> 0
        Difficulty.MARSHAL -> 30
        Difficulty.REGENT -> 60
    }
}
