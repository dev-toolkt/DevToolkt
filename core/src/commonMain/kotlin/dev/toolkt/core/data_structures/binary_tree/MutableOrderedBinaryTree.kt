package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.explicit.ExplicitBinaryTree
import dev.toolkt.core.data_structures.binary_tree.explicit.ExplicitMutableBinaryTree

interface MutableOrderedBinaryTree<DataT> : OrderedBinaryTree<DataT> {
    interface MutableNode<DataT> : OrderedBinaryTree.Node<DataT>

    interface BalancingStrategy<DataT, MetadataT> {
        val defaultMetadata: MetadataT

        fun rebalanceAfterLeafInsertion(
            insertedNode: ExplicitMutableBinaryTree.MutableNode<DataT>,
        )

        fun rebalanceAfterElevation(
            elevatedNode: ExplicitMutableBinaryTree.MutableNode<DataT>,
        )

        fun rebalanceAfterLeafRemoval(
            removalLink: ExplicitMutableBinaryTree.MutableLink<DataT>,
        )
    }

    override fun select(
        index: Int,
    ): MutableNode<DataT>?

    fun insertExtremal(
        side: ExplicitBinaryTree.Side,
        value: DataT,
    ): MutableNode<DataT>

    fun insertGuided(
        comparator: Comparator<DataT>,
        value: DataT,
    ): MutableNode<DataT>

    fun insertAdjacent(
        side: ExplicitBinaryTree.Side,
        value: DataT,
    ): MutableNode<DataT>

    fun remove(
        node: MutableNode<DataT>,
    )
}
