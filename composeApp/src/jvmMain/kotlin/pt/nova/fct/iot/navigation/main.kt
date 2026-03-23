package pt.nova.fct.iot.navigation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PublicTransportIoT",
    ) {
        App()
    }
}