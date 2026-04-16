package pt.nova.fct.iot.navigation.dto.carris

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stop(
    val id: String,
    val lat: Double,
    val lon: Double,
    @SerialName("long_name")
    val longName: String,
    @SerialName("short_name")
    val shortName: String? = null,
    @SerialName("tts_name")
    val ttsName: String? = null,
    @SerialName("line_ids")
    val lineIds: List<String> = emptyList(),
    @SerialName("route_ids")
    val routeIds: List<String> = emptyList(),
    @SerialName("pattern_ids")
    val patternIds: List<String> = emptyList(),
    @SerialName("district_id")
    val districtId: String? = null,
    @SerialName("locality_id")
    val localityId: String? = null,
    @SerialName("municipality_id")
    val municipalityId: String? = null,
    @SerialName("region_id")
    val regionId: String? = null,
    @SerialName("operational_status")
    val operationalStatus: String? = null,
    val facilities: List<String> = emptyList(),
    @SerialName("wheelchair_boarding")
    val wheelchairBoarding: Boolean? = null,
)
