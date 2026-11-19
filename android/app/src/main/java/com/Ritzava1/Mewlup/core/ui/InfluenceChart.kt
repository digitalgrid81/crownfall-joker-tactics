package com.Ritzava1.Mewlup.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.RoyalBlue
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.domain.model.ReplayTurn
import kotlin.math.max

@Composable
fun InfluenceChart(
    turns: List<ReplayTurn>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        if (turns.isEmpty()) return@Canvas
        val padding = 14f
        val width = size.width - padding * 2
        val height = size.height - padding * 2
        var playerTotal = 0
        var rivalTotal = 0
        val playerPoints = mutableListOf<Offset>()
        val rivalPoints = mutableListOf<Offset>()
        val totals = mutableListOf<Int>()
        for (turn in turns) {
            playerTotal += turn.playerDelta
            rivalTotal += turn.rivalDelta
            totals.add(playerTotal)
            totals.add(rivalTotal)
        }
        val peak = max(totals.maxOrNull() ?: 1, 1).toFloat()
        val step = if (turns.size > 1) width / (turns.size - 1) else width
        playerTotal = 0
        rivalTotal = 0
        turns.forEachIndexed { index, turn ->
            playerTotal += turn.playerDelta
            rivalTotal += turn.rivalDelta
            val x = padding + step * index
            playerPoints.add(Offset(x, padding + height - height * (playerTotal / peak)))
            rivalPoints.add(Offset(x, padding + height - height * (rivalTotal / peak)))
        }
        repeat(5) { line ->
            val y = padding + height * line / 4f
            drawLine(
                color = PolishedSilver.copy(alpha = 0.12f),
                start = Offset(padding, y),
                end = Offset(padding + width, y),
                strokeWidth = 1f
            )
        }
        drawPolyline(rivalPoints, RoyalBlue, 4f)
        drawPolyline(playerPoints, RoyalCrimson, 6f)
        playerPoints.forEach { point ->
            drawCircle(color = RoyalCrimson, radius = 5f, center = point)
        }
        rivalPoints.forEach { point ->
            drawCircle(color = RoyalBlue, radius = 4f, center = point)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPolyline(
    points: List<Offset>,
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Float
) {
    if (points.size < 2) {
        points.firstOrNull()?.let { drawCircle(color = color, radius = strokeWidth, center = it) }
        return
    }
    val path = Path()
    path.moveTo(points.first().x, points.first().y)
    for (index in 1 until points.size) {
        path.lineTo(points[index].x, points[index].y)
    }
    drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
}
