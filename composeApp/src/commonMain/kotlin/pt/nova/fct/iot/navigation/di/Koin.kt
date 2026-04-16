package pt.nova.fct.iot.navigation.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import pt.nova.fct.iot.navigation.db.AppDatabase
import pt.nova.fct.iot.navigation.services.CarrisApi
import pt.nova.fct.iot.navigation.services.CarrisService
import pt.nova.fct.iot.navigation.services.NearestBusStopService
import pt.nova.fct.iot.navigation.services.OsmApi
import pt.nova.fct.iot.navigation.services.OsmService
import pt.nova.fct.iot.navigation.services.createCarrisApi
import pt.nova.fct.iot.navigation.services.createOsmApi

interface PlatformComponent {
    fun getInfo(): String
}

expect fun platformModule() : Module

val nativeComponentModule = module {
    //single<NativeComponent>()
}

val dataModule = module {
    single { buildClient() }
    single(named(OSM_KTORFIT)) { buildOsmKtorfit(get()) }
    single(named(CARRIS_KTORFIT)) { buildCarrisKtorfit(get()) }
    single<OsmApi> { get<Ktorfit>(named(OSM_KTORFIT)).createOsmApi() }
    single<CarrisApi> { get<Ktorfit>(named(CARRIS_KTORFIT)).createCarrisApi() }
    single { OsmService(get()) }
    single { CarrisService(get()) }
    single { NearestBusStopService(get(), get(), get()) }
    single { get<AppDatabase>().busStopDao() }
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

private fun buildOsmKtorfit(client: HttpClient): Ktorfit = Ktorfit.Builder()
    //.baseUrl("https://overpass-api.de/api/")
    .baseUrl("https://overpass.private.coffee/api/")
    .httpClient(client)
    .build()

private fun buildCarrisKtorfit(client: HttpClient): Ktorfit = Ktorfit.Builder()
    .baseUrl("https://api.carrismetropolitana.pt/v2/")
    .httpClient(client)
    .build()

val viewModelModule = module {
    //viewModel<ListViewModel>()
    //viewModel<DetailViewModel>()
}

val appModule = module {
    includes(dataModule, viewModelModule, nativeComponentModule, platformModule())
}
private val log = KotlinLogging.logger {  }

fun initKoin(configuration: KoinAppDeclaration? = null) {
    if (runCatching { KoinPlatform.getKoin() }.isSuccess) {
        return
    }

    startKoin {
        configuration?.invoke(this)
        modules(appModule)
        printLogger(Level.DEBUG)
    }

    val platformInfo = KoinPlatform.getKoin().get<PlatformComponent>().getInfo()
    log.info { "-- Expect/Actual Module and Interface Definition -- Running on: $platformInfo" }
}

private const val OSM_KTORFIT = "osmKtorfit"
private const val CARRIS_KTORFIT = "carrisKtorfit"
