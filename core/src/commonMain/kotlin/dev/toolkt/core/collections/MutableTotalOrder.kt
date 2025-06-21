package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.data_structures.binary_tree.getRank
import dev.toolkt.core.data_structures.binary_tree.getSideMostFreeLocation
import dev.toolkt.core.data_structures.binary_tree.insertRelative
import dev.toolkt.core.data_structures.binary_tree.select
import dev.toolkt.core.data_structures.binary_tree.traverse
import dev.toolkt.core.order.OrderRelation
import kotlin.jvm.JvmInline

class MutableTotalOrder<E> {
    private val tree = RedBlackTree<E>()

    @JvmInline
    value class Handle<E> internal constructor(
        internal val nodeHandle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
    )

    companion object;

    fun get(
        handle: Handle<E>,
    ): E {
        val nodeHandle = handle.unpack()
        return tree.getPayload(nodeHandle = nodeHandle)
    }

    fun set(
        handle: Handle<E>,
        element: E,
    ) {
        val nodeHandle = handle.unpack()

        tree.setPayload(
            nodeHandle = nodeHandle,
            payload = element,
        )
    }

    fun get(
        index: Int,
    ): Handle<E>? {
        val nodeHandle = tree.select(index)
        return nodeHandle?.pack()
    }

    fun indexOf(
        handle: Handle<E>,
    ): Int {
        val nodeHandle = handle.unpack()
        return tree.getRank(nodeHandle = nodeHandle)
    }

    fun addRelative(
        handle: Handle<E>,
        relation: OrderRelation.Inequal,
        element: E,
    ): Handle<E> {
        val nodeHandle = handle.unpack()

        val insertedNodeHandle = tree.insertRelative(
            nodeHandle = nodeHandle,
            side = relation.side,
            payload = element,
        )

        return insertedNodeHandle.pack()
    }

    fun addExtremal(
        relation: OrderRelation.Inequal,
        element: E,
    ): Handle<E> {
        val insertedNodeHandle = tree.insert(
            location = tree.getSideMostFreeLocation(
                side = relation.side,
            ),
            payload = element,
        )

        return insertedNodeHandle.pack()
    }

    fun remove(
        handle: Handle<E>,
    ) {
        val nodeHandle = handle.unpack()

        tree.remove(
            nodeHandle = nodeHandle,
        )
    }

    fun traverse(): Sequence<Handle<E>> = tree.traverse().map { it.pack() }
}

private val OrderRelation.Inequal.side: BinaryTree.Side
    get() = when (this) {
        OrderRelation.Greater -> BinaryTree.Side.Right
        OrderRelation.Smaller -> BinaryTree.Side.Left
    }

private fun <E> MutableTotalOrder.Handle<E>.unpack(): BinaryTree.NodeHandle<E, RedBlackTree.Color> = this.nodeHandle

private fun <E> BinaryTree.NodeHandle<E, RedBlackTree.Color>.pack(): MutableTotalOrder.Handle<E> =
    MutableTotalOrder.Handle(
        nodeHandle = this,
    )
