package dev.toolkt.core.collections

import dev.toolkt.core.platform.PlatformWeakReference

interface ElementRemover {
    /**
     * Removes the element from the collection.
     *
     * @return true if the element was removed, false if it was not present in the collection.
     */
    fun remove(): Boolean
}

/**
 * Removes the element from the collection, throwing an exception if it was not present.
 *
 * @throws IllegalStateException if the element was not present in the collection.
 */
fun ElementRemover.removeEffectively() {
    val wasRemoved = this.remove()

    if (!wasRemoved) {
        throw IllegalStateException("The collection didn't contain the element")
    }
}

/**
 * Inserts the element into the collection.
 *
 * @return an [ElementRemover] that can be used to remove the element later
 * (if the element was added), or `null` if the element was already present
 * in the collection.
 */
fun <E> MutableCollection<E>.insert(element: E): ElementRemover? {
    val wasAdded = this.add(element)

    if (!wasAdded) return null

    return object : ElementRemover {
        override fun remove(): Boolean = this@insert.remove(element)
    }
}

/**
 * Inserts the element into the collection without keeping a strong reference
 * to the inserted element.
 *
 * @return an [ElementRemover] that can be used to remove the element later
 * (if the element was added), or `null` if the element was already present
 * in the collection.
 */
fun <E : Any> MutableCollection<E>.insertWeak(element: E): ElementRemover? {
    val wasAdded = this.add(element)

    if (!wasAdded) return null

    val elementWeakRef = PlatformWeakReference(element)

    return object : ElementRemover {
        override fun remove(): Boolean {
            val element = elementWeakRef.get() ?: return false
            return this@insertWeak.remove(element)
        }
    }
}

/**
 * Inserts the element into the collection, throwing an exception if it was
 * already present.
 *
 * @return an [ElementRemover] that can be used to remove the element later.
 *
 * @throws IllegalStateException if the element was already present in the
 * collection.
 */
fun <E> MutableCollection<E>.insertEffectively(
    element: E,
): ElementRemover =
    insert(element) ?: throw IllegalStateException("The collection already contains the element: $element")

/**
 * Inserts the element into the collection without keeping a strong reference
 * to the inserted element, throwing an exception if it was already present.
 *
 * @return an [ElementRemover] that can be used to remove the element later.
 *
 * @throws IllegalStateException if the element was already present in the
 * collection.
 */
fun <E : Any> MutableCollection<E>.insertEffectivelyWeak(
    element: E,
): ElementRemover =
    insertWeak(element) ?: throw IllegalStateException("The collection already contains the element: $element")
