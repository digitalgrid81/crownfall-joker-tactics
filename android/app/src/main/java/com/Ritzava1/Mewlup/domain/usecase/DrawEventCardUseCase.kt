package com.Ritzava1.Mewlup.domain.usecase

import com.Ritzava1.Mewlup.core.config.GameConfig
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.EventCard
import com.Ritzava1.Mewlup.domain.model.Owner
import kotlin.random.Random

class DrawEventCardUseCase {

    operator fun invoke(random: Random): EventCard {
        val deck = EventCard.entries
        return deck[random.nextInt(deck.size)]
    }

    fun applyCard(card: EventCard, districts: List<District>, random: Random): List<District> = when (card) {
        EventCard.BREAD_RIOT -> {
            val candidates = districts.filter { !it.burned && !isLastHolding(districts, it) }
            if (candidates.isEmpty()) {
                districts
            } else {
                val victim = candidates[random.nextInt(candidates.size)].index
                districts.map {
                    if (it.index == victim) {
                        it.copy(
                            burned = true,
                            owner = Owner.NEUTRAL,
                            figure = null,
                            figureOwner = Owner.NEUTRAL
                        )
                    } else {
                        it
                    }
                }
            }
        }

        EventCard.TREASON -> {
            val owned = districts.filter {
                it.owner != Owner.NEUTRAL && !it.burned && !isLastHolding(districts, it)
            }
            if (owned.isEmpty()) {
                districts
            } else {
                val turncoat = owned[random.nextInt(owned.size)].index
                districts.map {
                    if (it.index == turncoat) {
                        it.copy(owner = if (it.owner == Owner.PLAYER) Owner.RIVAL else Owner.PLAYER)
                    } else {
                        it
                    }
                }
            }
        }

        else -> districts
    }
}

private fun isLastHolding(districts: List<District>, district: District): Boolean {
    if (district.owner == Owner.NEUTRAL) return false
    return districts.count { it.owner == district.owner && !it.burned } <= 1
}

fun decoyCost(activeEvent: EventCard?, masqueradeFaction: Boolean, turn: Int): Int {
    if (activeEvent == EventCard.MASQUERADE_BALL) return 0
    if (masqueradeFaction && turn % 2 == 1) return 0
    return GameConfig.DECOY_COST
}

fun unmaskCost(activeEvent: EventCard?): Int =
    if (activeEvent == EventCard.MASQUERADE_BALL) {
        GameConfig.MASQUERADE_UNMASK_COST
    } else {
        GameConfig.UNMASK_COST
    }

fun deployCost(baseCost: Int, activeEvent: EventCard?): Int =
    if (activeEvent == EventCard.OPEN_GATES) (baseCost - 1).coerceAtLeast(0) else baseCost
