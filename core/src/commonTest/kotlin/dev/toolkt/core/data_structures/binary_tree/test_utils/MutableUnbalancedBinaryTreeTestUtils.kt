package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTree

fun <PayloadT: Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.putVerified(
    location: BinaryTree.Location<PayloadT, ColorT>,
    payload: PayloadT,
    color: ColorT,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val insertedNodeHandle = this.put(
        location = location,
        payload = payload,
        color = color,
    )

    verifyIntegrity(
        heightVerificator = null,
        colorVerificator = null,
    )

    return insertedNodeHandle
}

fun <PayloadT: Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.swapVerified(
    firstNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    secondNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
) {
    this.swap(
        firstNodeHandle = firstNodeHandle,
        secondNodeHandle = secondNodeHandle,
    )

    verifyIntegrity(
        heightVerificator = null,
        colorVerificator = null,
    )
}

fun <PayloadT: Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.cutOffVerified(
    leafHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.Location<PayloadT, ColorT> {
    val cutOffLeafLocation = this.cutOff(
        leafHandle = leafHandle,
    )

    verifyIntegrity(
        heightVerificator = null,
        colorVerificator = null,
    )

    return cutOffLeafLocation
}

fun <PayloadT: Comparable<PayloadT>, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.rotateVerified(
    pivotNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    direction: BinaryTree.RotationDirection,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val newSubtreeRootHandle = this.rotate(
        pivotNodeHandle = pivotNodeHandle,
        direction = direction,
    )

    verifyIntegrity(
        heightVerificator = null,
        colorVerificator = null,
    )

    return newSubtreeRootHandle
}
