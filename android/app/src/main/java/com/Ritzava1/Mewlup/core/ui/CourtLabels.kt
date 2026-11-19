package com.Ritzava1.Mewlup.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.Ritzava1.Mewlup.R
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.RoyalBlue
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.domain.model.Difficulty
import com.Ritzava1.Mewlup.domain.model.EventCard
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.model.FigureType
import com.Ritzava1.Mewlup.domain.model.MatchOutcome
import com.Ritzava1.Mewlup.domain.model.OrderKind
import com.Ritzava1.Mewlup.domain.model.OrderSummary
import com.Ritzava1.Mewlup.domain.model.Owner
import com.Ritzava1.Mewlup.domain.model.SpecialSite
import com.Ritzava1.Mewlup.domain.model.Takeaway
import com.Ritzava1.Mewlup.domain.model.TakeawayKind

fun spriteOf(type: FigureType): Int = when (type) {
    FigureType.JOKER -> R.drawable.sprite_1
    FigureType.MARSHAL -> R.drawable.sprite_2
    FigureType.HERALD -> R.drawable.sprite_3
    FigureType.SPY -> R.drawable.sprite_4
    FigureType.DUCHESS -> R.drawable.sprite_5
}

fun ownerColor(owner: Owner): Color = when (owner) {
    Owner.PLAYER -> RoyalCrimson
    Owner.RIVAL -> RoyalBlue
    Owner.NEUTRAL -> PolishedSilver
}

@Composable
fun figureLabel(type: FigureType): String = stringResource(
    when (type) {
        FigureType.JOKER -> R.string.figure_joker
        FigureType.MARSHAL -> R.string.figure_marshal
        FigureType.HERALD -> R.string.figure_herald
        FigureType.SPY -> R.string.figure_spy
        FigureType.DUCHESS -> R.string.figure_duchess
    }
)

@Composable
fun figureAbility(type: FigureType): String = stringResource(
    when (type) {
        FigureType.JOKER -> R.string.ability_joker
        FigureType.MARSHAL -> R.string.ability_marshal
        FigureType.HERALD -> R.string.ability_herald
        FigureType.SPY -> R.string.ability_spy
        FigureType.DUCHESS -> R.string.ability_duchess
    }
)

@Composable
fun eventLabel(card: EventCard): String = stringResource(
    when (card) {
        EventCard.CURFEW -> R.string.event_curfew
        EventCard.MASQUERADE_BALL -> R.string.event_masquerade
        EventCard.BREAD_RIOT -> R.string.event_riot
        EventCard.ROYAL_PARADE -> R.string.event_parade
        EventCard.TREASON -> R.string.event_treason
        EventCard.OPEN_GATES -> R.string.event_gates
    }
)

@Composable
fun eventRule(card: EventCard): String = stringResource(
    when (card) {
        EventCard.CURFEW -> R.string.rule_curfew
        EventCard.MASQUERADE_BALL -> R.string.rule_masquerade
        EventCard.BREAD_RIOT -> R.string.rule_riot
        EventCard.ROYAL_PARADE -> R.string.rule_parade
        EventCard.TREASON -> R.string.rule_treason
        EventCard.OPEN_GATES -> R.string.rule_gates
    }
)

@Composable
fun factionLabel(faction: Faction): String = stringResource(
    when (faction) {
        Faction.CRIMSON -> R.string.faction_crimson
        Faction.SILVER -> R.string.faction_silver
        Faction.PARCHMENT -> R.string.faction_parchment
    }
)

@Composable
fun factionAbility(faction: Faction): String = stringResource(
    when (faction) {
        Faction.CRIMSON -> R.string.faction_ability_crimson
        Faction.SILVER -> R.string.faction_ability_silver
        Faction.PARCHMENT -> R.string.faction_ability_parchment
    }
)

fun factionColor(faction: Faction): Color = when (faction) {
    Faction.CRIMSON -> RoyalCrimson
    Faction.SILVER -> PolishedSilver
    Faction.PARCHMENT -> Parchment
}

@Composable
fun difficultyLabel(difficulty: Difficulty): String = stringResource(
    when (difficulty) {
        Difficulty.COURTIER -> R.string.difficulty_courtier
        Difficulty.MARSHAL -> R.string.difficulty_marshal
        Difficulty.REGENT -> R.string.difficulty_regent
    }
)

@Composable
fun verdictLabel(outcome: MatchOutcome): String = stringResource(
    when (outcome) {
        MatchOutcome.WIN -> R.string.verdict_win
        MatchOutcome.LOSS -> R.string.verdict_loss
        MatchOutcome.DRAW -> R.string.verdict_draw
        MatchOutcome.FORFEIT -> R.string.verdict_loss
    }
)

@Composable
fun siteLabel(site: SpecialSite): String = stringResource(
    when (site) {
        SpecialSite.CATHEDRAL -> R.string.site_cathedral
        SpecialSite.TREASURY -> R.string.site_treasury
        SpecialSite.BARRACKS -> R.string.site_barracks
        SpecialSite.NONE -> R.string.site_none
    }
)

@Composable
fun takeawayLine(takeaway: Takeaway): String = when (takeaway.kind) {
    TakeawayKind.DECOYS_CALLED -> stringResource(R.string.takeaway_decoys, takeaway.value)
    TakeawayKind.NO_CHAIN -> stringResource(R.string.takeaway_no_chain, takeaway.value)
    TakeawayKind.EVENT_COST -> stringResource(R.string.takeaway_event, takeaway.value)
    TakeawayKind.SOLID_HOLD -> stringResource(R.string.takeaway_hold, takeaway.value)
}

@Composable
fun orderLine(summary: OrderSummary): String {
    val kind = summary.kind ?: return stringResource(R.string.order_none)
    val district = summary.districtCode
    val figure = summary.figure
    return if (kind == OrderKind.DECOY || figure == null) {
        stringResource(R.string.order_decoy_at, district)
    } else {
        stringResource(R.string.order_deploy_at, figureLabel(figure), district)
    }
}
