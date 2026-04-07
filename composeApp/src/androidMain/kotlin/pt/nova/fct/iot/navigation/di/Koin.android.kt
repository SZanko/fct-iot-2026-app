package pt.nova.fct.iot.navigation.di

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

class AndroidComponent : PlatformComponent {
    override fun getInfo(): String = "Android OS"
}

actual fun platformModule(): Module = module {
    single<PlatformComponent> { AndroidComponent() }
}
