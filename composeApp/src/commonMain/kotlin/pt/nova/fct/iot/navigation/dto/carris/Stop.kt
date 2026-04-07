package pt.nova.fct.iot.navigation.dto.carris

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stop(
    @SerialName("estimated_arrival")
    val estimatedArrival: String?,
    @SerialName("estimated_arrival_unix")
    val estimatedArrivalUnix: Long?,
    val headsign: String,
    @SerialName("line_id")
    val lineId: String,
    @SerialName("observed_arrival")
    val observedArrival: String?,
    @SerialName("observed_arrival_unix")
    val observedArrivalUnix: Long?,
    @SerialName("pattern_id")
    val patternId: String,
    @SerialName("route_id")
    val routeId: String,
    @SerialName("scheduled_arrival")
    val scheduledArrival: String,
    @SerialName("scheduled_arrival_unix")
    val scheduledArrivalUnix: Long,
    @SerialName("stop_sequence")
    val stopSequence: Long,
    @SerialName("trip_id")
    val tripId: String,
    @SerialName("vehicle_id")
    val vehicleId: String?,
)
