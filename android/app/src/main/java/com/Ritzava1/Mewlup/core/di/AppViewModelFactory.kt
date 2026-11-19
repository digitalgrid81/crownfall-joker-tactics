package com.Ritzava1.Mewlup.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.Ritzava1.Mewlup.presentation.app.AppViewModel
import com.Ritzava1.Mewlup.presentation.campaign.CampaignViewModel
import com.Ritzava1.Mewlup.presentation.game.GameViewModel
import com.Ritzava1.Mewlup.presentation.gameover.GameOverViewModel
import com.Ritzava1.Mewlup.presentation.menu.MenuViewModel
import com.Ritzava1.Mewlup.presentation.replay.ReplayViewModel
import com.Ritzava1.Mewlup.presentation.rules.RulesViewModel
import com.Ritzava1.Mewlup.presentation.splash.SplashViewModel

object AppViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.progressRepository(),
                ServiceLocator.settingsRepository(),
                ServiceLocator.replayRepository()
            )

            modelClass.isAssignableFrom(CampaignViewModel::class.java) -> CampaignViewModel(
                ServiceLocator.progressRepository(),
                ServiceLocator.settingsRepository()
            )

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ServiceLocator.settingsRepository(),
                ServiceLocator.progressRepository(),
                ServiceLocator.replayRepository()
            )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ServiceLocator.progressRepository()
            )

            modelClass.isAssignableFrom(ReplayViewModel::class.java) -> ReplayViewModel(
                ServiceLocator.replayRepository(),
                ServiceLocator.settingsRepository(),
                ServiceLocator.progressRepository()
            )

            modelClass.isAssignableFrom(RulesViewModel::class.java) -> RulesViewModel()
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
