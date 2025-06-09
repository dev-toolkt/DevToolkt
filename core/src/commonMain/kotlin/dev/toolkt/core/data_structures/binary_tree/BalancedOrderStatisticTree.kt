package dev.toolkt.core.data_structures.binary_tree

class BalancedOrderStatisticTree<DataT> internal constructor(
    orderStatistic: BinaryTreeOrderStatistic<DataT>,
    mutableTree: MutableBinaryTree<DataT>,
) : OrderStatisticTree<DataT>, BinaryTreeOrderStatistic<DataT> by orderStatistic,
    MutableBinaryTree<DataT> by mutableTree {
    companion object {
        fun <DataT> create(): BalancedOrderStatisticTree<DataT> {
            val orderStatisticTree =
                MutableOrderStatisticTree<EnhancedBinaryTree.EnhancedPayload<DataT, BalancedRedBlackTree.Color>>(
                    orderedTree = BasicBinaryTree()
                )

            val redBlackTree = BalancedRedBlackTree(
                subjectTree = orderStatisticTree,
            )

            return BalancedOrderStatisticTree(
                orderStatistic = orderStatisticTree.casting(),
                mutableTree = redBlackTree,
            )
        }
    }
}
