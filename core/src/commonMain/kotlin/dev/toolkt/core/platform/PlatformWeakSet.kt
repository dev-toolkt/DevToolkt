@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

expect class PlatformWeakSet<T : Any>() : AbstractMutableSet<T> {
    override val size: Int

    override fun iterator(): MutableIterator<T>

    override fun add(element: T): Boolean
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> mutableWeakSetOf(): MutableSet<T> = PlatformWeakSet()
