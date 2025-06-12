package dev.toolkt.core.data_structures.binary_tree

interface BinaryTree<out PayloadT> {
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
     * removed through this handle.
     */
    interface NodeHandle<out PayloadT>

    sealed interface Location<out PayloadT> {
        val parentHandle: NodeHandle<PayloadT>?
    }

    /**
     * Location of the root node
     */
    data object RootLocation : Location<Nothing> {
        override val parentHandle: NodeHandle<Nothing>? = null
    }

    /**
     * Location relative to the parent node
     */
    data class RelativeLocation<out PayloadT>(
        /**
         * The handle to the parent node
         */
        override val parentHandle: NodeHandle<PayloadT>,
        /**
         * The side of the parent node where the child is located
         */
        val side: Side,
    ) : Location<PayloadT> {
        val siblingSide: Side
            get() = side.opposite

        fun getSibling(
            tree: BinaryTree<@UnsafeVariance PayloadT>,
        ): NodeHandle<PayloadT>? = tree.getChild(
            nodeHandle = parentHandle,
            side = siblingSide,
        )
    }

    val root: NodeHandle<PayloadT>?

    fun resolve(
        location: Location<@UnsafeVariance PayloadT>,
    ): NodeHandle<PayloadT>?

    /**
     * Get the payload of the node associated with the given [nodeHandle].
     */
    fun getPayload(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT>,
    ): PayloadT

    /**
     * Get the handle to the parent of the node associated with the given [nodeHandle].
     */
    fun getParent(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT>,
    ): NodeHandle<PayloadT>?
}

/**
 * This is a very unsafe case, which works in practice only when used with care
 * be the binary trees wrapping other binary trees.
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <PayloadT> BinaryTree.NodeHandle<*>.cast(): BinaryTree.NodeHandle<PayloadT> {
    @Suppress("UNCHECKED_CAST") return this as BinaryTree.NodeHandle<PayloadT>
}

/**
 * This is a very unsafe case, which works in practice only when used with care
 * be the binary trees wrapping other binary trees.
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <PayloadT> BinaryTree.Location<*>.cast(): BinaryTree.Location<PayloadT> {
    @Suppress("UNCHECKED_CAST") return this as BinaryTree.Location<PayloadT>
}

fun <PayloadT> BinaryTree<PayloadT>.traverse(): Sequence<PayloadT> = traverseOrEmpty(
    rootHandle = root,
)

fun <PayloadT> BinaryTree<PayloadT>.traverse(
    rootHandle: BinaryTree.NodeHandle<PayloadT>,
): Sequence<PayloadT> {
    val leftChild = getChild(
        nodeHandle = rootHandle,
        side = BinaryTree.Side.Left,
    )

    val payload = getPayload(
        nodeHandle = rootHandle,
    )

    val rightChild = getChild(
        nodeHandle = rootHandle,
        side = BinaryTree.Side.Right,
    )

    return sequence {
        yieldAll(traverseOrEmpty(rootHandle = leftChild))
        yield(payload)
        yieldAll(traverseOrEmpty(rootHandle = rightChild))
    }
}

private fun <PayloadT> BinaryTree<PayloadT>.traverseOrEmpty(
    rootHandle: BinaryTree.NodeHandle<PayloadT>?,
): Sequence<PayloadT> {
    if (rootHandle == null) return emptySequence()

    return this.traverse(
        rootHandle = rootHandle,
    )
}

/**
 * Get the handle to the child on the given [side] of the node associated
 * with the given [nodeHandle].
 */
fun <PayloadT> BinaryTree<PayloadT>.getChild(
    nodeHandle: BinaryTree.NodeHandle<@UnsafeVariance PayloadT>,
    side: BinaryTree.Side,
): BinaryTree.NodeHandle<PayloadT>? = resolve(
    location = nodeHandle.getChildLocation(side = side),
)

fun <PayloadT> BinaryTree<PayloadT>.getLeftChild(
    nodeHandle: BinaryTree.NodeHandle<@UnsafeVariance PayloadT>,
): BinaryTree.NodeHandle<PayloadT>? = getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Left,
)

fun <PayloadT> BinaryTree<PayloadT>.getRightChild(
    nodeHandle: BinaryTree.NodeHandle<@UnsafeVariance PayloadT>,
): BinaryTree.NodeHandle<PayloadT>? = getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Right,
)

fun <PayloadT> BinaryTree<PayloadT>.getSubtreeSize(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT>,
): Int {
    val leftChild = getLeftChild(nodeHandle = subtreeRootHandle)
    val rightChild = getRightChild(nodeHandle = subtreeRootHandle)

    val leftSize = getSubtreeSizeOrZero(subtreeRootHandle = leftChild)
    val rightSize = getSubtreeSizeOrZero(subtreeRootHandle = rightChild)

    return leftSize + 1 + rightSize
}

fun <PayloadT> BinaryTree<PayloadT>.getSubtreeSizeOrZero(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT>?,
): Int {
    if (subtreeRootHandle == null) return 0
    return getSubtreeSize(subtreeRootHandle = subtreeRootHandle)
}


/**
 * Get a relative location of the child on the given [side] of the node
 */
