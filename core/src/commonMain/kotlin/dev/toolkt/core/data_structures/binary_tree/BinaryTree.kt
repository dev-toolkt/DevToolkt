package dev.toolkt.core.data_structures.binary_tree

interface BinaryTree<out PayloadT, out ColorT> {
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

    /**
     * A stable handle to the node inside the tree. Invalidates once the node is
     * removed through this handle. If two handles correspond to the same node,
     * they compare equal.
     */
    interface NodeHandle<out PayloadT, out MetadataT> {
        val isValid: Boolean
    }

    sealed interface Location<out PayloadT, out MetadataT> {
        val parentHandle: NodeHandle<PayloadT, MetadataT>?
    }

    /**
     * Location of the root node
     */
    data object RootLocation : Location<Nothing, Nothing> {
        override val parentHandle: NodeHandle<Nothing, Nothing>? = null
    }

    /**
     * Location relative to the parent node
     */
    data class RelativeLocation<out PayloadT, out MetadataT>(
        /**
         * The handle to the parent node
         */
        override val parentHandle: NodeHandle<PayloadT, MetadataT>,
        /**
         * The side of the parent node where the child is located
         */
        val side: Side,
    ) : Location<PayloadT, MetadataT> {
        val siblingSide: Side
            get() = side.opposite

    }

    val root: NodeHandle<PayloadT, ColorT>?

    val size: Int

    fun resolve(
        location: Location<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): NodeHandle<PayloadT, ColorT>?

    /**
     * Get the payload of the node associated with the given [nodeHandle].
     */
    fun getPayload(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): PayloadT

    fun getSubtreeSize(
        subtreeRootHandle: BinaryTree.NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): Int

    fun getColor(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): ColorT

    /**
     * Get the handle to the in-order neighbour from the given [side] of the node associated with the given [nodeHandle]
     */
    fun getInOrderNeighbour(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
        side: Side,
    ): NodeHandle<PayloadT, ColorT>?

    /**
     * Get the handle to the parent of the node associated with the given [nodeHandle].
     */
    fun getParent(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): NodeHandle<PayloadT, ColorT>?
}

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.isEmpty(): Boolean = size == 0

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.traverse(): Sequence<BinaryTree.NodeHandle<PayloadT, MetadataT>> =
    this.traverseOrEmpty(
        subtreeRootHandle = root,
    )

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.traverse(
    rootHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): Sequence<BinaryTree.NodeHandle<PayloadT, MetadataT>> {
    val leftChild = getChild(
        nodeHandle = rootHandle,
        side = BinaryTree.Side.Left,
    )

    val rightChild = getChild(
        nodeHandle = rootHandle,
        side = BinaryTree.Side.Right,
    )

    return sequence {
        yieldAll(traverseOrEmpty(subtreeRootHandle = leftChild))
        yield(rootHandle)
        yieldAll(traverseOrEmpty(subtreeRootHandle = rightChild))
    }
}

private fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.traverseOrEmpty(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>?,
): Sequence<BinaryTree.NodeHandle<PayloadT, MetadataT>> {
    if (subtreeRootHandle == null) return emptySequence()

    return this.traverse(
        rootHandle = subtreeRootHandle,
    )
}

/**
 * Get the handle to the child on the given [side] of the node associated
 * with the given [nodeHandle].
 */
fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
    side: BinaryTree.Side,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? = resolve(
    location = nodeHandle.getChildLocation(side = side),
)

/**
 * Get children of this node, starting from the given [side]. The first
 * child will be the "closer" child, the second one will be the "distant"
 * child.
 */
fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getChildren(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
    side: BinaryTree.Side,
): Pair<
        BinaryTree.NodeHandle<PayloadT, MetadataT>?,
        BinaryTree.NodeHandle<PayloadT, MetadataT>?,
        > {
    val closerChild = getChild(
        nodeHandle = nodeHandle,
        side = side,
    )

    val distantChild = getChild(
        nodeHandle = nodeHandle,
        side = side.opposite,
    )

    return Pair(closerChild, distantChild)
}

/**
 * Get a sibling of a node occupying the given [location] in the tree.
 */
fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getSibling(
    location: BinaryTree.RelativeLocation<PayloadT, MetadataT>,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? = getChild(
    nodeHandle = location.parentHandle,
    side = location.siblingSide,
)

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getLeftChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? = getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Left,
)

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getRightChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? = getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Right,
)

/**
 * Get a relative location of the child on the given [side] of the node
 */
fun <PayloadT, MetadataT> BinaryTree.NodeHandle<PayloadT, MetadataT>.getChildLocation(
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT, MetadataT> = BinaryTree.RelativeLocation(
    parentHandle = this,
    side = side,
)

fun <PayloadT, MetadataT> BinaryTree.NodeHandle<PayloadT, MetadataT>.getLeftChildLocation(): BinaryTree.RelativeLocation<PayloadT, MetadataT> =
    getChildLocation(
        side = BinaryTree.Side.Left,
    )

fun <PayloadT, MetadataT> BinaryTree.NodeHandle<PayloadT, MetadataT>.getRightChildLocation(): BinaryTree.RelativeLocation<PayloadT, MetadataT> =
    getChildLocation(
        side = BinaryTree.Side.Right,
    )

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getChildSide(
    parentHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
    childHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): BinaryTree.Side {
    val leftChildHandle = getChild(
        nodeHandle = parentHandle,
        side = BinaryTree.Side.Left,
    )

    val rightChildHandle = getChild(
        nodeHandle = parentHandle,
        side = BinaryTree.Side.Right,
    )

    return when (childHandle) {
        leftChildHandle -> BinaryTree.Side.Left

        rightChildHandle -> BinaryTree.Side.Right

        else -> {
            throw IllegalArgumentException("The given node is not a child of the given parent")
        }
    }
}

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.locate(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): BinaryTree.Location<PayloadT, MetadataT> = locateRelatively(
    nodeHandle = nodeHandle,
) ?: BinaryTree.RootLocation

/**
 * @return A relative location of the node associated with [nodeHandle] in the tree,
 * or null if the node is the root of the tree (i.e. has no parent).
 */
fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.locateRelatively(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): BinaryTree.RelativeLocation<PayloadT, MetadataT>? {
    val parentHandle = getParent(
        nodeHandle = nodeHandle,
    ) ?: return null

    val side = getChildSide(
        parentHandle = parentHandle,
        childHandle = nodeHandle,
    )

    return BinaryTree.RelativeLocation(
        parentHandle = parentHandle,
        side = side,
    )
}

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getSideMostFreeLocation(
    side: BinaryTree.Side,
): BinaryTree.Location<PayloadT, MetadataT> {
    val root = this.root ?: return BinaryTree.RootLocation

    return getSideMostFreeLocation(
        nodeHandle = root,
        side = side,
    )
}

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getSideMostFreeLocation(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT, MetadataT> {
    val sideChildHandle = getChild(
        nodeHandle = nodeHandle,
        side = side,
    ) ?: return BinaryTree.RelativeLocation(
        parentHandle = nodeHandle,
        side = side,
    )

    return getSideMostFreeLocation(
        nodeHandle = sideChildHandle,
        side = side,
    )
}
fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getInOrderPredecessor(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? = getInOrderNeighbour(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Left,
)

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getInOrderSuccessor(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? = getInOrderNeighbour(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Right,
)
