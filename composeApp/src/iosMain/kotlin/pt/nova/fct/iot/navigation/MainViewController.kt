package pt.nova.fct.iot.navigation

import androidx.compose.ui.window.ComposeUIViewController
import pt.nova.fct.iot.navigation.di.initKoin

fun MainViewController() = run {
    initKoin()
    ComposeUIViewController { App() }
}
