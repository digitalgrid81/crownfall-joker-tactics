package com.Ritzava1.Mewlup.domain.model

enum class Owner { NEUTRAL, PLAYER, RIVAL }

enum class FigureType { SPY, JOKER, HERALD, DUCHESS, MARSHAL }

data class CourtFigure(
    val type: FigureType,
    val cost: Int,
    val strength: Int
)

enum class Faction { CRIMSON, SILVER, PARCHMENT }

enum class SpecialSite { NONE, CATHEDRAL, TREASURY, BARRACKS }

data class DistrictName(val code: String, val name: String)

data class District(
    val index: Int,
    val code: String,
    val name: String,
    val value: Int,
    val site: SpecialSite,
    val owner: Owner = Owner.NEUTRAL,
    val burned: Boolean = false,
    val figure: FigureType? = null,
    val figureOwner: Owner = Owner.NEUTRAL
)

enum class OrderKind { DEPLOY, DECOY }

data class OrderMarker(
    val districtIndex: Int,
    val kind: OrderKind,
    val figure: FigureType?,
    val owner: Owner,
    val stunned: Boolean = false
)

data class BoardLayout(
    val districts: List<District>,
    val playerHome: Int,
    val rivalHome: Int,
    val fallback: Boolean
)
