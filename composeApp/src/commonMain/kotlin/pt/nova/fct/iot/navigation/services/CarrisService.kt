package pt.nova.fct.iot.navigation.services

import io.github.oshai.kotlinlogging.KotlinLogging
import pt.nova.fct.iot.navigation.dto.carris.Arrival
import pt.nova.fct.iot.navigation.dto.carris.Stop

class CarrisService(
    private val carrisApi: CarrisApi,
) {

    companion object {
        private val log = KotlinLogging.logger { }
    }

    suspend fun getArrivalsByStop(stopId: String): List<Arrival> {
        return carrisApi.arrivalsByStop(stopId).also { arrivals ->
            log.info { "Fetched ${arrivals.size} arrivals for Carris stop $stopId" }
        }
    }

    suspend fun getAllStops(): List<Stop> {
        return carrisApi.getAllStops().also { stops ->
            log.info { "Fetched ${stops.size} Carris stops" }
        }
    }
}
