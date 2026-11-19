package com.Ritzava1.Mewlup.presentation.app

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Ritzava1.Mewlup.core.di.AppViewModelFactory
import com.Ritzava1.Mewlup.core.navigation.Screen
import com.Ritzava1.Mewlup.presentation.campaign.CampaignScreen
import com.Ritzava1.Mewlup.presentation.game.GameScreen
import com.Ritzava1.Mewlup.presentation.gameover.GameOverScreen
import com.Ritzava1.Mewlup.presentation.menu.MenuScreen
import com.Ritzava1.Mewlup.presentation.replay.ReplayScreen
import com.Ritzava1.Mewlup.presentation.rules.RulesScreen
import com.Ritzava1.Mewlup.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    BackHandler(enabled = screen != Screen.Splash) {
        if (!appViewModel.back()) {
            activity?.finish()
        }
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = { fadeIn(tween(320)) togetherWith fadeOut(tween(260)) },
        label = "screenHost",
        modifier = Modifier.fillMaxSize()
    ) { current ->
        when (current) {
            Screen.Splash -> SplashScreen(
                onFinished = { appViewModel.replace(Screen.Menu) }
            )

            Screen.Menu -> MenuScreen(
                onPlay = { request -> appViewModel.navigate(Screen.Game(request)) },
                onCampaign = { tab -> appViewModel.navigate(Screen.Campaign(tab)) },
                onHistory = { appViewModel.navigate(Screen.Replay(0L)) },
                onRules = { appViewModel.navigate(Screen.Rules) }
            )

            is Screen.Campaign -> CampaignScreen(
                challengeTab = current.challengeTab,
                onMenu = appViewModel::toMenu,
                onStartMatch = { request -> appViewModel.navigate(Screen.Game(request)) }
            )

            is Screen.Game -> GameScreen(
                request = current.request,
                onGameOver = { summary -> appViewModel.replace(Screen.GameOver(summary)) },
                onMenu = appViewModel::toMenu,
                onRules = { appViewModel.navigate(Screen.Rules) }
            )

            is Screen.GameOver -> GameOverScreen(
                summary = current.summary,
                onPlayAgain = { request -> appViewModel.replace(Screen.Game(request)) },
                onReplay = { id -> appViewModel.navigate(Screen.Replay(id)) },
                onMenu = appViewModel::toMenu
            )

            is Screen.Replay -> ReplayScreen(
                recordId = current.recordId,
                onMenu = appViewModel::toMenu,
                onPlay = { request -> appViewModel.navigate(Screen.Game(request)) }
            )

            Screen.Rules -> RulesScreen(
                onMenu = appViewModel::toMenu
            )
        }
    }
}
