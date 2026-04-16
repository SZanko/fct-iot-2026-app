package pt.nova.fct.iot.navigation.services

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
)

interface LocationProvider {
    suspend fun currentLocation(): Coordinates
}
