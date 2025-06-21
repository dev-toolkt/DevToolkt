package dev.toolkt.core.data_structures.binary_tree

interface MutableBalancedBinaryTree<PayloadT, ColorT> : BinaryTree<PayloadT, ColorT> {
    /**
     * Set the payload of the node corresponding to the given [nodeHandle].
     */
    fun setPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        payload: PayloadT,
    )

    /**
     * Insert a new node with the given [payload] at the given free [location].
     *
     * May result in the tree re-balancing.
     *
     * @return A handle to the new inserted node
     * @throws IllegalArgumentException if the location is taken
     */
    fun insert(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>

    /**
     * Removes a node corresponding to the given [nodeHandle] from the tree
     * without otherwise affecting the tree's order.
     *
     * May result in the tree re-balancing.
     *
     * @throws IllegalArgumentException if the node is not a leaf
     */
    fun remove(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )
}

fun <PayloadT, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.insertRelative(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val location = getNextInOrderFreeLocation(
        nodeHandle = nodeHandle,
        side = side,
    )

    return insert(
        location = location,
        payload = payload,
    )
}
