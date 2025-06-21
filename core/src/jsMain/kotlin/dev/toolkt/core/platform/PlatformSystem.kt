package dev.toolkt.core.platform

actual object PlatformSystem {
    actual fun collectGarbage() {
    }

    actual fun log(value: Any) {
        console.log(value)
    }
}
