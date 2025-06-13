package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRightChild

data class NodeData<PayloadT, ColorT>(
    val payload: PayloadT,
    val color: ColorT,
    val leftChild: NodeData<PayloadT, ColorT>? = null,
    val rightChild: NodeData<PayloadT, ColorT>? = null,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.dump(): NodeData<PayloadT, ColorT>? = root?.let { dump(it) }

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.dump(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): NodeData<PayloadT, ColorT> {
    val payload = getPayload(nodeHandle = nodeHandle)
    val color = getColor(nodeHandle = nodeHandle)
    val leftChild = getLeftChild(nodeHandle = nodeHandle)
    val rightChild = getRightChild(nodeHandle = nodeHandle)

    return NodeData(
        payload = payload,
        color = color,
        leftChild = leftChild?.let { dump(nodeHandle = it) },
        rightChild = rightChild?.let { dump(nodeHandle = it) },
    )
}
