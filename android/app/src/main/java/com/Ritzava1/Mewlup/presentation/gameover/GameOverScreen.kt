package com.Ritzava1.Mewlup.presentation.gameover

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
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
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.PrimaryButton
import com.Ritzava1.Mewlup.core.ui.ScreenBackground
import com.Ritzava1.Mewlup.core.ui.SecondaryButton
import com.Ritzava1.Mewlup.core.ui.StatCard
import com.Ritzava1.Mewlup.core.ui.verdictLabel
import com.Ritzava1.Mewlup.domain.model.MatchOutcome
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.model.MatchSummary

private data class ResultStat(
    val value: String,
    val label: String,
    val accent: Color,
    val announce: Boolean
)

@Composable
fun GameOverScreen(
    summary: MatchSummary,
    onPlayAgain: (MatchRequest) -> Unit,
    onReplay: (Long) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(summary) {
        viewModel.prepare(summary)
    }

    val influence by animateIntAsState(
        targetValue = summary.playerInfluence,
        label = "influenceCount"
    )
    val outcome = summary.outcome
    val overlay = when (outcome) {
        MatchOutcome.WIN -> listOf(
            RoyalCrimson.copy(alpha = 0.30f),
            MidnightBlue.copy(alpha = 0.88f),
            DeepVoid.copy(alpha = 0.94f)
        )

        MatchOutcome.DRAW -> listOf(
            RoyalBlue.copy(alpha = 0.24f),
            MidnightBlue.copy(alpha = 0.88f),
            DeepVoid.copy(alpha = 0.94f)
        )

        else -> listOf(
            DeepVoid.copy(alpha = 0.90f),
            DeepVoid.copy(alpha = 0.94f),
            DeepVoid.copy(alpha = 0.97f)
        )
    }

    ScreenBackground(
        overlay = overlay,
        glow = if (outcome == MatchOutcome.WIN) RoyalCrimson else PolishedSilver,
        glowAlpha = 0.16f,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .border(
                            width = 2.dp,
                            color = PolishedSilver.copy(alpha = 0.16f),
                            shape = CircleShape
                        )
                )
                Text(
                    text = verdictLabel(outcome),
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 26.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            if (outcome == MatchOutcome.FORFEIT) {
                Text(
                    text = stringResource(R.string.verdict_forfeit_sub),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            if (summary.challengeSealed) {
                Text(
                    text = stringResource(R.string.challenge_sealed),
                    style = MaterialTheme.typography.labelSmall,
                    color = Parchment,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            val influenceLabel = stringResource(R.string.stat_influence)
            val rivalLabel = stringResource(R.string.stat_rival)
            val districtsLabel = stringResource(R.string.stat_districts)
            val bestLabel = stringResource(R.string.stat_best_influence)
            val cards = mutableListOf<ResultStat>()
            if (influence > 0) {
                cards.add(ResultStat(influence.toString(), influenceLabel, RoyalCrimson, true))
            }
            if (summary.rivalInfluence > 0) {
                cards.add(
                    ResultStat(summary.rivalInfluence.toString(), rivalLabel, RoyalBlue, false)
                )
            }
            if (summary.districtsHeld > 0) {
                cards.add(
                    ResultStat(summary.districtsHeld.toString(), districtsLabel, Parchment, false)
                )
            }
            if (state.bestInfluence > 0) {
                cards.add(
                    ResultStat(state.bestInfluence.toString(), bestLabel, PolishedSilver, false)
                )
            }
            if (cards.size >= 2) {
                cards.chunked(2).forEach { pair ->
                    if (pair.size == 2) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            pair.forEach { card ->
                                StatCard(
                                    value = card.value,
                                    label = card.label,
                                    accent = card.accent,
                                    announce = card.announce,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = {
                    val next = viewModel.rematchRequest()
                    if (next != null) onPlayAgain(next)
                },
                iconRes = R.drawable.ic_play,
                modifier = Modifier.fillMaxWidth()
            )
            SecondaryButton(
                text = stringResource(R.string.replay_analysis),
                onClick = { onReplay(summary.replayId) },
                iconRes = R.drawable.ic_history,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 18.dp)
            )
        }
    }
}
