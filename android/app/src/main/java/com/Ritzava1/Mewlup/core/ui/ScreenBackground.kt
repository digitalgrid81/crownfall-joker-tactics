package com.Ritzava1.Mewlup.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.Ritzava1.Mewlup.R
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson

@Composable
fun ScreenBackground(
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    glow: Color = RoyalCrimson,
    glowAlpha: Float = 0.18f,
    glowAlignment: Alignment = Alignment.BottomStart,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(glowAlignment)
                .offset(x = (-60).dp, y = 50.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(glow.copy(alpha = glowAlpha), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        content()
    }
}
