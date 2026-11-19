package com.Ritzava1.Mewlup.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.TextSecondary

@Composable
fun EventBanner(
    visible: Boolean,
    title: String,
    rule: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(animationSpec = tween(300)) { it } + fadeIn(tween(260)),
        exit = slideOutVertically(animationSpec = tween(240)) { it } + fadeOut(tween(220)),
        modifier = modifier
    ) {
        ParchmentPanel(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16,
            fillAlpha = 0.12f,
            borderAlpha = 0.34f,
            elevation = 18,
            contentPadding = 12
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(PolishedSilver, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        letterSpacing = 2.4.sp,
                        fontWeight = FontWeight.Black,
                        color = Parchment
                    )
                    Text(
                        text = rule,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
