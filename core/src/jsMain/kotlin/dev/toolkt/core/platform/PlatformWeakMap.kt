package dev.toolkt.core.platform

import dev.toolkt.core.iterator.nextOrNull

actual class PlatformWeakMap<K : Any, V : Any> : AbstractMutableMap<K, V>() {
    internal class WeakEntry<K, V>(
        val key: WeakRef<K>,
        var value: V,
    )

    private val weakEntryMap = WeakMap<K, WeakEntry<K, V>>()

    private val weakEntrySet = mutableSetOf<WeakEntry<K, V>>()

    internal fun verifyConsistency() {
        weakEntrySet.forEach {
            val key = it.key.deref() ?: return@forEach

            if (!weakEntryMap.has(key)) {
                throw AssertionError("Key $key is missing from the weak entry map")
            }
        }
    }

    internal fun weakEntryIterator(): MutableIterator<WeakEntry<K, V>> = weakEntrySet.iterator()

    actual override fun get(key: K): V? {
        val bucket = weakEntryMap.get(key) ?: return null

        return bucket.value
    }

    actual override fun put(
        key: K,
        value: V,
    ): V? {
        val bucket = weakEntryMap.get(key)

        when {
            bucket == null -> {
                val newEntry = WeakEntry(
                    key = WeakRef(key),
                    value = value,
                )

                weakEntryMap.set(key, newEntry)
                weakEntrySet.add(newEntry)

                return null
            }

            else -> {
                return bucket.value
            }
        }
    }

    actual override fun remove(key: K): V? {
        // If the weak entry map doesn't contain the key anymore, it doesn't
        // mean that the weak entry set doesn't contain a leftover entry. It
        // just means we have no way of finding it.
        val bucket = weakEntryMap.get(key) ?: return null

        val wasDeleted = weakEntryMap.delete(key)

        if (!wasDeleted) {
            throw AssertionError("The bucket wasn't successfully deleted from the bucket map")
        }

        val wasRemoved = weakEntrySet.remove(bucket)

        if (!wasRemoved) {
            throw AssertionError("The bucket wasn't successfully removed from the bucket set")
        }

        return bucket.value
    }

    actual override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = PlatformWeakMapEntries(platformWeakMap = this)

    actual override val values: MutableCollection<V>
        get() = super.values

    actual override val size: Int
        get() = weakEntrySet.size
}

private class PlatformWeakMapEntries<K : Any, V : Any>(
    private val platformWeakMap: PlatformWeakMap<K, V>,
) : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
    override fun add(
        element: MutableMap.MutableEntry<K, V>,
    ): Boolean = platformWeakMap.put(element.key, element.value) != null

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = PurgingWeakMapIterator(
        entryIterator = platformWeakMap.weakEntryIterator()
    )

    override val size: Int
        get() = platformWeakMap.size
}

private class PurgingWeakMapIterator<K : Any, V : Any>(
    private val entryIterator: MutableIterator<PlatformWeakMap.WeakEntry<K, V>>,
) : MutableIterator<MutableMap.MutableEntry<K, V>> {
    private var peekedEntryData: Pair<K, PlatformWeakMap.WeakEntry<K, V>>? = null

    override fun next(): MutableMap.MutableEntry<K, V> {
        val (peekedKey, peekedEntry) = this.peekedEntryData
            ?: throw UnsupportedOperationException("Calling next() without preceding hasNext() is not supported")

        this.peekedEntryData = null

        return object : MutableMap.MutableEntry<K, V> {
            override fun setValue(newValue: V): V {
                val oldValue = peekedEntry.value
                peekedEntry.value = newValue
                return oldValue
            }

            override val key: K
                get() = peekedKey

            override val value: V
                get() = peekedEntry.value
        }
    }

    override fun hasNext(): Boolean {
        when (peekedEntryData) {
            null -> {
                this.peekedEntryData = nextReachable() ?: return false

                return true
            }

            else -> {
                // A repetitive call to `hasNext` is not a primary use case, but
                // it's easy to support
                return true
            }
        }
    }

    override fun remove() {
        throw UnsupportedOperationException("Removal during iteration is not supported")
    }

    /**
     * Finds the next reachable entry in the weak map.
     *
     * @return the next reachable entry data (key + weak entry), or `null` if there
     * are no more reachable entries.
     */
    private tailrec fun nextReachable(): Pair<K, PlatformWeakMap.WeakEntry<K, V>>? {
        val nextEntry = entryIterator.nextOrNull() ?: return null

        return when (val reachableNextKey = nextEntry.key.deref()) {
            null -> {
                // The entry is not reachable anymore, let's purge it
                // We don't even try to remove it from the weak entry map, as
                // it's not even possible (the key is already collected)
                entryIterator.remove()

                // Let's pretend the entry was never there and move on
                nextReachable()
            }

            // The entry is reachable, let's expose it
            else -> Pair(reachableNextKey, nextEntry)
        }
    }
}
