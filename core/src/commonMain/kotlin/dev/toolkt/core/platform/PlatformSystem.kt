package dev.toolkt.core.platform

expect object PlatformSystem {
    fun collectGarbage()

    fun log(value: Any)
}
