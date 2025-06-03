package dev.toolkt.core.collections

interface MutableMultiValuedMap<K, V> : MultiValuedMap<K, V> {
    companion object {
        fun <K, V> newFromMap(
            map: MutableMap<K, MutableSet<V>>,
        ): MutableMultiValuedMap<K, V> = MapBackedMultiValuedMap(
            backingMap = map,
        )

        fun <K, V> new(): MutableMultiValuedMap<K, V> = newFromMap(mutableMapOf())
    }

    /**
     * Removes all the mappings from this map.
     */
    fun clear()

    /**
     * Adds a key-value mapping to this multivalued map.
     *
     * @return true if the value was added, false if it was already present.
     */
    fun put(key: K, value: V): Boolean

    /**
     * Removes all values associated with the specified key.
     */
    fun remove(key: K): Collection<V>

    /**
     * Removes a key-value mapping from the map.
     *
     * @return true if the mapping was removed, false if it was not present.
     */
    fun removeMapping(key: K, item: V): Boolean
}
