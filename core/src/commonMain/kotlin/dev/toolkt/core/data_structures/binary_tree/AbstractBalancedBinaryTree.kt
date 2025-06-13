package dev.toolkt.core.data_structures.binary_tree

abstract class AbstractBalancedBinaryTree<PayloadT, ColorT> private constructor(
    protected val unbalancedTree: MutableUnbalancedBinaryTree<PayloadT, ColorT>,
) : MutableBalancedBinaryTree<PayloadT, ColorT>, BinaryTree<PayloadT, ColorT> by unbalancedTree {
    constructor() : this(
        unbalancedTree = MutableUnbalancedBinaryTree.create(),
    )

    override fun insert(
        location: BinaryTree.Location<PayloadT, ColorT>, payload: PayloadT
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        TODO("Not yet implemented")
    }

    override fun remove(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ) {
        TODO("Not yet implemented")
    }

    abstract fun rebalanceAfterPut(
        putNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )

    abstract fun rebalanceAfterCutOff(
        cutOffLeafLocation: BinaryTree.Location<PayloadT, ColorT>,
    )

    abstract fun rebalanceAfterCollapse(
        elevatedNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )
}
