package com.Ritzava1.Mewlup.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Ritzava1.Mewlup.R
import com.Ritzava1.Mewlup.core.config.GameConfig
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
import com.Ritzava1.Mewlup.core.ui.DistrictTile
import com.Ritzava1.Mewlup.core.ui.EventBanner
import com.Ritzava1.Mewlup.core.ui.FigureChip
import com.Ritzava1.Mewlup.core.ui.PrimaryButton
import com.Ritzava1.Mewlup.core.ui.ScreenBackground
import com.Ritzava1.Mewlup.core.ui.SecondaryButton
import com.Ritzava1.Mewlup.core.ui.StatCard
import com.Ritzava1.Mewlup.core.ui.eventLabel
import com.Ritzava1.Mewlup.core.ui.eventRule
import com.Ritzava1.Mewlup.core.ui.factionLabel
import com.Ritzava1.Mewlup.core.ui.figureLabel
import com.Ritzava1.Mewlup.domain.model.FigureType
import com.Ritzava1.Mewlup.domain.model.MatchPhase
import com.Ritzava1.Mewlup.domain.model.MatchRequest
import com.Ritzava1.Mewlup.domain.model.MatchSummary
import com.Ritzava1.Mewlup.domain.model.Owner
import com.Ritzava1.Mewlup.presentation.dialog.ConfirmEndTurnDialog
import com.Ritzava1.Mewlup.presentation.dialog.PauseDialog

private const val BOARD_FRAME_DP = 8
private const val BOARD_MAX_DP = 380

