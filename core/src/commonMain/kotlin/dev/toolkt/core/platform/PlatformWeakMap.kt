@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

expect class PlatformWeakMap<K : Any, V : Any>() : MutableMap<K, V> {
    class Handle<K: Any, V: Any>

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

    /**
     * Adds the specified entry to the set, returning a handle.
     *
     * @return a handle if the entry has been added, `null` if the entry with the
     * given key was already present in the map.
     */
    fun add(key: K, value: V): Handle<K, V>?

    /**
     * Removes the entry corresponding to the given [handle] from this map.
     *
     * @return `true` if the entry has been successfully removed; `false` if it
     * was not present in the collection.
     */
    fun removeHandled(handle: Handle<K, V>): Boolean
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableWeakMapOf(): PlatformWeakMap<K, V> = PlatformWeakMap()
