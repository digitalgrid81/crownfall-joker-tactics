package com.Ritzava1.Mewlup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.Ritzava1.Mewlup.core.di.ServiceLocator
import com.Ritzava1.Mewlup.core.theme.AppTheme
import com.Ritzava1.Mewlup.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ServiceLocator.start(applicationContext)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
