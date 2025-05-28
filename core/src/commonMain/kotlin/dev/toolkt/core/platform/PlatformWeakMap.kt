@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

expect class PlatformWeakMap<K : Any, V : Any>() : MutableMap<K, V> {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
    override fun isEmpty(): Boolean
    override fun containsKey(key: K): Boolean
    override fun containsValue(value: V): Boolean
    override fun get(key: K): V?
    override val keys: MutableSet<K>
    override val size: Int

    override val values: MutableCollection<V>
    override fun put(key: K, value: V): V?
    override fun remove(key: K): V?
    override fun putAll(from: Map<out K, V>)
    override fun clear()
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableWeakMapOf(): MutableMap<K, V> = PlatformWeakMap()
