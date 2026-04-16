package pt.nova.fct.iot.navigation.ui

import pt.nova.fct.iot.navigation.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}