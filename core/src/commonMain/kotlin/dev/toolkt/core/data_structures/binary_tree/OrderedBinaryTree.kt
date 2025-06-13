package dev.toolkt.core.data_structures.binary_tree

interface OrderedBinaryTree<out DataT> {
    interface Node<out DataT>

    fun find(
        comparator: Comparator<@UnsafeVariance DataT>,
    ): DataT?

    fun select(
        index: Int,
    ): Node<DataT>?

    fun getRank(
        node: Node<@UnsafeVariance DataT>,
    )

    fun traverse(): Sequence<DataT>
}
