package ru.performanceugc.lr3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ru.performanceugc.lr3.navigation.StudioNavigation
import ru.performanceugc.lr3.ui.common.Paper
import ru.performanceugc.lr3.ui.common.StudioTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudioTheme {
                Box(Modifier.fillMaxSize().background(Paper).imePadding().safeDrawingPadding()) {
                    StudioNavigation()
                }
            }
        }
    }
}
