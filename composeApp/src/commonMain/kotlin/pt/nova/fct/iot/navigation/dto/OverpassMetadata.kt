package pt.nova.fct.iot.navigation.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OverpassMetadata(
    @SerialName("timestamp_osm_base")
    val timestampOsmBase: String,
    val copyright: String,
)
