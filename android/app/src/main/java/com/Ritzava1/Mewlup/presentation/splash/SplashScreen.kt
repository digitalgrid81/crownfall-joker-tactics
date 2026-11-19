package com.Ritzava1.Mewlup.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Ritzava1.Mewlup.R
import com.Ritzava1.Mewlup.core.di.AppViewModelFactory
import com.Ritzava1.Mewlup.core.theme.DeepVoid
import com.Ritzava1.Mewlup.core.theme.MidnightBlue
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.SurfacePanel
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.ScreenBackground

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory)
    val finished by viewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    val loop = rememberInfiniteTransition(label = "splashLoop")
    val ringAngle by loop.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing)
        ),
        label = "ringAngle"
    )
    val glow by loop.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val drift by loop.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sealDrift"
    )

    ScreenBackground(
        overlay = listOf(
            DeepVoid.copy(alpha = 0.94f),
            MidnightBlue.copy(alpha = 0.90f),
            DeepVoid.copy(alpha = 0.97f)
        ),
        glow = RoyalCrimson,
        glowAlpha = 0.10f,
        glowAlignment = Alignment.TopEnd,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .graphicsLayer { rotationZ = ringAngle }
                        .border(
                            width = 2.dp,
                            color = PolishedSilver.copy(alpha = 0.12f),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .alpha(glow)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(RoyalCrimson.copy(alpha = 0.55f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Parchment.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
                        .border(
                            width = 1.dp,
                            color = PolishedSilver.copy(alpha = 0.28f),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(22.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(150.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displayMedium,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                letterSpacing = 4.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .offset(y = (drift + index * 2).dp)
                            .size(6.dp)
                            .background(Parchment.copy(alpha = 0.55f), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            LinearProgressIndicator(
                color = RoyalCrimson,
                trackColor = SurfacePanel,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_loading),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
        }
    }
}
