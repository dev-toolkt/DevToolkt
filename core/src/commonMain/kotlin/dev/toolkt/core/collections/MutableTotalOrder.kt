package dev.toolkt.core.collections

import dev.toolkt.core.order.OrderRelation

class MutableTotalOrder<E> {
    interface Handle<E>

    companion object;

    fun get(
        handle: Handle<E>,
    ): E {
        TODO()
    }

    fun set(
        handle: Handle<E>,
        element: E,
    ) {
        TODO()
    }

    fun select(
        index: Int,
    ): Handle<E> {
        TODO()
    }

    fun rank(
        handle: Handle<E>,
    ): Int {
        TODO()
    }

    fun addRelative(
        handle: Handle<E>,
        relation: OrderRelation.Inequal,
        element: E,
    ): Handle<E> {
        TODO()
    }

    fun addExtremal(
        relation: OrderRelation.Inequal,
        element: E,
    ): Handle<E> {
        TODO()
    }

    fun remove(
        handle: Handle<E>,
    ) {
        TODO()
    }

    fun traverse(): Sequence<Handle<E>> {
        TODO()
    }
}
