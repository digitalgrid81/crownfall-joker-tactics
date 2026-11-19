package com.Ritzava1.Mewlup.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.Ritzava1.Mewlup.core.theme.CrimsonDeep
import com.Ritzava1.Mewlup.core.theme.DeepVoid
import com.Ritzava1.Mewlup.core.theme.MidnightBlue
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.RoyalBlue
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.SurfaceAlt
import com.Ritzava1.Mewlup.core.theme.SurfacePanel
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.ScreenBackground
import com.Ritzava1.Mewlup.core.ui.StatCard
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.presentation.dialog.SettingsDialog

@Composable
fun MenuScreen(
    onPlay: (MatchRequest) -> Unit,
    onCampaign: (Boolean) -> Unit,
    onHistory: () -> Unit,
    onRules: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val loop = rememberInfiniteTransition(label = "menuLoop")
    val glow by loop.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playGlow"
    )

    if (state.settingsOpen) {
        SettingsDialog(
            settings = state.settings,
            onSelectDifficulty = viewModel::selectDifficulty,
            onToggleThreatOverlay = viewModel::toggleThreatOverlay,
            onToggleConfirmEndTurn = viewModel::toggleConfirmEndTurn,
            onToggleReduceMotion = viewModel::toggleReduceMotion,
            onDismiss = viewModel::closeSettings
        )
    }

    ScreenBackground(
        overlay = listOf(
            MidnightBlue.copy(alpha = 0.62f),
            SurfacePanel.copy(alpha = 0.74f),
            DeepVoid.copy(alpha = 0.92f)
        ),
        glow = RoyalCrimson,
        glowAlpha = 0.18f,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = stringResource(R.string.brand),
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishedSilver
                    )
                    Text(
                        text = stringResource(R.string.app_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 15.sp,
                        lineHeight = 19.sp,
                        letterSpacing = 0.4.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = viewModel::openSettings,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.settings),
                        tint = PolishedSilver,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Button(
                onClick = { onPlay(state.quickMatch) },
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(0.dp),
                elevation = null,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(listOf(RoyalCrimson, CrimsonDeep)),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = PolishedSilver.copy(alpha = glow * 0.6f),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_1),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp, top = 10.dp, bottom = 10.dp)
                            .size(86.dp)
                            .alpha(0.22f)
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 22.dp, end = 110.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.menu_play_hint),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            letterSpacing = 2.2.sp,
                            color = TextPrimary.copy(alpha = 0.76f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_play),
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.play),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                lineHeight = 30.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                BentoTile(
                    iconRes = R.drawable.ic_flag,
                    accent = PolishedSilver,
                    caption = stringResource(R.string.campaign),
                    value = stringResource(
                        R.string.menu_campaign_value,
                        state.clearedMaps,
                        state.totalMaps
                    ),
                    tileHeight = 104,
                    onClick = { onCampaign(false) },
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    iconRes = R.drawable.ic_star,
                    accent = Parchment,
                    caption = stringResource(R.string.challenge),
                    value = stringResource(
                        R.string.menu_challenge_value,
                        state.sealedChallenges,
                        state.totalChallenges
                    ),
                    tileHeight = 104,
                    onClick = { onCampaign(true) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                BentoTile(
                    iconRes = R.drawable.ic_history,
                    accent = RoyalBlue,
                    caption = stringResource(R.string.history),
                    value = stringResource(R.string.menu_history_value, state.storedMatches),
                    tileHeight = 96,
                    onClick = onHistory,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    iconRes = R.drawable.ic_info,
                    accent = TextSecondary,
                    caption = stringResource(R.string.how_to_play),
                    value = stringResource(R.string.menu_rules_value),
                    tileHeight = 96,
                    onClick = onRules,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (state.statCount >= 2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    if (state.bestInfluence > 0) {
                        StatCard(
                            value = state.bestInfluence.toString(),
                            label = stringResource(R.string.stat_best_influence),
                            accent = RoyalCrimson,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (state.courtsWon > 0) {
                        StatCard(
                            value = state.courtsWon.toString(),
                            label = stringResource(R.string.stat_courts_won),
                            accent = PolishedSilver,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (state.decoysCalled > 0) {
                        StatCard(
                            value = state.decoysCalled.toString(),
                            label = stringResource(R.string.stat_decoys_called),
                            accent = RoyalBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceAlt.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, Parchment.copy(alpha = 0.30f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.disclaimer),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 1.2.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun BentoTile(
    iconRes: Int,
    accent: Color,
    caption: String,
    value: String,
    tileHeight: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfacePanel.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.30f)),
        modifier = modifier
            .height(tileHeight.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                letterSpacing = 1.8.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                letterSpacing = 1.1.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
