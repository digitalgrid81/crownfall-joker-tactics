package com.Ritzava1.Mewlup.presentation.rules

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.Ritzava1.Mewlup.core.theme.SurfacePanel
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.ParchmentPanel
import com.Ritzava1.Mewlup.core.ui.SecondaryButton
import com.Ritzava1.Mewlup.core.ui.ScreenBackground
import com.Ritzava1.Mewlup.core.ui.SectionHeader
import com.Ritzava1.Mewlup.core.ui.eventLabel
import com.Ritzava1.Mewlup.core.ui.eventRule
import com.Ritzava1.Mewlup.core.ui.figureAbility
import com.Ritzava1.Mewlup.core.ui.figureLabel
import com.Ritzava1.Mewlup.core.ui.spriteOf
import com.Ritzava1.Mewlup.domain.model.CourtFigure
import com.Ritzava1.Mewlup.domain.model.EventCard

@Composable
fun RulesScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RulesViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScreenBackground(
        overlay = listOf(
            MidnightBlue.copy(alpha = 0.82f),
            SurfacePanel.copy(alpha = 0.84f),
            DeepVoid.copy(alpha = 0.94f)
        ),
        glow = Parchment,
        glowAlpha = 0.10f,
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
                    text = stringResource(R.string.rules_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.2.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    RuleCard(
                        title = stringResource(R.string.rules_goal_title),
                        body = stringResource(R.string.rules_goal_body)
                    )
                }
                item {
                    RuleCard(
                        title = stringResource(R.string.rules_order_title),
                        body = stringResource(R.string.rules_order_body)
                    )
                }
                item {
                    RuleCard(
                        title = stringResource(R.string.rules_bluff_title),
                        body = stringResource(R.string.rules_bluff_body)
                    )
                }
                item {
                    SectionHeader(
                        title = stringResource(R.string.rules_figures_title),
                        subtitle = stringResource(R.string.rules_figures_body),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(state.roster, key = { it.type.name }) { figure ->
                    FigureRow(figure)
                }
                item {
                    SectionHeader(
                        title = stringResource(R.string.rules_events_title),
                        subtitle = stringResource(R.string.rules_events_body),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(state.events, key = { it.name }) { card ->
                    EventRow(card)
                }
                item {
                    RuleCard(
                        title = stringResource(R.string.rules_chains_title),
                        body = stringResource(R.string.rules_chains_body)
                    )
                }
                item {
                    RuleCard(
                        title = stringResource(R.string.rules_fair_title),
                        body = stringResource(R.string.disclaimer)
                    )
                }
            }

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun RuleCard(title: String, body: String) {
    ParchmentPanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 12.sp,
            letterSpacing = 2.4.sp,
            fontWeight = FontWeight.Bold,
            color = PolishedSilver
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun FigureRow(figure: CourtFigure) {
    ParchmentPanel(modifier = Modifier.fillMaxWidth(), contentPadding = 10) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(spriteOf(figure.type)),
                contentDescription = figureLabel(figure.type),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.figure_headline,
                        figureLabel(figure.type),
                        figure.strength,
                        figure.cost
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = Parchment
                )
                Text(
                    text = figureAbility(figure.type),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun EventRow(card: EventCard) {
    ParchmentPanel(modifier = Modifier.fillMaxWidth(), contentPadding = 10, fillAlpha = 0.06f) {
        Text(
            text = eventLabel(card),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
            color = PolishedSilver
        )
        Text(
            text = eventRule(card),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}
