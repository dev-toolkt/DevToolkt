@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

import dev.toolkt.core.collection.CollectionUtils

actual class PlatformWeakSet<T : Any> : AbstractMutableSet<T>() {
    private val weakHashSet: MutableSet<T> = CollectionUtils.newWeakSet()

    actual override fun add(
        element: T,
    ): Boolean = weakHashSet.add(element)

    actual override fun iterator(): MutableIterator<T> = weakHashSet.iterator()

    actual override val size: Int
        get() = weakHashSet.size
}
