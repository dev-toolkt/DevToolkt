package dev.toolkt.core.collections

interface MutableMultiValuedMap<K, V> : MultiValuedMap<K, V> {
    companion object {
        fun <K, V> newFromMap(
            backingMap: MutableMap<K, MutableSet<V>>,
        ): MutableMultiValuedMap<K, V> = MapBackedMultiValuedMap(
            backingMap = backingMap,
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
     * @return true if the mapping was added, false if it was already present.
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

@Suppress("NOTHING_TO_INLINE")
inline fun <K, V> mutableMultiValuedMapOf(
    vararg pairs: Pair<K, V>,
): MutableMultiValuedMap<K, V> = MutableMultiValuedMap.newFromMap(
    backingMap = pairs.groupBy { (key, _) -> key }.mapValues { (_, keyPairs) ->
        keyPairs.map { (_, value) -> value }.toMutableSet()
    }.toMutableMap(),
)
