package dev.toolkt.core.platform

import java.util.WeakHashMap

actual class PlatformWeakMap<K : Any, V : Any> : AbstractMutableMap<K, V>() {
    private val weakHashMap = WeakHashMap<K, V>()

    override val keys: MutableSet<K>
        get() = weakHashMap.keys

    override val values: MutableCollection<V>
        get() = weakHashMap.values

    actual override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = weakHashMap.entries

    override val size: Int
        get() = weakHashMap.size

    actual override fun put(key: K, value: V): V? = weakHashMap.put(key, value)

    override fun putAll(from: Map<out K, V>) {
        weakHashMap.putAll(from)
    }

    override fun clear() {
        weakHashMap.clear()
    }

    override fun remove(key: K): V? = weakHashMap.remove(key)

    override fun remove(key: K, value: V): Boolean = weakHashMap.remove(key, value)

    override fun containsKey(key: K): Boolean = weakHashMap.containsKey(key)

    override fun containsValue(value: V): Boolean = weakHashMap.containsValue(value)

    override fun get(key: K): V? = weakHashMap[key]

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
}
