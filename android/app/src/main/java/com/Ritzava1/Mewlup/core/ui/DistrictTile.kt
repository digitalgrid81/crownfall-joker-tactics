package com.Ritzava1.Mewlup.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Ritzava1.Mewlup.core.theme.DeepVoid
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.RoyalBlue
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.SurfacePanel
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.domain.model.District
import com.Ritzava1.Mewlup.domain.model.OrderMarker
import com.Ritzava1.Mewlup.domain.model.Owner

@Composable
fun DistrictTile(
    district: District,
    tileSize: Dp,
    selected: Boolean,
    marker: OrderMarker?,
    revealed: Boolean,
    threatened: Boolean,
    inChain: Boolean,
    description: String,
    stateLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fill = when {
        district.burned -> DeepVoid
        district.owner == Owner.PLAYER -> RoyalCrimson.copy(alpha = 0.26f)
        district.owner == Owner.RIVAL -> RoyalBlue.copy(alpha = 0.24f)
        else -> SurfacePanel.copy(alpha = 0.70f)
    }
    val stroke = when {
        selected -> RoyalCrimson
        district.burned -> TextMuted.copy(alpha = 0.5f)
        district.owner == Owner.PLAYER -> RoyalCrimson
        district.owner == Owner.RIVAL -> RoyalBlue
        inChain -> PolishedSilver
        threatened -> RoyalBlue.copy(alpha = 0.5f)
        else -> Parchment.copy(alpha = 0.22f)
    }
    Box(
        modifier = modifier
            .size(tileSize)
            .padding(1.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(fill, RoundedCornerShape(10.dp))
            .border(
                width = if (selected || inChain) 2.dp else 1.dp,
                color = stroke,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(enabled = !district.burned, onClick = onClick)
            .semantics {
                contentDescription = description
                stateDescription = stateLabel
            }
    ) {
        Text(
            text = district.code,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            lineHeight = 11.sp,
            letterSpacing = 0.4.sp,
            fontWeight = FontWeight.Bold,
            color = if (district.burned) TextMuted else Parchment,
            textDecoration = if (district.burned) TextDecoration.LineThrough else null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 4.dp, top = 3.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 4.dp, bottom = 4.dp)
        ) {
            if (!district.burned) {
                repeat(district.value) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Parchment, CircleShape)
                    )
                }
            }
        }
        val figure = district.figure
        if (figure != null && !district.burned) {
            Image(
                painter = painterResource(spriteOf(figure)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(tileSize * 0.52f)
            )
        }
        if (marker != null) {
            val sealColor = if (marker.owner == Owner.PLAYER) RoyalCrimson else RoyalBlue
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(if (revealed) 10.dp else 8.dp)
                    .background(
                        color = if (revealed && marker.figure == null) {
                            Color.Transparent
                        } else {
                            sealColor
                        },
                        shape = CircleShape
                    )
                    .border(1.dp, PolishedSilver.copy(alpha = 0.7f), CircleShape)
            )
        }
    }
}
