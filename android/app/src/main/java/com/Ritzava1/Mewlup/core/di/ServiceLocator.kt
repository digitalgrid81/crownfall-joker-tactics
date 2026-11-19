package com.Ritzava1.Mewlup.core.di

import android.content.Context
import com.Ritzava1.Mewlup.data.local.AppPreferences
import com.Ritzava1.Mewlup.data.repository.ProgressRepositoryImpl
import com.Ritzava1.Mewlup.data.repository.ReplayRepositoryImpl
import com.Ritzava1.Mewlup.data.repository.SettingsRepositoryImpl
import com.Ritzava1.Mewlup.domain.repository.ProgressRepository
import com.Ritzava1.Mewlup.domain.repository.ReplayRepository
import com.Ritzava1.Mewlup.domain.repository.SettingsRepository
import com.Ritzava1.Mewlup.domain.usecase.BuildReplayTakeawayUseCase
import com.Ritzava1.Mewlup.domain.usecase.DrawEventCardUseCase
import com.Ritzava1.Mewlup.domain.usecase.EvaluateChallengeUseCase
import com.Ritzava1.Mewlup.domain.usecase.GenerateBoardUseCase
import com.Ritzava1.Mewlup.domain.usecase.ResolveTurnUseCase
import com.Ritzava1.Mewlup.domain.usecase.RivalCourtAiUseCase
import com.Ritzava1.Mewlup.domain.usecase.ScoreInfluenceUseCase

object ServiceLocator {

    private var preferences: AppPreferences? = null
    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null
    private var replays: ReplayRepository? = null

    val generateBoard = GenerateBoardUseCase()
    val resolveTurn = ResolveTurnUseCase()
    val scoreInfluence = ScoreInfluenceUseCase()
    val rivalCourtAi = RivalCourtAiUseCase()
    val drawEventCard = DrawEventCardUseCase()
    val evaluateChallenge = EvaluateChallengeUseCase()
    val buildTakeaway = BuildReplayTakeawayUseCase()

    fun start(context: Context) {
        if (preferences != null) return
        val store = AppPreferences(context.applicationContext)
        preferences = store
        progress = ProgressRepositoryImpl(store)
        settings = SettingsRepositoryImpl(store)
        replays = ReplayRepositoryImpl(store)
    }

    fun progressRepository(): ProgressRepository = requireNotNull(progress)

    fun settingsRepository(): SettingsRepository = requireNotNull(settings)

    fun replayRepository(): ReplayRepository = requireNotNull(replays)

    fun isReady(): Boolean = preferences != null
}
