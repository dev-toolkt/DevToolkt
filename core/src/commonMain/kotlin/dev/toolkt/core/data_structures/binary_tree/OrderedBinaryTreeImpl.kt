package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.explicit.ExplicitBinaryTree
import dev.toolkt.core.data_structures.binary_tree.explicit.ExplicitMutableBinaryTree
import kotlin.jvm.JvmInline

class OrderedBinaryTreeImpl<DataT>(
    private val rawTree: ExplicitMutableBinaryTree<DataT>,
) : MutableOrderedBinaryTree<DataT> {
    @JvmInline
    value class NodeImpl<DataT>(
        val rawNode: ExplicitMutableBinaryTree.MutableNode<DataT>,
    ) : MutableOrderedBinaryTree.MutableNode<DataT>

    override fun select(
        index: Int,
    ): MutableOrderedBinaryTree.MutableNode<DataT>? {
        TODO("Not yet implemented")
    }

    override fun insertExtremal(
        side: ExplicitBinaryTree.Side,
        value: DataT
    ): MutableOrderedBinaryTree.MutableNode<DataT> {
        TODO("Not yet implemented")
    }

    override fun insertGuided(
        comparator: Comparator<DataT>,
        value: DataT
    ): MutableOrderedBinaryTree.MutableNode<DataT> {
        TODO("Not yet implemented")
    }

    override fun insertAdjacent(
        side: ExplicitBinaryTree.Side,
        value: DataT
    ): MutableOrderedBinaryTree.MutableNode<DataT> {
        TODO("Not yet implemented")
    }

    override fun remove(node: MutableOrderedBinaryTree.MutableNode<DataT>) {
        TODO("Not yet implemented")
    }

    override fun find(
        comparator: Comparator<DataT>,
    ): DataT? {
        TODO("Not yet implemented")
    }

    override fun getRank(
        node: OrderedBinaryTree.Node<DataT>,
    ) {
        TODO("Not yet implemented")
    }

    override fun traverse(): Sequence<DataT> {
        TODO("Not yet implemented")
    }
}
