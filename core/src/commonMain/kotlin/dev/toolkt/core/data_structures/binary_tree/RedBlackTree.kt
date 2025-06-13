package dev.toolkt.core.data_structures.binary_tree

/**
 * @constructor The constructor that accepts an existing mutable [internalTree]
 * is a low-level functionality. The ownership of that tree passes to this object.
 * The given tree is assumed to initially be a valid red-black tree.
 */
class RedBlackTree<PayloadT>(
    internalTree: MutableUnbalancedBinaryTree<PayloadT, Color> = MutableUnbalancedBinaryTree.create(),
) : AbstractBalancedBinaryTree<PayloadT, RedBlackTree.Color>(
    internalTree = internalTree,
) {
    enum class Color {
        Red, Black,
    }

    companion object;


    override fun rebalanceAfterPut(
        putNodeHandle: BinaryTree.NodeHandle<PayloadT, Color>,
    ) {
        TODO("Not yet implemented")
    }

    override fun rebalanceAfterCutOff(
        cutOffLeafLocation: BinaryTree.Location<PayloadT, Color>,
    ) {
        TODO("Not yet implemented")
    }

    override fun rebalanceAfterCollapse(
        elevatedNodeHandle: BinaryTree.NodeHandle<PayloadT, Color>,
    ) {
        TODO("Not yet implemented")
    }
}