@Composable
fun GameScreen(
    request: MatchRequest,
    onGameOver: (MatchSummary) -> Unit,
    onMenu: () -> Unit,
    onRules: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(request) {
        viewModel.prepare(request)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onGameOver(event.summary)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }

    if (state.paused) {
        PauseDialog(
            onResume = viewModel::resume,
            onRules = onRules,
            onMenu = onMenu,
            onDismiss = viewModel::resume
        )
    }
    if (state.confirmVisible) {
        ConfirmEndTurnDialog(
            onConfirm = viewModel::confirmFlip,
            onDismiss = viewModel::dismissConfirm
        )
    }

    ScreenBackground(
        overlay = listOf(
            MidnightBlue.copy(alpha = 0.78f),
            DeepVoid.copy(alpha = 0.92f)
        ),
        glow = RoyalCrimson,
        glowAlpha = 0.14f,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(
                turnLine = stringResource(R.string.turn_counter, state.turn, state.totalTurns),
                courtLine = stringResource(R.string.court_of, factionLabel(state.faction)),
                seedLine = stringResource(R.string.seed_chip, state.seed),
                finalTurn = state.finalTurn,
                onPause = viewModel::pause
            )

            Text(
                text = hintText(state),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 1.4.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BoardArea(
                    state = state,
                    onTile = viewModel::selectDistrict
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                StatCard(
                    value = state.influence.toString(),
                    label = stringResource(R.string.stat_influence),
                    accent = RoyalCrimson,
                    announce = true,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.favor.toString(),
                    label = stringResource(R.string.stat_favor),
                    accent = Parchment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.rivalInfluence.toString(),
                    label = stringResource(R.string.stat_rival),
                    accent = RoyalBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            val banner = state.bannerEvent
            if (banner != null) {
                EventBanner(
                    visible = true,
                    title = eventLabel(banner),
                    rule = eventRule(banner),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            ControlsBlock(
                state = state,
                onFigure = viewModel::selectFigure,
                onDecoy = viewModel::decoy,
                onUnmask = viewModel::unmask,
                onPrimary = viewModel::primaryAction
            )
        }
    }
}

@Composable
private fun GameHeader(
    turnLine: String,
    courtLine: String,
    seedLine: String,
    finalTurn: Boolean,
    onPause: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(DeepVoid.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp)
    ) {
        IconButton(onClick = onPause, modifier = Modifier.size(48.dp)) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = turnLine,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.6.sp,
                color = if (finalTurn) RoyalCrimson else TextPrimary
            )
            Text(
                text = courtLine,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                letterSpacing = 1.6.sp,
                color = TextSecondary
            )
        }
        Text(
            text = seedLine,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            letterSpacing = 0.6.sp,
            color = TextMuted,
            textAlign = TextAlign.End,
            modifier = Modifier.width(62.dp)
        )
    }
}

@Composable
private fun BoardArea(
    state: GameUiState,
    onTile: (Int) -> Unit
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val frame = BOARD_FRAME_DP.dp
        val span = min(min(maxWidth, maxHeight) - 24.dp, BOARD_MAX_DP.dp)
        val tile = ((span - frame * 2).value / GameConfig.GRID).toInt().dp
        val board = tile * GameConfig.GRID + frame * 2
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Parchment.copy(alpha = 0.07f),
            border = BorderStroke(2.dp, PolishedSilver.copy(alpha = 0.34f)),
            shadowElevation = 20.dp,
            modifier = Modifier
                .width(board)
                .height(board)
        ) {
            Column(modifier = Modifier.padding(frame)) {
                for (row in 0 until GameConfig.GRID) {
                    Row {
                        for (column in 0 until GameConfig.GRID) {
                            val index = row * GameConfig.GRID + column
                            val district = state.districts.getOrNull(index)
                            if (district != null) {
                                val marker = state.playerOrders
                                    .firstOrNull { it.districtIndex == index }
                                    ?: state.rivalOrders
                                        .firstOrNull { it.districtIndex == index }
                                DistrictTile(
                                    district = district,
                                    tileSize = tile,
                                    selected = state.selectedDistrict == index,
                                    marker = marker,
                                    revealed = state.revealed,
                                    threatened = state.threatOverlay && index in state.threatened,
                                    inChain = index in state.chainCells,
                                    description = district.name,
                                    stateLabel = ownerStateLabel(district.owner),
                                    onClick = { onTile(index) }
                                )
                            } else {
                                Spacer(modifier = Modifier.size(tile))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlsBlock(
    state: GameUiState,
    onFigure: (FigureType) -> Unit,
    onDecoy: () -> Unit,
    onUnmask: () -> Unit,
    onPrimary: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        color = SurfacePanel.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, PolishedSilver.copy(alpha = 0.20f)),
        shadowElevation = 18.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                state.roster.forEach { figure ->
                    FigureChip(
                        figure = figure,
                        label = figureLabel(figure.type),
                        costLine = stringResource(
                            R.string.figure_cost_line,
                            figure.strength,
                            figure.cost
                        ),
                        stateLabel = if (figure.cost <= state.favor) {
                            stringResource(R.string.state_ready)
                        } else {
                            stringResource(R.string.state_no_favor)
                        },
                        selected = state.selectedFigure == figure.type,
                        enabled = state.phase == MatchPhase.ORDERS &&
                            figure.type !in state.spentFigures &&
                            figure.cost <= state.favor,
                        onClick = { onFigure(figure.type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.decoy),
                    onClick = onDecoy,
                    enabled = state.canDecoy,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.unmask),
                    onClick = onUnmask,
                    enabled = state.canUnmask,
                    modifier = Modifier.weight(1f)
                )
            }
            PrimaryButton(
                text = ctaText(state.cta),
                onClick = onPrimary,
                enabled = state.ctaEnabled,
                iconRes = R.drawable.ic_play,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun ctaText(cta: CtaLabel): String = stringResource(
    when (cta) {
        CtaLabel.DEPLOY -> R.string.deploy
        CtaLabel.FLIP_ORDERS -> R.string.flip_orders
        CtaLabel.END_TURN -> R.string.end_turn
    }
)

@Composable
private fun hintText(state: GameUiState): String = stringResource(
    when (state.hint) {
        GameHint.PICK_FIGURE -> R.string.hint_pick_figure
        GameHint.SEAL_ORDER -> R.string.hint_seal_order
        GameHint.FLIP_ORDERS -> R.string.hint_flip_orders
        GameHint.NEXT_TURN -> R.string.hint_next_turn
    }
)

@Composable
private fun ownerStateLabel(owner: Owner): String = stringResource(
    when (owner) {
        Owner.PLAYER -> R.string.owner_yours
        Owner.RIVAL -> R.string.owner_rival
        Owner.NEUTRAL -> R.string.owner_neutral
    }
)
