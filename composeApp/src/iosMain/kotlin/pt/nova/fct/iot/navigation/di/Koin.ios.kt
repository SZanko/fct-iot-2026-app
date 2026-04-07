package pt.nova.fct.iot.navigation.di

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

class IOSComponent : PlatformComponent {
    override fun getInfo(): String = "iOS"
}

actual fun platformModule(): Module = module {
    single<IOSComponent>() bind PlatformComponent::class
}
