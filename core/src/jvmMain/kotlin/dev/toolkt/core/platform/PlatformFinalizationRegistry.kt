@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

import java.lang.ref.Cleaner

actual class PlatformFinalizationRegistry {
    private val cleaner = Cleaner.create()

    actual fun register(
        target: Any,
        cleanup: () -> Unit,
    ): PlatformCleanable {
        val cleanable = cleaner.register(target, cleanup)

        return object : PlatformCleanable {
            override fun clean() {
                cleanable.clean()
            }
        }
    }
}
