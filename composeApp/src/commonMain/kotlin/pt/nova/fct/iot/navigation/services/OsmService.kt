package pt.nova.fct.iot.navigation.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.cio.Request
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode


class OsmService (
    private val client: HttpClient
) : OsmApi {

    companion object {
        private val log = KotlinLogging.logger {}
    }


    suspend fun getNearestPublicTransportSpot(
        around: Int = 500,
        latitude: Float,
        longitude: Float
    ): String {
        // (around:500,38.672817,-9.232244)

        val query = """
        [out:json];
        (
          node(around:$around,$latitude,$longitude)["highway"="bus_stop"];
          node(around:$around,$latitude,$longitude)["public_transport"="platform"];
        );
        out;
    """.trimIndent()


        try {
            val response: HttpResponse = client.post("https://overpass-api.de/api/interpreter") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    listOf("data" to query).formUrlEncode()
                )
            }

            val body = response.bodyAsText()
            log.info { body }

        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }


        return "test";
    }
}