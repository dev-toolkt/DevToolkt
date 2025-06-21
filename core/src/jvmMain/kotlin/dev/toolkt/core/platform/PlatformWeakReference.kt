@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

import java.lang.ref.WeakReference

actual class PlatformWeakReference<T : Any> actual constructor(value: T) {
    private val weakReference = WeakReference(value)

    actual fun get(): T? = weakReference.get()
}
