package pt.nova.fct.iot.navigation.di

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import org.koin.core.module.Module
import org.koin.dsl.module
import pt.nova.fct.iot.navigation.db.AppDatabase
import pt.nova.fct.iot.navigation.db.buildAppDatabase
import pt.nova.fct.iot.navigation.services.JvmLocationProvider
import pt.nova.fct.iot.navigation.services.LocationProvider

class JvmComponent() : PlatformComponent {
    override fun getInfo(): String = "Jvm"
}

actual fun platformModule() : Module = module {
    single<PlatformComponent> { JvmComponent() }
    single<LocationProvider> { JvmLocationProvider() }
    single { buildAppDatabase(getDatabaseBuilder()) }
}

private fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val configHome = System.getenv("XDG_CONFIG_HOME")?.plus("/publictransportiot")
        ?: (System.getProperty("user.home") + "/.config/publictransportiot")
    val dataDirectory = File(configHome)
    dataDirectory.mkdirs()
    val databaseFile = File(dataDirectory, "public_transport_iot.db")

    return Room.databaseBuilder<AppDatabase>(name = databaseFile.absolutePath)
}
