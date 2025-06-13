package dev.toolkt.core.data_structures.binary_tree

interface MutableUnbalancedBinaryTree<PayloadT, ColorT> : BinaryTree<PayloadT, ColorT> {
    companion object {
        fun <PayloadT, ColorT> create(): MutableUnbalancedBinaryTree<PayloadT, ColorT> {
            TODO()
        }
    }

    /**
     * Put a new node with the given [payload] and [color] at the given free
     * [location].
     *
     * @return A handle to the put node
     * @throws IllegalArgumentException if the location is taken
     */
    fun put(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
        color: ColorT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>

    /**
     * Remove the leaf corresponding to the given [leafHandle] from the tree.
     *
     * @throws IllegalArgumentException if the node is not really a leaf
     */
    fun cutOff(
        leafHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.Location<PayloadT, ColorT>

    /**
     * Elevate the node corresponding to the given [nodeHandle] (replace its
     * parent with this node). Requires that this node is not the root and has
     * no sibling. May result in the tree re-balancing.
     *
     * Remove a single-child node corresponding to the given [nodeHandle] from
     * the tree by replacing it with its child.
     *
     * @throws IllegalArgumentException if the node is a root or has a sibling
     */
    fun collapse(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )

    /**
     * Swap two given nodes. Doesn't affect the colors, meaning that the first
     * node will have the second node's color after the swap and the second
     * node will have the first node's color.
     */
    fun swap(
        firstNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        secondNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )

    /**
     * Rotate the subtree starting at node corresponding to [pivotNodeHandle] in
     * the given direction.
     *
     * @return A handle to the new root of the subtree after the rotation
     * @throws IllegalStateException if the pivot has no child on the respective side
     */
    fun rotate(
        pivotNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        direction: BinaryTree.RotationDirection,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>
}
