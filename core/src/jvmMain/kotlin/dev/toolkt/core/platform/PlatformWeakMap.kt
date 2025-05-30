package dev.toolkt.core.platform

import java.lang.ref.WeakReference
import java.util.WeakHashMap

actual class PlatformWeakMap<K : Any, V : Any> : AbstractMutableMap<K, V>() {
    actual data class Handle<K : Any, V : Any> internal constructor(
        internal val keyWeakReference: WeakReference<K>,
    )

    private val weakHashMap = WeakHashMap<K, V>()

    actual override val keys: MutableSet<K>
        get() = weakHashMap.keys

    actual override val values: MutableCollection<V>
        get() = weakHashMap.values

    actual override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = weakHashMap.entries

    actual override val size: Int
        get() = weakHashMap.size

    actual override fun put(key: K, value: V): V? = weakHashMap.put(key, value)

    actual override fun putAll(from: Map<out K, V>) {
        weakHashMap.putAll(from)
    }

    actual override fun clear() {
        weakHashMap.clear()
    }

    actual override fun remove(key: K): V? = weakHashMap.remove(key)

    override fun remove(key: K, value: V): Boolean = weakHashMap.remove(key, value)

    actual override fun containsKey(key: K): Boolean = weakHashMap.containsKey(key)

    actual override fun containsValue(value: V): Boolean = weakHashMap.containsValue(value)

    actual override fun get(key: K): V? = weakHashMap[key]

    override fun getOrDefault(key: K, defaultValue: V): V = weakHashMap.getOrDefault(key, defaultValue)

    override fun equals(other: Any?): Boolean {
        if (other !is PlatformWeakMap<K, V>) return false
        return weakHashMap == other.weakHashMap
    }

    override fun hashCode(): Int = weakHashMap.hashCode()

    override fun toString(): String = "PlatformWeakMap($weakHashMap)"

    override fun clone(): Any? {
        throw UnsupportedOperationException()
    }

    actual fun add(
        key: K,
        value: V,
    ): Handle<K, V>? {
        val previousValue = weakHashMap.put(key, value)
        
        return when {
            previousValue == null -> Handle(
                keyWeakReference = WeakReference(key),
            )

            else -> null
        }
    }

    actual fun remove(
        handle: Handle<K, V>,
    ): Boolean {
        val key = handle.keyWeakReference.get() ?: return false

        val previousValue = weakHashMap.remove(key)

        return previousValue != null
    }
}
