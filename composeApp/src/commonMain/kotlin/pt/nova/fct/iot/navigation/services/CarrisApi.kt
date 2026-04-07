package pt.nova.fct.iot.navigation.services

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import pt.nova.fct.iot.navigation.dto.carris.Arrival
import pt.nova.fct.iot.navigation.dto.carris.Stop

interface CarrisApi {

    //const val baseUrl = "https://api.carrismetropolitana.pt/v2";

    @GET("arrivals/by_stop/{stop}")
    suspend fun arrivalsByStop(
        @Path("stop") stop: String
    ): List<Arrival>

    @GET("stops")
    suspend fun getAllStops(): List<Stop>
}