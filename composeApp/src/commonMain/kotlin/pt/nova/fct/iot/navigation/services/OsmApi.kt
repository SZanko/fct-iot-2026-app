package pt.nova.fct.iot.navigation.services

import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST
interface OsmApi {
    @FormUrlEncoded
    @POST("interpreter")
    suspend fun getNearestPublicTransportSpot(@Field("data") query: String): String
}
