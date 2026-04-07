package pt.nova.fct.iot.navigation.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient

class CarrisService(

    private val client: HttpClient
) {

    companion object {
        private val log = KotlinLogging.logger { }
    }
}