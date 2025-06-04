package dev.toolkt.core.platform

actual object PlatformSystem {
    actual fun collectGarbage() {
        System.gc()
    }

    actual fun log(value: Any) {
        println(value)
    }
}
