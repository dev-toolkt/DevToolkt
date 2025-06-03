package dev.toolkt.core.collections

class MapBackedMultiValuedMap<K, V>(
    private val backingMap: MutableMap<K, MutableSet<V>>,
) : MutableMultiValuedMap<K, V> {
    data class MapEntry<K, out V>(
        override val key: K,
        override val value: V,
    ) : Map.Entry<K, V>

    private var cachedSize: Int = backingMap.values.sumOf { it.size }

    override fun clear() {
        backingMap.clear()

        cachedSize = 0
    }

    override fun put(key: K, value: V): Boolean {
        val bucket = backingMap.getOrPut(key) { mutableSetOf() }

        val wasAdded = bucket.add(value)

        if (wasAdded) {
            cachedSize += 1
        }

        return wasAdded
    }

    override fun remove(key: K): Collection<V> {
        val removedBucket = backingMap.remove(key) ?: return emptyList()

        if (removedBucket.isEmpty()) {
            throw AssertionError("Buckets aren't supposed to be empty")
        }

        cachedSize -= removedBucket.size

        return removedBucket
    }

    override fun removeMapping(
        key: K,
        item: V,
    ): Boolean {
        val bucket = backingMap[key] ?: return false

        if (bucket.isEmpty()) {
            throw AssertionError("Buckets aren't supposed to be empty")
        }

        val wasRemoved = bucket.remove(item)

        if (wasRemoved) {
            cachedSize -= 1
        }

        if (bucket.isEmpty()) {
            val removedBucket = backingMap.remove(key)

            if (removedBucket == null) {
                throw AssertionError("The bucket wasn't successfully removed")
            }
        }

        return wasRemoved
    }

    override fun asMap(): Map<K, Collection<V>> = backingMap

    override fun containsKey(
        key: K,
    ): Boolean = backingMap.containsKey(key)

    override fun containsMapping(
        key: K,
        value: V,
    ): Boolean {
        val bucket = backingMap[key] ?: return false

        return bucket.contains(value)
    }

    override fun containsValue(
        value: V,
    ): Boolean = backingMap.any { (_, bucket) ->
        bucket.contains(value)
    }

    override val entries: Collection<Map.Entry<K, V>>
        get() = backingMap.flatMap { (key, values) ->
            values.map { value ->
                MapEntry(
                    key = key,
                    value = value,
                )
            }
        }

    override operator fun get(
        key: K,
    ): Collection<V> = backingMap[key] ?: emptySet()

    override fun isEmpty(): Boolean = backingMap.isEmpty()

    override val keys: Set<K>
        get() = backingMap.keys

    override val size: Int
        get() = cachedSize

    override val values: Collection<V>
        get() = backingMap.values.flatten()
}
