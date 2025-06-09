package dev.toolkt.core.data_structures.binary_tree

/**
 * In addition to introducing the rotation operation, trees implementing this
 * interface guarantee that the basic mutation operations (insertLeaf,
 * removeLeaf) will not update the tree in any other way.
 */
interface UnbalancedBinaryTree<PayloadT> : MutableBinaryTree<PayloadT> {
    interface Prototype {
        fun <PayloadT> create(): UnbalancedBinaryTree<PayloadT>
    }

    fun rotate(
        pivotNodeHandle: BinaryTree.NodeHandle<PayloadT>,
        direction: BinaryTree.RotationDirection,
    ): BinaryTree.NodeHandle<PayloadT>
}

fun <PayloadT> UnbalancedBinaryTree<PayloadT>.removeLeafLocated(
    leafHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.Location<PayloadT> {
    val leafLocation = locate(nodeHandle = leafHandle)

    removeLeaf(leafHandle = leafHandle)

    return leafLocation
}
