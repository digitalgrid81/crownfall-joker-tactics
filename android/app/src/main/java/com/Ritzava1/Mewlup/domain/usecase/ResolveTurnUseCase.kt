package com.Ritzava1.Mewlup.domain.usecase

import com.Ritzava1.Mewlup.domain.model.CourtFigure
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.FigureType
import com.Ritzava1.Mewlup.domain.model.OrderKind
import com.Ritzava1.Mewlup.domain.model.OrderMarker
import com.Ritzava1.Mewlup.domain.model.Owner
import com.Ritzava1.Mewlup.domain.model.SpecialSite
import kotlin.math.max

data class TurnResolution(
    val districts: List<District>,
    val capturedByPlayer: List<Int>,
    val capturedByRival: List<Int>,
    val forfeited: Boolean
)

class ResolveTurnUseCase {

    operator fun invoke(
        districts: List<District>,
        playerOrders: List<OrderMarker>,
        rivalOrders: List<OrderMarker>,
        roster: Map<FigureType, CourtFigure>,
        playerHome: Int
    ): TurnResolution {
        var board = districts
        val activeOrders = (playerOrders + rivalOrders)
            .filter { it.kind == OrderKind.DEPLOY && !it.stunned && it.figure != null }
        for (order in activeOrders) {
            val figure = order.figure ?: continue
            board = board.map {
                if (it.index == order.districtIndex) {
                    it.copy(figure = figure, figureOwner = order.owner)
                } else {
                    it
                }
            }
        }

        val contested = (playerOrders + rivalOrders).map { it.districtIndex }.toSet()
        val capturedByPlayer = mutableListOf<Int>()
        val capturedByRival = mutableListOf<Int>()
        val resolved = board.toMutableList()
        for (index in contested) {
            val district = resolved[index]
            if (district.burned) continue
            val playerPower = powerOf(resolved, index, Owner.PLAYER, playerOrders, roster)
            val rivalPower = powerOf(resolved, index, Owner.RIVAL, rivalOrders, roster)
            if (playerPower > rivalPower && district.owner != Owner.PLAYER) {
                resolved[index] = district.copy(owner = Owner.PLAYER)
                capturedByPlayer.add(index)
            } else if (rivalPower > playerPower && district.owner != Owner.RIVAL) {
                resolved[index] = district.copy(owner = Owner.RIVAL)
                capturedByRival.add(index)
            }
        }

        var forfeited = false
        if (playerOrders.isEmpty()) {
            val undefended = resolved
                .filter { it.owner == Owner.PLAYER && !it.burned }
                .minByOrNull { if (it.index == playerHome) -1 else it.value }
            if (undefended != null) {
                resolved[undefended.index] = undefended.copy(
                    owner = Owner.RIVAL,
                    figure = null,
                    figureOwner = Owner.NEUTRAL
                )
                capturedByRival.add(undefended.index)
                forfeited = true
            }
        }

        return TurnResolution(resolved.toList(), capturedByPlayer, capturedByRival, forfeited)
    }

    private fun powerOf(
        board: List<District>,
        index: Int,
        owner: Owner,
        orders: List<OrderMarker>,
        roster: Map<FigureType, CourtFigure>
    ): Int {
        val deployed = orders.firstOrNull {
            it.districtIndex == index && it.kind == OrderKind.DEPLOY && !it.stunned && it.figure != null
        }
        var power = 0
        if (deployed?.figure != null) {
            power += strengthOf(board, index, deployed.figure, owner, roster)
        }
        val standing = board[index]
        if (standing.figure != null && standing.figureOwner == owner && deployed?.figure == null) {
            power += strengthOf(board, index, standing.figure, owner, roster)
        }
        return power
    }

    private fun strengthOf(
        board: List<District>,
        index: Int,
        type: FigureType,
        owner: Owner,
        roster: Map<FigureType, CourtFigure>
    ): Int {
        val base = roster[type]?.strength ?: 1
        var power = base
        if (type == FigureType.MARSHAL && board[index].site == SpecialSite.BARRACKS) power += 1
        if (type == FigureType.JOKER) {
            val strongest = neighbours(index)
                .mapNotNull { board[it].figure }
                .mapNotNull { roster[it]?.strength }
                .maxOrNull() ?: 0
            power = max(power, strongest)
        }
        val duchessNearby = neighbours(index).any {
            board[it].figure == FigureType.DUCHESS && board[it].figureOwner == owner
        }
        if (duchessNearby && type != FigureType.DUCHESS) power += 1
        return power
    }
}
