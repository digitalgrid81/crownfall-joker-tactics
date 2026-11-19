package com.Ritzava1.Mewlup.presentation.campaign

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.SurfacePanel
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.ParchmentPanel
import com.Ritzava1.Mewlup.core.ui.PrimaryButton
import com.Ritzava1.Mewlup.core.ui.ScreenBackground
import com.Ritzava1.Mewlup.core.ui.factionAbility
import com.Ritzava1.Mewlup.core.ui.factionColor
import com.Ritzava1.Mewlup.core.ui.factionLabel
import com.Ritzava1.Mewlup.domain.model.Faction
import com.Ritzava1.Mewlup.domain.model.MatchRequest

@Composable
fun CampaignScreen(
    challengeTab: Boolean,
    onMenu: () -> Unit,
    onStartMatch: (MatchRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CampaignViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(challengeTab) {
        viewModel.prepare(challengeTab)
    }

    ScreenBackground(
        overlay = listOf(
            MidnightBlue.copy(alpha = 0.78f),
            SurfacePanel.copy(alpha = 0.80f),
            DeepVoid.copy(alpha = 0.94f)
        ),
        glow = PolishedSilver,
        glowAlpha = 0.12f,
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
                    text = stringResource(R.string.campaign_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.2.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 6.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(factionColor(state.faction).copy(alpha = 0.35f), CircleShape)
                        .border(1.dp, factionColor(state.faction), CircleShape)
                )
            }

            SecondaryTabRow(
                selectedTabIndex = if (state.challengeTab) 1 else 0,
                containerColor = Color.Transparent,
                contentColor = RoyalCrimson,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = !state.challengeTab,
                    onClick = { viewModel.selectTab(false) },
                    selectedContentColor = TextPrimary,
                    unselectedContentColor = TextMuted
                ) {
                    Text(
                        text = stringResource(R.string.campaign),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
                Tab(
                    selected = state.challengeTab,
                    onClick = { viewModel.selectTab(true) },
                    selectedContentColor = TextPrimary,
                    unselectedContentColor = TextMuted
                ) {
                    Text(
                        text = stringResource(R.string.challenge),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (state.challengeTab) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.challenges, key = { it.scenario.id.name }) { card ->
                            ChallengeRow(
                                name = card.scenario.name,
                                rule = card.scenario.rule,
                                target = card.scenario.target,
                                done = card.sealedOff,
                                selected = state.selectedChallenge == card.scenario.id,
                                onClick = { viewModel.selectChallenge(card.scenario.id) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.maps, key = { it.map.index }) { card ->
                            MapRow(
                                name = card.map.name,
                                brief = card.map.brief,
                                seedLabel = stringResource(R.string.seed_chip, card.seed),
                                cleared = card.cleared,
                                locked = card.locked,
                                selected = state.selectedMap == card.map.index,
                                onClick = { viewModel.selectMap(card.map.index) }
                            )
                        }
                    }
                }
            }

            Text(
                text = stringResource(R.string.choose_house),
                style = MaterialTheme.typography.labelSmall,
                color = Parchment,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Faction.entries.forEach { house ->
                    val label = factionLabel(house)
                    val selected = state.faction == house
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                color = if (selected) {
                                    factionColor(house).copy(alpha = 0.22f)
                                } else {
                                    SurfacePanel.copy(alpha = 0.70f)
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) {
                                    RoyalCrimson
                                } else {
                                    PolishedSilver.copy(alpha = 0.22f)
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.selectFaction(house) }
                            .semantics { stateDescription = label }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) TextPrimary else TextSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
            Text(
                text = factionAbility(state.faction),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 8.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.start_match),
                onClick = { onStartMatch(viewModel.buildRequest()) },
                enabled = state.startEnabled,
                iconRes = R.drawable.ic_play,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 14.dp)
            )
        }
    }
}

@Composable
private fun MapRow(
    name: String,
    brief: String,
    seedLabel: String,
    cleared: Boolean,
    locked: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    ParchmentPanel(
        cornerRadius = 16,
        borderAlpha = if (selected) 0.65f else 0.22f,
        fillAlpha = if (selected) 0.14f else 0.07f,
        elevation = if (selected) 18 else 10,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !locked, onClick = onClick)
            .alpha(if (locked) 0.38f else 1f)
            .semantics { stateDescription = seedLabel }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = brief,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Text(
                    text = seedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
            if (locked) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = stringResource(R.string.locked),
                    tint = TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            } else if (cleared) {
                Text(
                    text = stringResource(R.string.cleared_mark),
                    style = MaterialTheme.typography.titleMedium,
                    color = PolishedSilver
                )
            }
        }
    }
}

@Composable
private fun ChallengeRow(
    name: String,
    rule: String,
    target: String,
    done: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    ParchmentPanel(
        cornerRadius = 16,
        borderAlpha = if (selected) 0.65f else 0.22f,
        fillAlpha = if (selected) 0.14f else 0.07f,
        elevation = if (selected) 18 else 10,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .semantics { stateDescription = target }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = rule,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Text(
                    text = target,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Parchment,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
            if (done) {
                Text(
                    text = stringResource(R.string.cleared_mark),
                    style = MaterialTheme.typography.titleMedium,
                    color = PolishedSilver
                )
            }
        }
    }
}
