package pt.nova.fct.iot.navigation.dto

import kotlinx.serialization.Serializable

@Serializable
data class OverpassElement(
    val type: String,
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val tags: Map<String, String> = emptyMap(),
)
