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
 * Removes the element from the collection, throwing an exception if it was not
 * present.
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
 * (if it was added), or `null` if the element was already present.
 */
fun <E> MutableCollection<E>.insert(element: E): ElementRemover? {
    val wasAdded = this.add(element)

    if (!wasAdded) return null

    return object : ElementRemover {
        override fun remove(): Boolean = this@insert.remove(element)
    }
}

/**
 * Inserts the mapping into the map.
 *
 * @return an [ElementRemover] that can be used to remove the mapping later (if
 * it was added) or `null` if the mapping was already present
 */
fun <K, V : Any> MutableMultiValuedMap<K, V>.insert(
    key: K,
    value: V,
): ElementRemover? {
    val wasAdded = this.put(
        key = key,
        value = value,
    )

    if (!wasAdded) return null

    return object : ElementRemover {
        override fun remove(): Boolean = this@insert.removeMapping(key, value)
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
 *
 * Inserts the mapping into the map without keeping a strong reference to the
 * inserted key or value.

 * @return an [ElementRemover] that can be used to remove the mapping later
 * (if it was added), or `null` if the mapping was already present
 * in the collection.
 */
fun <K : Any, V : Any> MutableMultiValuedMap<K, V>.insertWeak(
    key: K,
    value: V,
): ElementRemover? {
    val wasAdded = this.put(
        key = key,
        value = value,
    )

    if (!wasAdded) return null

    val keyWeakRef = PlatformWeakReference(key)
    val valueWeakRef = PlatformWeakReference(value)

    return object : ElementRemover {
        override fun remove(): Boolean {
            // If the key or value was collected, it's not possible that the map contains the mapping
            val key = keyWeakRef.get() ?: return false
            val value = valueWeakRef.get() ?: return false

            return this@insertWeak.removeMapping(
                key = key,
                item = value,
            )
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
 * Inserts the mapping into the map, throwing an exception if it was
 * already present.
 *
 * @return an [ElementRemover] that can be used to remove the mapping later.
 *
 * @throws IllegalStateException if the mapping was already present in the map.
 */
fun <K, V : Any> MutableMultiValuedMap<K, V>.insertEffectively(
    key: K,
    value: V,
): ElementRemover =
    insert(key, value) ?: throw IllegalStateException("The map already contains the key: $key with value: $value")

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

/**
 * Inserts the mapping into the map without keeping a strong reference to the
 * inserted key or value, throwing an exception if the mapping was already present.
 *
 * @return an [ElementRemover] that can be used to remove the mapping later.
 *
 * @throws IllegalStateException if the mapping was already present in the map.
 */
fun <K : Any, V : Any> MutableMultiValuedMap<K, V>.insertEffectivelyWeak(
    key: K,
    value: V,
): ElementRemover =
    insertWeak(key, value) ?: throw IllegalStateException("The map already contains the key: $key with value: $value")
