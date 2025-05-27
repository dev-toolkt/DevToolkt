@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

expect class PlatformWeakReference<T : Any>(value: T) {
    fun get(): T?
}
