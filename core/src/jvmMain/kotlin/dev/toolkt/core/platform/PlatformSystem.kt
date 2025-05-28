package dev.toolkt.core.platform

actual object PlatformSystem {
    actual fun collectGarbage() {
        System.gc()
    }
}
