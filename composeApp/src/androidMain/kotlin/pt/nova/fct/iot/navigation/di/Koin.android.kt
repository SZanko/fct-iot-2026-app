package pt.nova.fct.iot.navigation.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import pt.nova.fct.iot.navigation.db.AppDatabase
import pt.nova.fct.iot.navigation.db.buildAppDatabase
import pt.nova.fct.iot.navigation.services.AndroidLocationProvider
import pt.nova.fct.iot.navigation.services.LocationProvider

class AndroidComponent : PlatformComponent {
    override fun getInfo(): String = "Android OS"
}

actual fun platformModule(): Module = module {
    single<PlatformComponent> { AndroidComponent() }
    single<LocationProvider> { AndroidLocationProvider(get()) }
    single { buildAppDatabase(getDatabaseBuilder(get())) }
}

private fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath("public_transport_iot.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
    )
}
