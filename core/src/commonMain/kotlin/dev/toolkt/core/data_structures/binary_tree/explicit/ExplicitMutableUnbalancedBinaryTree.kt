package dev.toolkt.core.data_structures.binary_tree.explicit

interface ExplicitMutableUnbalancedBinaryTree<PayloadT> : ExplicitMutableBinaryTree<PayloadT> {
    sealed interface RemovalStrategy<PayloadT> {
        data class Collapse<PayloadT>(
            val collapsedNode: ExplicitMutableBinaryTree.MutableNode<PayloadT>
        ) : RemovalStrategy<PayloadT>

        data class RemoveLeaf<PayloadT>(
            val removedLeafLink: ExplicitMutableBinaryTree.MutableLink<PayloadT>
        ) : RemovalStrategy<PayloadT>
    }

    /**
     * Rotate the subtree starting at [pivot]. Does not affect the tree's order.
     */
    fun rotate(
        pivot: ExplicitMutableBinaryTree.MutableNode<PayloadT>,
        direction: ExplicitBinaryTree.RotationDirection,
    ): ExplicitMutableBinaryTree.MutableProperNode<PayloadT>

    override fun collapse(node: ExplicitMutableBinaryTree.MutableNode<PayloadT>) {
        collapseDirectly(node = node)
    }

    /**
     * Remove a single-child [node] from the tree by replacing it with its child
     * without otherwise affecting the tree's structure.
     *
     * @throws IllegalStateException if this node has zero or two children
     */
    fun collapseDirectly(
        node: ExplicitMutableBinaryTree.MutableNode<PayloadT>,
    )

    /**
     * Remove a leaf from the tree without otherwise affecting the tree's structure.
     *
     * @throws IllegalStateException if [leaf] is not actually a leaf
     */
    fun removeLeafDirectly(
        leaf: ExplicitMutableBinaryTree.MutableNode<PayloadT>,
    ): ExplicitMutableBinaryTree.MutableLink<PayloadT>

    override fun remove(
        node: ExplicitMutableBinaryTree.MutableProperNode<PayloadT>,
    ) {
        removeDirectly(node = node)
    }
}

/**
 * Removes a node from the tree without otherwise affecting the tree's order.
 *
 * If this node has two children, this operation will start with swapping this
 * node with its in-order successor. Then it will either collapse or remove
 * [node], as after the swap it's guaranteed to have one child or be a leaf.
 *
 * @return The chosen removal strategy
 */
fun <PayloadT> ExplicitMutableUnbalancedBinaryTree<PayloadT>.removeDirectly(
    node: ExplicitMutableBinaryTree.MutableProperNode<PayloadT>,
): ExplicitMutableUnbalancedBinaryTree.RemovalStrategy<PayloadT> = when {
    node.leftChild != null && node.rightChild != null -> {
        val successor: ExplicitMutableBinaryTree.MutableProperNode<PayloadT> = TODO()

        removeLeafOrCollapse(
            node = successor,
        )
    }

    else -> removeLeafOrCollapse(
        node = node,
    )
}

/**
 * A helper function for removing a node with less than two children.
 */
private fun <PayloadT> ExplicitMutableUnbalancedBinaryTree<PayloadT>.removeLeafOrCollapse(
    node: ExplicitMutableBinaryTree.MutableProperNode<PayloadT>,
): ExplicitMutableUnbalancedBinaryTree.RemovalStrategy<PayloadT> = when (val singleChild = node.leftChild ?: node.rightChild) {
    null -> {
        val removedLeafLink = removeLeafDirectly(leaf = node)

        ExplicitMutableUnbalancedBinaryTree.RemovalStrategy.RemoveLeaf(
            removedLeafLink = removedLeafLink,
        )
    }

    else -> {
        collapseDirectly(node = singleChild)

        ExplicitMutableUnbalancedBinaryTree.RemovalStrategy.Collapse(
            collapsedNode = singleChild,
        )
    }
}
