package pt.nova.fct.iot.navigation.dto

import kotlinx.serialization.Serializable

@Serializable
data class OverpassResponse(
    val version: Double,
    val generator: String,
    val osm3s: OverpassMetadata,
    val elements: List<OverpassElement>,
)
