package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTree

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.attachVerified(
    location: BinaryTree.Location<PayloadT, ColorT>,
    payload: PayloadT,
    color: ColorT,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val insertedNodeHandle = this.attach(
        location = location,
        payload = payload,
        color = color,
    )

    verifyIntegrity()

    return insertedNodeHandle
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.swapVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
) {
    this.swap(
        nodeHandle = nodeHandle,
        side = side,
    )

    verifyIntegrity()
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.cutOffVerified(
    leafHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.Location<PayloadT, ColorT> {
    val cutOffLeafLocation = this.cutOff(
        leafHandle = leafHandle,
    )

    verifyIntegrity()

    return cutOffLeafLocation
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.rotateVerified(
    pivotNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    direction: BinaryTree.RotationDirection,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val newSubtreeRootHandle = this.rotate(
        pivotNodeHandle = pivotNodeHandle,
        direction = direction,
    )

    verifyIntegrity()

    return newSubtreeRootHandle
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.collapseVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val elevatedChildHandle = this.collapse(
        nodeHandle = nodeHandle,
    )

    verifyIntegrity()

    return elevatedChildHandle
}
