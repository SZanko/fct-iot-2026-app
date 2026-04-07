package pt.nova.fct.iot.navigation.integrationTest

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import pt.nova.fct.iot.navigation.services.OsmApi
import pt.nova.fct.iot.navigation.services.OsmService
import pt.nova.fct.iot.navigation.services.createOsmApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OsmApiTest {

    @Test
    fun given_koinModule_when_resolvingOsmService_then_jsonResponseIsParsed() = runTest {
        stopKoin()

        var capturedUrl: String? = null
        val expectedResponse = """
            {
              "version": 0.6,
              "generator": "Overpass API",
              "osm3s": {
                "timestamp_osm_base": "2026-04-07T14:29:31Z",
                "copyright": "OpenStreetMap contributors"
              },
              "elements": [
                {
                  "type": "node",
                  "id": 2398521881,
                  "lat": 38.6698614,
                  "lon": -9.23313,
                  "tags": {
                    "name": "EN 377-1 - Bombeiros",
                    "ref": "020539"
                  }
                }
              ]
            }
        """.trimIndent()

        val testModule = module {
            single<HttpClient> {
                HttpClient(MockEngine) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
                    }
                    engine {
                        addHandler { request ->
                            capturedUrl = request.url.toString()
                            respond(
                                content = expectedResponse,
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            )
                        }
                    }
                }
            }
            single<Ktorfit> {
                Ktorfit.Builder()
                    .baseUrl("https://overpass-api.de/api/")
                    .httpClient(get<HttpClient>())
                    .build()
            }
            single<OsmApi> { get<Ktorfit>().createOsmApi() }
            single { OsmService(get()) }
        }

        try {
            startKoin {
                modules(testModule)
            }

            val service = KoinPlatform.getKoin().get<OsmService>()
            assertNotNull(service)

            val response = service.getNearestPublicTransportSpot(
                around = 50,
                latitude = 38.672817f,
                longitude = -9.232244f,
            )

            assertEquals("Overpass API", response.generator)
            assertEquals(1, response.elements.size)
            assertEquals("EN 377-1 - Bombeiros", response.elements.first().tags["name"])
            assertEquals("https://overpass-api.de/api/interpreter", capturedUrl)
        } finally {
            stopKoin()
        }
    }

    @Test
    fun given_busyOverpassResponse_when_callingService_then_reenqueueUpToThreeTimes() = runTest {
        stopKoin()

        var requestCount = 0
        val busyResponse = """
            <?xml version="1.0" encoding="UTF-8"?>
            <html>
            <body>
            <p><strong>Error</strong>: runtime error: open64: 0 Success /osm3s_osm_base Dispatcher_Client::request_read_and_idx::timeout. The server is probably too busy to handle your request.</p>
            </body>
            </html>
        """.trimIndent()
        val jsonResponse = """
            {
              "version": 0.6,
              "generator": "Overpass API",
              "osm3s": {
                "timestamp_osm_base": "2026-04-07T14:29:31Z",
                "copyright": "OpenStreetMap contributors"
              },
              "elements": [
                {
                  "type": "node",
                  "id": 2398521881,
                  "lat": 38.6698614,
                  "lon": -9.23313,
                  "tags": {
                    "name": "EN 377-1 - Bombeiros",
                    "ref": "020539"
                  }
                }
              ]
            }
        """.trimIndent()

        val testModule = module {
            single<HttpClient> {
                HttpClient(MockEngine) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
                    }
                    engine {
                        addHandler { request ->
                            requestCount += 1
                            assertEquals(HttpMethod.Post, request.method)
                            respond(
                                content = if (requestCount < 3) busyResponse else jsonResponse,
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Html.toString())
                            )
                        }
                    }
                }
            }
            single<Ktorfit> {
                Ktorfit.Builder()
                    .baseUrl("https://overpass-api.de/api/")
                    .httpClient(get<HttpClient>())
                    .build()
            }
            single<OsmApi> { get<Ktorfit>().createOsmApi() }
            single { OsmService(get()) }
        }

        try {
            startKoin {
                modules(testModule)
            }

            val service = KoinPlatform.getKoin().get<OsmService>()
            val response = service.getNearestPublicTransportSpot(
                around = 50,
                latitude = 38.672817f,
                longitude = -9.232244f,
            )

            assertEquals(3, requestCount)
            assertEquals(1, response.elements.size)
            assertEquals("020539", response.elements.first().tags["ref"])
        } finally {
            stopKoin()
        }
    }
}
