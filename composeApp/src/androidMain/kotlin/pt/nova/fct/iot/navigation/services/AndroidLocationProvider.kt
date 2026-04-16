package pt.nova.fct.iot.navigation.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidLocationProvider(
    private val context: Context,
) : LocationProvider {

    override suspend fun currentLocation(): Coordinates {
        if (!hasLocationPermission()) {
            error("Location permission is required to find the nearest bus stop. Grant location permission and try again.")
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            .filter { provider -> runCatching { locationManager.isProviderEnabled(provider) }.getOrDefault(false) }

        if (providers.isEmpty()) {
            error("No location provider is enabled. Enable device location and try again.")
        }

        latestKnownLocation(locationManager, providers)?.let { location ->
            return location.toCoordinates()
        }

        return withTimeout(15_000L) {
            requestSingleLocation(locationManager, providers.first())
        }
    }

    private fun hasLocationPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun latestKnownLocation(
        locationManager: LocationManager,
        providers: List<String>,
    ): Location? {
        return providers
            .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull { location -> location.time }
    }

    private suspend fun requestSingleLocation(
        locationManager: LocationManager,
        provider: String,
    ): Coordinates = suspendCancellableCoroutine { continuation ->
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationManager.removeUpdates(this)
                if (continuation.isActive) {
                    continuation.resume(location.toCoordinates())
                }
            }

            @Deprecated("Deprecated by Android")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

            override fun onProviderEnabled(provider: String) = Unit

            override fun onProviderDisabled(provider: String) = Unit
        }

        continuation.invokeOnCancellation {
            locationManager.removeUpdates(listener)
        }

        runCatching {
            locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
        }.onFailure { error ->
            locationManager.removeUpdates(listener)
            if (continuation.isActive) {
                continuation.resumeWithException(error)
            }
        }
    }

    private fun Location.toCoordinates(): Coordinates {
        return Coordinates(latitude = latitude, longitude = longitude)
    }
}
