package pt.nova.fct.iot.navigation.dto.carris

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Arrival(
    @SerialName("estimated_arrival")
    val estimatedArrival: String? = null,
    @SerialName("estimated_arrival_unix")
    val estimatedArrivalTimeUnix: Long? = null,
    @SerialName("observed_arrival")
    val observedArrival: String? = null,
    @SerialName("observed_arrival_unix")
    val observedArrivalTimeUnix: Long? = null,
    @SerialName("scheduled_arrival")
    val scheduledArrival: String? = null,
    @SerialName("scheduled_arrival_unix")
    val scheduledArrivalTimeUnix: Long? = null,
    @SerialName("line_id")
    val lineId: String,
    val headsign: String,
    @SerialName("pattern_id")
    val patternId: String? = null,
    @SerialName("route_id")
    val routeId: String? = null,
    @SerialName("stop_sequence")
    val stopSequence: Long? = null,
    @SerialName("trip_id")
    val tripId: String? = null,
    @SerialName("vehicle_id")
    val vehicleId: String? = null,
)
