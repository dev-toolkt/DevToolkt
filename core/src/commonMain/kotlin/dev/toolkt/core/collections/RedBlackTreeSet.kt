package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.data_structures.binary_tree.findLocationComparable
import kotlin.jvm.JvmInline

@JvmInline
class RedBlackTreeSet<E : Comparable<E>>() : AbstractMutableSet<E>() {
    private val tree = RedBlackTree<E>()

    override val size: Int
        get() = tree.size

    override fun iterator(): MutableIterator<E> {
        TODO("Not yet implemented")
    }

    override fun add(element: E): Boolean {
        val location = tree.findLocationComparable()

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
        val location = tree.findLocationComparable()
        return tree.resolve(location = location) != null
    }
}
