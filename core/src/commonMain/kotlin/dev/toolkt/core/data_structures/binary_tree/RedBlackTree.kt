package dev.toolkt.core.data_structures.binary_tree

class RedBlackTree<PayloadT> : AbstractBalancedBinaryTree<PayloadT, RedBlackTree.Color>() {
    enum class Color {
        Red, Black,
    }

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
