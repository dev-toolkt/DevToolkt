package dev.toolkt.core.data_structures.binary_tree

data class DumpedNode<PayloadT>(
    val payload: PayloadT,
    val leftChild: DumpedNode<PayloadT>? = null,
    val rightChild: DumpedNode<PayloadT>? = null,
)