fun <PayloadT> BinaryTree.NodeHandle<PayloadT>.getChildLocation(
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT> = BinaryTree.RelativeLocation(
    parentHandle = this,
    side = side,
)

fun <PayloadT> BinaryTree.NodeHandle<PayloadT>.getLeftChildLocation(): BinaryTree.RelativeLocation<PayloadT> =
    getChildLocation(
        side = BinaryTree.Side.Left,
    )

fun <PayloadT> BinaryTree.NodeHandle<PayloadT>.getRightChildLocation(): BinaryTree.RelativeLocation<PayloadT> =
    getChildLocation(
        side = BinaryTree.Side.Right,
    )

fun <PayloadT> BinaryTree<PayloadT>.getChildSide(
    parentHandle: BinaryTree.NodeHandle<PayloadT>,
    childHandle: BinaryTree.NodeHandle<PayloadT>,
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

fun <PayloadT> BinaryTree<PayloadT>.locate(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.Location<PayloadT> = locateRelatively(
    nodeHandle = nodeHandle,
) ?: BinaryTree.RootLocation

/**
 * @return A relative location of the node associated with [nodeHandle] in the tree,
 * or null if the node is the root of the tree (i.e. has no parent).
 */
fun <PayloadT> BinaryTree<PayloadT>.locateRelatively(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.RelativeLocation<PayloadT>? {
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

fun <PayloadT, TransformedPayloadT> BinaryTree.Location<PayloadT>.map(
    transform: (BinaryTree.NodeHandle<PayloadT>) -> BinaryTree.NodeHandle<TransformedPayloadT>,
): BinaryTree.Location<TransformedPayloadT> = when (this) {
    is BinaryTree.RelativeLocation<PayloadT> -> this.map(transform)
    BinaryTree.RootLocation -> BinaryTree.RootLocation
}

fun <PayloadT, TransformedPayloadT> BinaryTree.RelativeLocation<PayloadT>.map(
    transform: (BinaryTree.NodeHandle<PayloadT>) -> BinaryTree.NodeHandle<TransformedPayloadT>,
): BinaryTree.RelativeLocation<TransformedPayloadT> = BinaryTree.RelativeLocation(
    parentHandle = transform(parentHandle),
    side = side,
)

interface Guide<in PayloadT> {
    interface Instruction

    data class TurnInstruction(
        /**
         * The side of the tree to turn to
         */
        val side: BinaryTree.Side,
    ) : Instruction

    /**
     * An instruction to stop
     */
    data object StopInstruction : Instruction

    fun instruct(
        nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    ): Instruction
}

/**
 * @return the set of the given node and its proper ancestors
 */
fun <PayloadT> BinaryTree<PayloadT>.getQuasiAncestors(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): Sequence<BinaryTree.NodeHandle<PayloadT>> = generateSequence(
    seed = nodeHandle,
) { currentNodeHandle ->
    getParent(nodeHandle = currentNodeHandle)
}

fun <PayloadT> BinaryTree<PayloadT>.getAncestors(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): Sequence<BinaryTree.NodeHandle<PayloadT>> {
    val parent = this.getParent(nodeHandle = nodeHandle) ?: return emptySequence()

    return generateSequence(
        seed = parent,
    ) { currentNodeHandle ->
        getParent(nodeHandle = currentNodeHandle)
    }
}

fun <PayloadT> BinaryTree<PayloadT>.getChildCount(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): Int {
    val leftChild = getChild(
        nodeHandle = nodeHandle,
        side = BinaryTree.Side.Left,
    )

    val rightChild = getChild(
        nodeHandle = nodeHandle,
        side = BinaryTree.Side.Right,
    )

    val l = if (leftChild != null) 1 else 0
    val r = if (rightChild != null) 1 else 0
    return l + r
}

fun <PayloadT> BinaryTree<PayloadT>.getSingleChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.NodeHandle<PayloadT>? = getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Left,
) ?: getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Right,
)

fun <PayloadT> BinaryTree<PayloadT>.getSideMostFreeLocation(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT> {
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

/**
 * Get the in-order neighbour (predecessor / successor) of the node associated with
 * [nodeHandle] on the specified [side].
 *
 * @return A handle to the in-order neighbour node, or null if the given node has
 * no child on the specified [side]. This neighbour node will have no children
 * on the side opposite to [side].
 */
fun <PayloadT> BinaryTree<PayloadT>.getInOrderNeighbour(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    side: BinaryTree.Side,
): BinaryTree.NodeHandle<PayloadT>? = getNextDeeperInOrderLocation(
    nodeHandle, side,
)?.parentHandle

fun <PayloadT> BinaryTree<PayloadT>.getNextInOrderLocation(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT> = getNextDeeperInOrderLocation(
    nodeHandle = nodeHandle,
    side = side,
) ?: BinaryTree.RelativeLocation(
    parentHandle = nodeHandle,
    side = side,
)

fun <PayloadT> BinaryTree<PayloadT>.getNextDeeperInOrderLocation(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT>? {
    val sideChildHandle = getChild(
        nodeHandle = nodeHandle,
        side = side,
    ) ?: return null

    return getSideMostFreeLocation(
        nodeHandle = sideChildHandle,
        side = side.opposite,
    )
}

fun <PayloadT> BinaryTree<PayloadT>.getInOrderPredecessor(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.NodeHandle<PayloadT>? = getInOrderNeighbour(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Left,
)

fun <PayloadT> BinaryTree<PayloadT>.getInOrderSuccessor(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.NodeHandle<PayloadT>? = getInOrderNeighbour(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Right,
)

fun <PayloadT> BinaryTree<PayloadT>.findLeafGuided(
    originNodeHandle: BinaryTree.NodeHandle<PayloadT>,
    guide: Guide<PayloadT>,
): BinaryTree.NodeHandle<PayloadT>? {
    TODO()
}
