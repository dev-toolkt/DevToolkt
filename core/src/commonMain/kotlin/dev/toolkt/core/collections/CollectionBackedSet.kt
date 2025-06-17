package dev.toolkt.core.collections

import kotlin.jvm.JvmInline

@JvmInline
value class CollectionBackedSet<E>(
    private val backingCollection: Collection<E>,
) : Set<E> {
    override val size: Int
        get() = backingCollection.size

    override fun isEmpty(): Boolean = backingCollection.isEmpty()

    override fun contains(element: E): Boolean = backingCollection.contains(element)

    override fun iterator(): Iterator<E> = backingCollection.iterator()

    override fun containsAll(elements: Collection<E>): Boolean = backingCollection.containsAll(elements)
}
