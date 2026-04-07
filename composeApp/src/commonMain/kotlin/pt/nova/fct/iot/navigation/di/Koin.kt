package pt.nova.fct.iot.navigation.di

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.includes
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel
import pt.nova.fct.iot.navigation.services.OsmApi
import pt.nova.fct.iot.navigation.services.OsmService

interface PlatformComponent {
    fun getInfo(): String
}

expect fun platformModule() : Module

val nativeComponentModule = module {
    //single<NativeComponent>()
}

val dataModule = module {
    single { create(::buildClient) }
    single<OsmService>() bind OsmService::class
    //single<InMemoryMuseumStorage>() bind MuseumStorage::class
    //single<MuseumRepository>() withOptions { createdAtStart() }
}

private fun buildClient(): HttpClient {
    val json = Json { ignoreUnknownKeys = true }
    return HttpClient {
        install(ContentNegotiation) {
            // TODO Fix API so it serves application/json
            json(json, contentType = ContentType.Any)
        }
    }
}

val viewModelModule = module {
    //viewModel<ListViewModel>()
    //viewModel<DetailViewModel>()
}

val appModule = module {
    includes(dataModule, viewModelModule, nativeComponentModule, platformModule())
}
private val log = KotlinLogging.logger {  }

fun initKoin(configuration: KoinAppDeclaration? = null) {
    startKoin {
        includes(configuration)
        modules(appModule)
        printLogger(Level.DEBUG)
    }

    //val nativeComponent = KoinPlatform.getKoin().get<NativeComponent>().getInfo()
    //log.info{"-- Expect/Actual Definition -- Running on: $nativeComponent"}

    val platformInfo = KoinPlatform.getKoin().get<PlatformComponent>().getInfo()
    log.info { "-- Expect/Actual Module's + Interface Definition -- Running on: $platformInfo" }
}
