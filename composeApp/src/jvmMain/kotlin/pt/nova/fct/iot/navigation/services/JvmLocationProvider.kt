package pt.nova.fct.iot.navigation.services

class JvmLocationProvider : LocationProvider {
    override suspend fun currentLocation(): Coordinates {
        return Coordinates(latitude = 38.672817, longitude = -9.232244)
    }
}
