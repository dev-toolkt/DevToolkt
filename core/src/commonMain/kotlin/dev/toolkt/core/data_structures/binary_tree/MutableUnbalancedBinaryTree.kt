package dev.toolkt.core.data_structures.binary_tree

interface MutableUnbalancedBinaryTree<PayloadT, ColorT> : BinaryTree<PayloadT, ColorT> {
    companion object {
        fun <PayloadT, ColorT> create(): MutableUnbalancedBinaryTree<PayloadT, ColorT> = MutableUnbalancedBinaryTreeImpl()
    }

    /**
     * Set the color of the node corresponding to the given [nodeHandle] to [newColor]
     */
    fun setColor(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        newColor: ColorT,
    )

    /**
     * Attach a new leaf with the given [payload] and [color] at the given free
     * [location].
     *
     * @return A handle to the attached leaf
     * @throws IllegalArgumentException if the location is taken
     */
    fun attach(
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
     * Remove a single-child node corresponding to the given [nodeHandle] from
     * the tree by replacing it with its child.
     *
     * @throws IllegalArgumentException if the node is a leaf or has two children
     * @return A handle to the elevated child of the removed node
     */
    fun collapse(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>

    /**
     * Swap two node corresponding to the [firstNodeHandle] with the node
     * corresponding to the [secondNodeHandle].
     *
     * Doesn't affect the colors, meaning that the first node will have the second
     * node's original color after the swap and the second node will have the first
     * node's original color.
     *
     * Handles aren't affected, meaning that the first node will still be reachable
     * by the first handle and the second node will still be reachable by the second handle.
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

internal fun <PayloadT, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.paint(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    newColor: ColorT,
) {
    val color = getColor(nodeHandle = nodeHandle)

    if (color == newColor) {
        throw IllegalStateException("The node is already painted $newColor")
    }

    setColor(
        nodeHandle = nodeHandle,
        newColor = newColor,
    )
}
