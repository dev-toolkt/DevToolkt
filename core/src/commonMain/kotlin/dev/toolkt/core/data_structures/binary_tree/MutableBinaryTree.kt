package dev.toolkt.core.data_structures.binary_tree

interface MutableBinaryTree<PayloadT> : BinaryTree<PayloadT> {
    /**
     * Insert a new value at the given free [location]. May result in the tree
     * re-balancing.
     *
     * @return A handle to the new inserted node
     * @throws IllegalArgumentException if the location is taken
     */
    fun insert(
        location: BinaryTree.Location<PayloadT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT>

    /**
     * Remove the leaf corresponding to the given [leafHandle] from the tree.
     * May result in the tree re-balancing.
     *
     * @throws IllegalArgumentException if the node is not a leaf
     */
    fun removeLeaf(
        leafHandle: BinaryTree.NodeHandle<PayloadT>,
    )

    /**
     * Elevate the node corresponding to the given [nodeHandle] (replace its
     * parent with this node). Requires that this node is not the root and has
     * no sibling. May result in the tree re-balancing.
     *
     * @throws IllegalArgumentException if the node is a root or has a sibling
     */
    fun elevate(
        nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    )

    fun swap(
        firstNodeHandle: BinaryTree.NodeHandle<PayloadT>,
        secondNodeHandle: BinaryTree.NodeHandle<PayloadT>,
    )
}

/**
 * Insert a new minimal/maximal value to the tree.
 *
 * @return A handle to the new inserted node
 */
fun <PayloadT> MutableBinaryTree<PayloadT>.insertSideMost(
    /**
     * A side of the tree the new value should be inserted. If it's left,
     * [payload] will be the new minimal value, if it's right, it will be the
     * new maximal value.
     */
    side: BinaryTree.Side,
    /**
     * The value to insert.
     */
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT> {
    val rootHandle = this.root ?: run {
        TODO()
    }

    return insertSideMost(
        subtreeRootHandle = rootHandle,
        side = side,
        payload = payload,
    )
}

/**
 * Insert a new value at the given location (if it's free) or to the [side]-most
 * free location of the node on the given location.
 *
 * @return A handle to the new inserted node
 */
fun <PayloadT> MutableBinaryTree<PayloadT>.insertSideMost(
    /**
     * A handle to the root of the subtree where the new value should be
     * inserted.
     */
    location: BinaryTree.Location<PayloadT>,
    /**
     * A side of the tree the new value should be inserted. If it's left,
     * [payload] will be the new minimal value, if it's right, it will be the
     * new maximal value.
     */
    side: BinaryTree.Side,
    /**
     * The value to insert.
     */
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT> = when (val nodeHandle = resolve(location = location)) {
    null -> insert(
        location = location,
        payload = payload,
    )

    else -> {
        val freeLocation = getSideMostFreeLocation(
            nodeHandle = nodeHandle,
            side = side,
        )

        insert(
            location = freeLocation,
            payload = payload,
        )
    }
}

/**
 * Insert a new minimal/maximal value to the subtree.
 *
 * @return A handle to the new inserted node
 */
fun <PayloadT> MutableBinaryTree<PayloadT>.insertSideMost(
    /**
     * A handle to the root of the subtree where the new value should be
     * inserted.
     */
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT>,
    /**
     * A side of the tree the new value should be inserted. If it's left,
     * [payload] will be the new minimal value, if it's right, it will be the
     * new maximal value.
     */
    side: BinaryTree.Side,
    /**
     * The value to insert.
     */
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT> {
    val sideMostFreeLocation = getSideMostFreeLocation(
        nodeHandle = subtreeRootHandle,
        side = side,
    )

    return insert(
        location = sideMostFreeLocation,
        payload = payload,
    )
}

/**
 * Insert a new strictly smaller/greater value relative to the node
 * associated with the given [nodeHandle].
 *
 * @return A handle to the new inserted node
 */
fun <PayloadT> MutableBinaryTree<PayloadT>.insertAdjacent(
    /**
     * A handle to the node that will be used as a reference for the new
     * value insertion.
     */
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    /**
     * A side of the reference node the new value should be inserted. If it's
     * left, [payload] will be strictly smaller than the reference node's value.
     * If it's right, [payload] will be strictly greater than the reference
     * node's value.
     */
    side: BinaryTree.Side,
    /**
     * The value to insert.
     */
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT> {
    val nextInOrderLocation = getNextInOrderLocation(
        nodeHandle = nodeHandle,
        side = side,
    )

    return insert(
        location = nextInOrderLocation,
        payload = payload,
    )
}

fun <PayloadT> MutableBinaryTree<PayloadT>.collapse(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
) {
    val childCount = getChildCount(nodeHandle = nodeHandle)

    if (childCount <= 1) {
        removeNodeWithAtMostOneChild(nodeHandle = nodeHandle)
    }

    val successorHandle = getInOrderSuccessor(
        nodeHandle = nodeHandle,
    ) ?: throw AssertionError("A node with two children must have a successor")

    swap(
        nodeHandle,
        successorHandle,
    )

    removeNodeWithAtMostOneChild(
        nodeHandle = successorHandle,
    )
}

private fun <PayloadT> MutableBinaryTree<PayloadT>.removeNodeWithAtMostOneChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
) {
    val singleChildHandle = getSingleChild(
        nodeHandle = nodeHandle,
    )

    when (singleChildHandle) {
        null -> {
            removeLeaf(leafHandle = nodeHandle)
        }

        else -> {
            elevate(nodeHandle = singleChildHandle)
        }
    }
}
