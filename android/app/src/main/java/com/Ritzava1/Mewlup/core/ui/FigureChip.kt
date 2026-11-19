package com.Ritzava1.Mewlup.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.SurfaceAlt
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.domain.model.CourtFigure

@Composable
fun FigureChip(
    figure: CourtFigure,
    label: String,
    costLine: String,
    stateLabel: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val border = when {
        selected -> BorderStroke(2.dp, RoyalCrimson)
        enabled -> BorderStroke(1.dp, PolishedSilver.copy(alpha = 0.26f))
        else -> BorderStroke(1.dp, TextMuted.copy(alpha = 0.20f))
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) RoyalCrimson.copy(alpha = 0.22f) else SurfaceAlt.copy(alpha = 0.86f),
        border = border,
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else 0.38f)
            .semantics {
                contentDescription = label
                stateDescription = stateLabel
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp, horizontal = 2.dp)
        ) {
            Image(
                painter = painterResource(spriteOf(figure.type)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                lineHeight = 10.sp,
                letterSpacing = 0.6.sp,
                fontWeight = FontWeight.Bold,
                color = Parchment,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = costLine,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                lineHeight = 11.sp,
                letterSpacing = 0.4.sp,
                fontWeight = FontWeight.Black,
                color = if (enabled) PolishedSilver else TextMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
