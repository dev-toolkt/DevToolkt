package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.data_structures.binary_tree.find
import dev.toolkt.core.data_structures.binary_tree.getMinimalDescendant

class RedBlackTreeSet<E : Comparable<E>>() : AbstractMutableSet<E>() {
    class RedBlackTreeSetIterator<E : Comparable<E>>(
        private val tree: RedBlackTree<E>,
    ) : HandleIterator<E, BinaryTree.NodeHandle<E, RedBlackTree.Color>>(
        firstElementHandle = tree.getMinimalDescendant(),
    ) {
        override fun resolve(
            handle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
        ): E = tree.getPayload(nodeHandle = handle)

        override fun getNext(
            handle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
        ): BinaryTree.NodeHandle<E, RedBlackTree.Color>? = tree.getInOrderNeighbour(
            nodeHandle = handle,
            side = BinaryTree.Side.Right,
        )

        override fun remove(
            handle: BinaryTree.NodeHandle<E, RedBlackTree.Color>,
        ) {
            tree.remove(nodeHandle = handle)
        }
    }

    private val tree = RedBlackTree<E>()

    override val size: Int
        get() = tree.size

    override fun iterator(): MutableIterator<E> = RedBlackTreeSetIterator(
        tree = tree,
    )

    override fun add(element: E): Boolean {
        val location = tree.find(element)

        when {
            tree.resolve(location = location) == null -> {
                tree.insert(
                    location = location,
                    payload = element,
                )

                return true
            }

            else -> {
                return false
            }
        }
    }

    override fun contains(element: E): Boolean {
        val location = tree.find(element)
        return tree.resolve(location = location) != null
    }
}
