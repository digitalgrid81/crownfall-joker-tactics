package com.Ritzava1.Mewlup.presentation.replay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.Ritzava1.Mewlup.core.theme.RoyalBlue
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.SurfacePanel
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.InfluenceChart
import com.Ritzava1.Mewlup.core.ui.ParchmentPanel
import com.Ritzava1.Mewlup.core.ui.PrimaryButton
import com.Ritzava1.Mewlup.core.ui.ScreenBackground
import com.Ritzava1.Mewlup.core.ui.SectionHeader
import com.Ritzava1.Mewlup.core.ui.orderLine
import com.Ritzava1.Mewlup.core.ui.takeawayLine
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.model.ReplayTurn

@Composable
fun ReplayScreen(
    recordId: Long,
    onMenu: () -> Unit,
    onPlay: (MatchRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ReplayViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(recordId) {
        viewModel.prepare(recordId)
    }

    ScreenBackground(
        overlay = listOf(
            MidnightBlue.copy(alpha = 0.80f),
            SurfacePanel.copy(alpha = 0.82f),
            DeepVoid.copy(alpha = 0.94f)
        ),
        glow = RoyalBlue,
        glowAlpha = 0.14f,
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
                IconButton(onClick = onMenu, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.menu),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.replay_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.2.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            val record = state.selected
            if (record == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    ParchmentPanel(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.replay_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PrimaryButton(
                        text = stringResource(R.string.play),
                        onClick = { onPlay(viewModel.freshMatchRequest()) },
                        iconRes = R.drawable.ic_play,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.records, key = { it.id }) { item ->
                        val selected = item.id == record.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) {
                                RoyalCrimson.copy(alpha = 0.26f)
                            } else {
                                SurfacePanel.copy(alpha = 0.78f)
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selected) {
                                    RoyalCrimson
                                } else {
                                    PolishedSilver.copy(alpha = 0.22f)
                                }
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.select(item.id) }
                        ) {
                            Text(
                                text = stringResource(R.string.court_number, item.id),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = if (selected) TextPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                SectionHeader(
                    title = record.mapLabel,
                    subtitle = stringResource(
                        R.string.replay_score,
                        record.playerInfluence,
                        record.rivalInfluence
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                ParchmentPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    InfluenceChart(turns = record.turns)
                }

                ParchmentPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    fillAlpha = 0.12f
                ) {
                    Text(
                        text = takeawayLine(record.takeaway),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 1.2.sp,
                        color = Parchment
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(record.turns, key = { it.turn }) { turn ->
                        LedgerRow(turn)
                    }
                }
            }
        }
    }
}

@Composable
private fun LedgerRow(turn: ReplayTurn) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfacePanel.copy(alpha = 0.62f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(26.dp)
        ) {
            Text(
                text = turn.turn.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PolishedSilver
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = orderLine(turn.player),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                letterSpacing = 0.8.sp,
                color = TextPrimary
            )
            Text(
                text = orderLine(turn.rival),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                letterSpacing = 0.8.sp,
                color = TextMuted
            )
        }
        Spacer(modifier = Modifier.size(6.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.delta_value, turn.playerDelta),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = RoyalCrimson
            )
            Text(
                text = stringResource(R.string.delta_value, turn.rivalDelta),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = RoyalBlue
            )
        }
    }
}
