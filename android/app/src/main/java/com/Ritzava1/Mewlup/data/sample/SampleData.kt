package com.Ritzava1.Mewlup.data.sample

import com.Ritzava1.Mewlup.domain.model.CampaignMap
import com.Ritzava1.Mewlup.domain.model.ChallengeId
import com.Ritzava1.Mewlup.domain.model.ChallengeScenario
import com.Ritzava1.Mewlup.domain.model.CourtFigure
import com.Ritzava1.Mewlup.domain.model.DistrictName
import com.Ritzava1.Mewlup.domain.model.FigureType

object SampleData {

    val roster: List<CourtFigure> = listOf(
        CourtFigure(FigureType.SPY, cost = 1, strength = 1),
        CourtFigure(FigureType.JOKER, cost = 2, strength = 2),
        CourtFigure(FigureType.HERALD, cost = 2, strength = 2),
        CourtFigure(FigureType.DUCHESS, cost = 3, strength = 3),
        CourtFigure(FigureType.MARSHAL, cost = 4, strength = 4)
    )

    val rosterByType: Map<FigureType, CourtFigure> = roster.associateBy { it.type }

    val campaignMaps: List<CampaignMap> = listOf(
        CampaignMap(0, "THE GILDED GATE", "Wide avenues. Room to manoeuvre before the bells."),
        CampaignMap(1, "LANTERN QUARTER", "Narrow lanes. Decoys read true in the lamplight."),
        CampaignMap(2, "CATHEDRAL SQUARE", "The parade favours whoever holds the steps."),
        CampaignMap(3, "THE SALT DOCKS", "Riots start at the water. Chains break often."),
        CampaignMap(4, "MINT AND TREASURY", "Favor flows fast and the rival knows it."),
        CampaignMap(5, "CROWNFALL PLAZA", "Dense inner districts. Curfews bite hard.")
    )

    val challenges: List<ChallengeScenario> = listOf(
        ChallengeScenario(
            ChallengeId.SILENT_COURT,
            "SILENT COURT",
            "Decoys cost nothing for either court.",
            "Finish six influence ahead."
        ),
        ChallengeScenario(
            ChallengeId.HOLD_THE_CATHEDRAL,
            "HOLD THE CATHEDRAL",
            "Only the cathedral steps are counted.",
            "Hold Cathedral Square three turns running."
        ),
        ChallengeScenario(
            ChallengeId.NO_JESTERS,
            "NO JESTERS",
            "The Joker is barred from your court.",
            "Win the coronation without deploying it."
        ),
        ChallengeScenario(
            ChallengeId.ONE_GOLDEN_HOUR,
            "ONE GOLDEN HOUR",
            "The bells ring three turns early.",
            "Win inside five turns."
        )
    )

    val districtNames: List<DistrictName> = listOf(
        DistrictName("CAT", "CATHEDRAL SQUARE"),
        DistrictName("LAN", "LANTERN ROW"),
        DistrictName("DCK", "SALT DOCKS"),
        DistrictName("MNT", "MINT YARD"),
        DistrictName("GAT", "THE GILDED GATE"),
        DistrictName("BRK", "OLD BARRACKS"),
        DistrictName("TRS", "TREASURY STAIR"),
        DistrictName("BLL", "BELLFOUNDERS LANE"),
        DistrictName("ASH", "ASHGROVE TERRACE"),
        DistrictName("VLT", "VELVET MARKET"),
        DistrictName("QUA", "QUARRY GATE"),
        DistrictName("HRB", "HARBOUR STEPS"),
        DistrictName("PLZ", "CROWNFALL PLAZA"),
        DistrictName("GRN", "GREENSTONE WALK"),
        DistrictName("INK", "INKWELL COURT"),
        DistrictName("MAS", "MASQUE ARCADE"),
        DistrictName("SPR", "SPARROW HILL"),
        DistrictName("CRN", "CORN EXCHANGE"),
        DistrictName("GLV", "GLOVERS YARD"),
        DistrictName("WID", "WIDOWS BRIDGE"),
        DistrictName("ORC", "ORCHARD GATE"),
        DistrictName("SIG", "SIGNAL TOWER"),
        DistrictName("FRY", "FERRY LANDING"),
        DistrictName("PAL", "PALACE MEWS"),
        DistrictName("THR", "THRONE APPROACH"),
        DistrictName("RVN", "RAVEN CLOISTER"),
        DistrictName("SLT", "SALTERS ROW"),
        DistrictName("CND", "CANDLE WYND")
    )
}
