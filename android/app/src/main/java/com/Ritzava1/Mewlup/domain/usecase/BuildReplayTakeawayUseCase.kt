package com.Ritzava1.Mewlup.domain.usecase

import com.Ritzava1.Mewlup.domain.model.MatchState
import com.Ritzava1.Mewlup.domain.model.Owner
import com.Ritzava1.Mewlup.domain.model.Takeaway
import com.Ritzava1.Mewlup.domain.model.TakeawayKind

class BuildReplayTakeawayUseCase {

    operator fun invoke(state: MatchState): Takeaway {
        if (state.decoysPlaced > 0 && state.decoysCalled * 2 >= state.decoysPlaced) {
            return Takeaway(TakeawayKind.DECOYS_CALLED, state.decoysCalled)
        }
        val worstTurn = state.turns.minByOrNull { it.playerDelta - it.rivalDelta }
        if (worstTurn != null && worstTurn.event != null && worstTurn.playerDelta < worstTurn.rivalDelta) {
            return Takeaway(TakeawayKind.EVENT_COST, worstTurn.rivalDelta - worstTurn.playerDelta)
        }
        val chains = chainsOf(state.districts, Owner.PLAYER)
        if (chains.isEmpty()) {
            return Takeaway(TakeawayKind.NO_CHAIN, state.turn)
        }
        return Takeaway(TakeawayKind.SOLID_HOLD, chains.size)
    }
}
