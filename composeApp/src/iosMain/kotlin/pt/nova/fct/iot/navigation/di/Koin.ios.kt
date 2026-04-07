package pt.nova.fct.iot.navigation.di

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

class IOSComponent : PlatformComponent {
    override fun getInfo(): String = "iOS"
}

actual fun platformModule(): Module = module {
    single<PlatformComponent> { IOSComponent() }
}
