package com.Ritzava1.Mewlup.presentation.app

import androidx.lifecycle.ViewModel
import com.Ritzava1.Mewlup.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val history = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) history.add(current)
        _screen.value = target
        _canGoBack.value = history.isNotEmpty()
    }

    fun replace(target: Screen) {
        _screen.value = target
        _canGoBack.value = history.isNotEmpty()
    }

    fun back(): Boolean {
        if (history.isEmpty()) return false
        val previous = history.removeAt(history.lastIndex)
        _screen.value = previous
        _canGoBack.value = history.isNotEmpty()
        return true
    }

    fun toMenu() {
        history.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }
}
