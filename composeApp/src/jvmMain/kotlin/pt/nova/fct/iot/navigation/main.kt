package pt.nova.fct.iot.navigation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pt.nova.fct.iot.navigation.di.initKoin
import pt.nova.fct.iot.navigation.services.OsmService

private val log = KotlinLogging.logger {}

fun main() {
    initKoin()

    val startupScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    //startupScope.launch {
    //    runCatching {
    //        KoinPlatform.getKoin().get<OsmService>().getNearestPublicTransportSpot(
    //            latitude = 38.672817f,
    //            longitude = -9.232244f,
    //        )
    //    }.onSuccess { response ->
    //        log.info { "Startup OSM response contains ${response.elements.size} stops" }
    //        response.elements.firstOrNull()?.let { firstStop ->
    //            val firstStopName = firstStop.tags["name"]
    //            val firstStopRef = firstStop.tags["ref"]
    //            log.info { "First stop: id=${firstStop.id}, name=$firstStopName, ref=$firstStopRef" }
    //        }
    //    }.onFailure { error ->
    //        log.error(error) { "Failed to fetch startup OSM response" }
    //    }
    //}

    application {
        Window(
            onCloseRequest = {
                startupScope.cancel()
                exitApplication()
            },
        title = "PublicTransportIoT",
        ) {
            App()
        }
    }
}
