package dev.toolkt.core.data_structures.red_black_tree_og

import dev.toolkt.core.data_structures.red_black_tree_og.RedBlackTreeOgImpl.Color
import dev.toolkt.core.data_structures.red_black_tree_og.RedBlackTreeOgImpl.ProperNode
import dev.toolkt.core.data_structures.red_black_tree_og.RedBlackTreeOgImpl.RotationDirection
import kotlin.jvm.JvmInline

/**
 * Red–black tree is a self-balancing binary search tree data structure noted
 * for fast storage and retrieval of ordered information.
 */
@JvmInline
value class RedBlackTreeOg<T> private constructor(
    private val origin: RedBlackTreeOgImpl.OriginNode<T>,
) {
    sealed class Side {
        data object Left : Side() {
            override val opposite: Side = Right

            override val directionTo = RotationDirection.CounterClockwise
        }

        data object Right : Side() {
            override val opposite: Side = Left

            override val directionTo = RotationDirection.Clockwise
        }

        internal abstract val opposite: Side

        /**
         * The rotation direction that makes the parent take the position of
         * the child on this side
         */
        internal abstract val directionTo: RotationDirection

        /**
         * The rotation direction that makes the child on this side take the
         * position of its parent
         */
        internal val directionFrom: RotationDirection
            get() = directionTo.opposite
    }

    data class DumpedNode<T>(
        val leftNode: DumpedNode<T>?,
        val value: T,
        val rightNode: DumpedNode<T>?,
    )

    /**
     * A stable handle to the node inside the tree. Invalidates once the node is
     * removed through via this handle.
     */
    class Handle<T> internal constructor(
        private val node: ProperNode<T>,
    ) {
        private var isValid = true

        internal val validNode: ProperNode<T>
            get() {
                require(isValid) {
                    "The handle is invalid, the node has been removed from the tree"
                }

                val validNode = node.asValid ?: throw IllegalStateException("The associated node is no loner valid")

                return validNode
            }

        internal fun invalidate() {
            isValid = false
        }
    }

    // This is a harmless micro-optimization (one less object allocation)
    constructor() : this(origin = RedBlackTreeOgImpl.OriginNode())

    /**
     * Insert a new minimal/maximal value to the tree.
     *
     * @return A handle to the new inserted node
     */
    fun insertExtremal(
        /**
         * The value to insert.
         */
        value: T,
        /**
         * A side of the tree the new value should be inserted. If it's left,
         * [value] will be the new minimal value, if it's right, it will be the
         * new maximal value.
         */
        side: Side,
    ): Handle<T> {
        val newNode = createNode(
            value = value,
        )

        origin.addChildDescending(
            child = newNode,
            side = side,
        )

        newNode.fixupInsertion()

        return Handle(node = newNode)
    }

    /**
     * Insert a new strictly smaller/greater value relative to the node
     * associated with the given [handle].
     *
     * @return A handle to the new inserted node
     */
    fun insertAdjacent(
        /**
         * A handle to the node that will be used as a reference for the new
         * value insertion.
         */
        handle: Handle<T>,
        /**
         * The value to insert.
         */
        value: T,
        /**
         * A side of the reference node the new value should be inserted. If it's
         * left, [value] will be strictly smaller than the reference node's value.
         * If it's right, [value] will be strictly greater than the reference
         * node's value.
         */
        side: Side,
    ): Handle<T> {
        val referenceNode = handle.validNode

        val newNode = createNode(
            value = value,
        )

        referenceNode.addChildAdjacent(
            child = newNode,
            side = side,
        )

        newNode.fixupInsertion()

        return Handle(node = newNode)
    }

    /**
     * Remove the node associated with the given [handle]. The handle is not
     * valid after this operation.
     */
    fun remove(
        handle: Handle<T>,
    ) {
        val node = handle.validNode

        handle.invalidate()

        when {
            node.childCount == 2 -> {
                removeNodeWithTwoChildren(node)
            }

            else -> {
                removeNodeWithAtMostOneChild(node)
            }
        }
    }

    val inOrderTraversal: Sequence<T>
        get() = origin.inOrderTraversal

    fun verifyIntegrity() {
        try {
            origin.verifyIntegrity()
        } catch (e: AssertionError) {
            println(dump())
            throw e
        }
    }

    fun dumpNode(): DumpedNode<T>? = origin.dumpNode()

    fun dump(): String = origin.dump()

    private fun createNode(
        value: T,
    ): ProperNode<T> = ProperNode(
        // Temporarily set the parent to origin, it will be overridden during
        // the actual insertion
        currentParent = origin,
        currentColor = Color.Red,
        currentValue = value,
    )

    private fun removeNodeWithTwoChildren(
        node: ProperNode<T>,
    ) {
        val successor =
            node.getInOrderSuccessor() ?: throw AssertionError("A node with two children must have a successor")

        node.stealValue(successor)

        // The in-order successor can have at most one child, by definition
        removeNodeWithAtMostOneChild(successor)
    }

    private fun removeNodeWithAtMostOneChild(
        node: ProperNode<T>,
    ) {
        when (node.childCount) {
            1 -> removeNodeWithOneChild(node)
            0 -> removeLeaf(node)
            else -> throw IllegalArgumentException("The node must have at most one child, but has ${node.childCount} children")
        }
    }

    private fun removeNodeWithOneChild(
        node: ProperNode<T>,
    ) {
        val singleChild = node.singleChildOrNull
            ?: throw IllegalArgumentException("The node must have exactly one child, but has ${node.childCount} children")

        node.parentLink.replaceChild(
            newChild = singleChild,
        )

        node.invalidate()

        singleChild.paintBlack()
    }

    private fun removeLeaf(
        leaf: ProperNode<T>,
    ) {
        require(leaf.childCount == 0) {
            "The node must be a leaf, but has ${leaf.childCount} children"
        }

        val leafColor = leaf.effectiveColor

        val newNullNode = leaf.parentLink.clearChild()

        leaf.invalidate()

        if (leafColor == Color.Black) {
            newNullNode.fixupRemoval()
        }
    }
}
