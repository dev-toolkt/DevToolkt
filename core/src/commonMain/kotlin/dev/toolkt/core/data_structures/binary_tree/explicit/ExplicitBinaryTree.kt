package dev.toolkt.core.data_structures.binary_tree.explicit

/**
 * "Explicit" means that it exposes its nodes directly. This might not be the
 * best idea.
 */
interface ExplicitBinaryTree<out PayloadT> {
    /**
     * A side of the tree or the tree's node, either left or right.
     */
    sealed class Side {
        data object Left : Side() {
            override val opposite: Side = Right

            override val directionTo = RotationDirection.CounterClockwise
        }

        data object Right : Side() {
            override val opposite: Side = Left

            override val directionTo = RotationDirection.Clockwise
        }

        /**
         * The opposite side to this side.
         */
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

    /**
     * A direction of rotation of a subtree around a node.
     */
    sealed class RotationDirection {
        data object Clockwise : RotationDirection() {
            override val opposite = CounterClockwise

            override val startSide = Side.Left
        }

        data object CounterClockwise : RotationDirection() {
            override val opposite = Clockwise

            override val startSide = Side.Right
        }

        abstract val opposite: RotationDirection

        abstract val startSide: Side

        val endSide: Side
            get() = startSide.opposite
    }

    sealed interface Node<out PayloadT> {
        fun buildLink(
            child: ProperNode<@UnsafeVariance PayloadT>,
        ): Link<PayloadT>
    }

    interface ProperNode<out PayloadT> : Node<PayloadT> {
        val parent: Node<PayloadT>

        val payload: PayloadT

        val leftChild: ProperNode<PayloadT>?

        val rightChild: ProperNode<PayloadT>?

        val subtreeSize: Int
    }

    interface OriginNode<out PayloadT> : Node<PayloadT> {
        val root: ProperNode<PayloadT>?
    }

    /**
     * A link between a parent node and the location of its child.
     */
    sealed interface Link<out PayloadT> {
        val parent: Node<PayloadT>
    }

    /**
     * A link between the origin node and the root node
     */
    sealed class RootLink<PayloadT>() : Link<PayloadT> {
        override val parent: OriginNode<PayloadT> = origin

        abstract val origin: OriginNode<PayloadT>
    }

    /**
     * A link between a proper node and a child on one of its sides
     */
    sealed class ProperLink<out PayloadT>() : Link<PayloadT> {
        abstract override val parent: ProperNode<PayloadT>

        abstract val side: Side

        val siblingSide: Side
            get() = side.opposite
    }

    val origin: OriginNode<PayloadT>
}

internal val <PayloadT> ExplicitBinaryTree.ProperNode<PayloadT>?.subtreeSizeOrZero: Int
    get() = this?.subtreeSize ?: 0

val <PayloadT> ExplicitBinaryTree.ProperNode<PayloadT>.parentLink: ExplicitBinaryTree.Link<PayloadT>
    get() = this.parent.buildLink(child = this)

fun <PayloadT> ExplicitBinaryTree.ProperNode<PayloadT>.getChild(
    side: ExplicitBinaryTree.Side,
): ExplicitBinaryTree.ProperNode<PayloadT>? = when (side) {
    ExplicitBinaryTree.Side.Left -> leftChild
    ExplicitBinaryTree.Side.Right -> rightChild
}

fun <PayloadT> ExplicitBinaryTree.ProperNode<PayloadT>.getChildSide(
    child: ExplicitBinaryTree.ProperNode<PayloadT>,
): ExplicitBinaryTree.Side = when {
    child === leftChild -> ExplicitBinaryTree.Side.Left
    child === rightChild -> ExplicitBinaryTree.Side.Right
    else -> throw IllegalArgumentException("The given node is not a child of this node")
}
