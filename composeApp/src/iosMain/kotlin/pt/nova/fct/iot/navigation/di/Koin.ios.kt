package pt.nova.fct.iot.navigation.di

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import pt.nova.fct.iot.navigation.db.AppDatabase
import pt.nova.fct.iot.navigation.db.buildAppDatabase

class IOSComponent : PlatformComponent {
    override fun getInfo(): String = "iOS"
}

actual fun platformModule(): Module = module {
    single<PlatformComponent> { IOSComponent() }
    single { buildAppDatabase(getDatabaseBuilder()) }
}

private fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val databaseFilePath = documentDirectory() + "/public_transport_iot.db"
    return Room.databaseBuilder<AppDatabase>(name = databaseFilePath)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )

    return requireNotNull(directory?.path)
}
