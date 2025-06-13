package dev.toolkt.core.data_structures.binary_tree

fun <PayloadT, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.putVerified(
    location: BinaryTree.Location<PayloadT, ColorT>,
    payload: PayloadT,
    color: ColorT,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val insertedNodeHandle = this.put(
        location = location,
        payload = payload,
        color = color,
    )

    verifyIntegrity()

    return insertedNodeHandle
}

fun <PayloadT, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.swapVerified(
    firstNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    secondNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
) {
    this.swap(
        firstNodeHandle = firstNodeHandle,
        secondNodeHandle = secondNodeHandle,
    )

    verifyIntegrity()
}

fun <PayloadT, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.cutOffVerified(
    leafHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.Location<PayloadT, ColorT> {
    val cutOffLeafLocation = this.cutOff(
        leafHandle = leafHandle,
    )

    verifyIntegrity()

    return cutOffLeafLocation
}

fun <PayloadT, ColorT> MutableUnbalancedBinaryTree<PayloadT, ColorT>.rotateVerified(
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
