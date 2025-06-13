package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRightChild

data class DumpedNode<PayloadT, ColorT>(
    val payload: PayloadT,
    val color: ColorT,
    val leftChild: DumpedNode<PayloadT, ColorT>? = null,
    val rightChild: DumpedNode<PayloadT, ColorT>? = null,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.dump(): DumpedNode<PayloadT, ColorT>? = root?.let { dump(it) }

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.dump(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): DumpedNode<PayloadT, ColorT> {
    val payload = getPayload(nodeHandle = nodeHandle)
    val color = getColor(nodeHandle = nodeHandle)
    val leftChild = getLeftChild(nodeHandle = nodeHandle)
    val rightChild = getRightChild(nodeHandle = nodeHandle)

    return DumpedNode(
        payload = payload,
        color = color,
        leftChild = leftChild?.let { dump(nodeHandle = it) },
        rightChild = rightChild?.let { dump(nodeHandle = it) },
    )
}
