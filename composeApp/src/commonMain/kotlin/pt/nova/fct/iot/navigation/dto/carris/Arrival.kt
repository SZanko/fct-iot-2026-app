package pt.nova.fct.iot.navigation.dto.carris

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Arrival(
    @SerialName("estimated_arrival_unix")
    val estimatedArrivalTimeUnix: Long?,
    @SerialName("observed_arrival_unix")
    val observedArrivalTimeUnix: Long?,
    @SerialName("scheduled_arrival_unix")
    val scheduledArrivalTimeUnix: Long?,
    @SerialName("line_id")
    val lineId: UShort,
    @SerialName("headsign")
    val headsign: String,
    @SerialName("scheduled_arrival")
    val scheduledArrival: String?,
)
