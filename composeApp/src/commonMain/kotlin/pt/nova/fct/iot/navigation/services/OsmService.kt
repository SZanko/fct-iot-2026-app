package pt.nova.fct.iot.navigation.services

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import pt.nova.fct.iot.navigation.dto.OverpassResponse

class OsmService(
    private val osmApi: OsmApi
) {

    companion object {
        private val log = KotlinLogging.logger {}
        private val json = Json { ignoreUnknownKeys = true }
        private const val MAX_REENQUEUES = 3
    }

    suspend fun getNearestPublicTransportSpot(
        around: Int = 50,
        latitude: Float,
        longitude: Float
    ): OverpassResponse {
        // (around:500,38.672817,-9.232244)

        val query = """
        [out:json];
        (
          node(around:$around,$latitude,$longitude)["highway"="bus_stop"];
          node(around:$around,$latitude,$longitude)["public_transport"="platform"];
        );
        out;
    """.trimIndent()


        return try {
            fetchOverpassResponse(query).also { response ->
                log.info { "Fetched ${response.elements.size} nearby stops from Overpass" }
            }
        } catch (e: Exception) {
            log.error(e) { "Failed to query Overpass API" }
            throw e
        }
    }

    private suspend fun fetchOverpassResponse(query: String): OverpassResponse {
        repeat(MAX_REENQUEUES + 1) { attempt ->
            val rawResponse = osmApi.getNearestPublicTransportSpot(query).trim()

            if (rawResponse.startsWith("{")) {
                return json.decodeFromString<OverpassResponse>(rawResponse)
            }

            if (isServerBusyResponse(rawResponse)) {
                check(attempt != MAX_REENQUEUES) {
                    "Overpass API stayed overloaded after ${MAX_REENQUEUES + 1} attempts. " +
                        "Last response started with: ${rawResponse.take(160)}"
                }

                val retryDelayMs = (attempt + 1) * 1_000L
                log.warn {
                    "Overpass API is busy. Re-enqueueing request ${attempt + 1}/$MAX_REENQUEUES in ${retryDelayMs}ms"
                }
                delay(retryDelayMs)
                return@repeat
            }

            throw IllegalStateException(
                "Overpass API returned a non-JSON response: ${rawResponse.take(160)}"
            )
        }

        error("Unreachable Overpass retry state")
    }

    private fun isServerBusyResponse(rawResponse: String): Boolean {
        return rawResponse.startsWith("<?xml") ||
            rawResponse.startsWith("<html") ||
            rawResponse.contains("The server is probably too busy", ignoreCase = true) ||
            rawResponse.contains("Dispatcher_Client::request_read_and_idx::timeout", ignoreCase = true)
    }
}
