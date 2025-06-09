package dev.toolkt.core.data_structures.binary_tree

interface BinaryTreeOrderStatistic<DataT> {
    fun select(
        index: Int,
    ): BinaryTree.NodeHandle<DataT>?

    fun getRank(
        nodeHandle: BinaryTree.NodeHandle<DataT>,
    ): Int
}

fun <DataT> BinaryTreeOrderStatistic<*>.casting(): BinaryTreeOrderStatistic<DataT> =
    object : BinaryTreeOrderStatistic<DataT> {
        override fun select(
            index: Int,
        ): BinaryTree.NodeHandle<DataT>? = this@casting.select(
            index = index,
        )?.cast()

        override fun getRank(
            nodeHandle: BinaryTree.NodeHandle<DataT>,
        ): Int = this@casting.getRank(
            nodeHandle = nodeHandle.cast(),
        )
    }
