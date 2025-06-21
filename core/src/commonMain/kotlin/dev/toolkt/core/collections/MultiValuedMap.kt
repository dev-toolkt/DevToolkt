package dev.toolkt.core.collections

interface MultiValuedMap<K, out V> {
    /**
     * Returns a view of this multivalued map as a Map from each distinct key to the non-empty collection of that key's associated values.
     */
    fun asMap(): Map<K, Collection<V>>

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    fun containsKey(key: K): Boolean

    /**
     * Checks whether the map contains a mapping for the specified key and value.
     */
    fun containsMapping(key: K, value: @UnsafeVariance V): Boolean

    /**
     * Checks whether the map contains at least one mapping for the specified value.
     */
    fun containsValue(value: @UnsafeVariance V): Boolean

    /**
     * Returns a Collection view of the mappings contained in this multivalued map.
     */
    val entries: Collection<Map.Entry<K, V>>

    /**
     * Gets a view collection of the values associated with the specified key.
     */
    fun get(key: K): Collection<V>

    /**
     * @return true if this map contains no key-value mappings.
     */
    fun isEmpty(): Boolean

    /**
     * A Set view of the keys contained in this multivalued map.
     */
    val keys: Set<K>

    /**
     * The total size of the map.
     */
    val size: Int

    /**
     * A collection view of all values contained in this multivalued map.
     */
    val values: Collection<V>
}

inline fun <K, V> MultiValuedMap<K, V>.forEach(
    action: (Map.Entry<K, V>) -> Unit
) {
    this.entries.forEach(action)
}
