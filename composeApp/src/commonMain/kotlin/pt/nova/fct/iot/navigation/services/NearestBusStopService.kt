package pt.nova.fct.iot.navigation.services

import pt.nova.fct.iot.navigation.dto.OverpassElement
import pt.nova.fct.iot.navigation.dto.carris.Arrival
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class NearestBusStopService(
    private val locationProvider: LocationProvider,
    private val osmService: OsmService,
    private val carrisService: CarrisService,
) {

    suspend fun getNearestBusStopArrivals(): NearestBusStopResult {
        val userLocation = locationProvider.currentLocation()
        val nearestStop = findNearestStop(userLocation)
        val stopId = nearestStop.carrisStopId()
            ?: error("The nearest OpenStreetMap stop does not have a Carris stop reference.")
        val arrivals = carrisService.getArrivalsByStop(stopId)

        return NearestBusStopResult(
            stopId = stopId,
            stopName = nearestStop.displayName(),
            stopLatitude = requireNotNull(nearestStop.lat),
            stopLongitude = requireNotNull(nearestStop.lon),
            userLocation = userLocation,
            arrivals = arrivals.sortedBy { it.bestArrivalUnix() ?: Long.MAX_VALUE },
        )
    }

    private suspend fun findNearestStop(userLocation: Coordinates): OverpassElement {
        for (radius in SEARCH_RADII_METERS) {
            val response = osmService.getNearestPublicTransportSpot(
                around = radius,
                latitude = userLocation.latitude.toFloat(),
                longitude = userLocation.longitude.toFloat(),
            )
            val nearest = response.elements
                .asSequence()
                .filter { it.lat != null && it.lon != null }
                .filter { it.carrisStopId() != null }
                .minByOrNull { stop ->
                    distanceMeters(
                        userLocation.latitude,
                        userLocation.longitude,
                        requireNotNull(stop.lat),
                        requireNotNull(stop.lon),
                    )
                }

            if (nearest != null) {
                return nearest
            }
        }

        error("No Carris bus stop with a usable reference was found near your location.")
    }

    private fun OverpassElement.displayName(): String {
        return tags["name"]
            ?: tags["addr:street"]
            ?: tags["ref"]
            ?: "OpenStreetMap stop $id"
    }

    private fun OverpassElement.carrisStopId(): String? {
        val candidates = listOfNotNull(
            tags["ref:carris_metropolitana"],
            tags["ref:CarrisMetropolitana"],
            tags["ref:operator"],
            tags["ref"],
        )

        return candidates.firstNotNullOfOrNull { candidate ->
            STOP_ID_REGEX.find(candidate)?.value
        }
    }

    private fun Arrival.bestArrivalUnix(): Long? {
        return estimatedArrivalTimeUnix ?: observedArrivalTimeUnix ?: scheduledArrivalTimeUnix
    }

    private fun distanceMeters(
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double,
    ): Double {
        val dLat = Math.toRadians(latitude2 - latitude1)
        val dLon = Math.toRadians(longitude2 - longitude1)
        val lat1 = Math.toRadians(latitude1)
        val lat2 = Math.toRadians(latitude2)
        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    companion object {
        private val SEARCH_RADII_METERS = listOf(75, 150, 300, 600, 1_000)
        private val STOP_ID_REGEX = Regex("[0-9]{6}")
        private const val EARTH_RADIUS_METERS = 6_371_000.0
    }
}

data class NearestBusStopResult(
    val stopId: String,
    val stopName: String,
    val stopLatitude: Double,
    val stopLongitude: Double,
    val userLocation: Coordinates,
    val arrivals: List<Arrival>,
)
