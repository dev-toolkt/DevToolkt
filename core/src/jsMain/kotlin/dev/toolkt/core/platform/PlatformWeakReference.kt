@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

actual class PlatformWeakReference<T : Any> actual constructor(value: T) {
    private val weakRef = WeakRef(value)

    actual fun get(): T? = weakRef.deref()
}
