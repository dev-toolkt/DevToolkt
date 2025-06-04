@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

/**
 * A weak reference to an object of type [T].
 *
 * On JavaScript, there's a restriction that weak references can only be used
 * with objects that are not primitives.
 */
expect class PlatformWeakReference<T : Any>(value: T) {
    fun get(): T?
}
