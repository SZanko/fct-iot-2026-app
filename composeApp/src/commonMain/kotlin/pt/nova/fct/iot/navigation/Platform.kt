package pt.nova.fct.iot.navigation

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform