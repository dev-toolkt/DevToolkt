package dev.toolkt.core.data_structures.binary_tree

data class DumpedNode<PayloadT>(
    val payload: PayloadT,
    val leftChild: DumpedNode<PayloadT>? = null,
    val rightChild: DumpedNode<PayloadT>? = null,
)

fun <PayloadT> BinaryTree<PayloadT>.dump(): DumpedNode<PayloadT>? = root?.let { dump(it) }

fun <PayloadT> BinaryTree<PayloadT>.dump(
    nodeHandle: BinaryTree.NodeHandle<PayloadT>,
): DumpedNode<PayloadT> {
    val payload = getPayload(nodeHandle = nodeHandle)
    val leftChild = getLeftChild(nodeHandle = nodeHandle)
    val rightChild = getRightChild(nodeHandle = nodeHandle)

    return DumpedNode(
        payload = payload,
        leftChild = leftChild?.let { dump(nodeHandle = it) },
        rightChild = rightChild?.let { dump(nodeHandle = it) },
    )
}

fun <PayloadT> BasicBinaryTree<PayloadT>.insertVerified(
    location: BinaryTree.Location<PayloadT>,
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT> {
    val insertedNodeHandle = this.insert(
        location = location,
        payload = payload,
    )

    verifyIntegrity()

    return insertedNodeHandle
}

fun <PayloadT> BasicBinaryTree<PayloadT>.swapVerified(
    firstNodeHandle: BinaryTree.NodeHandle<PayloadT>,
    secondNodeHandle: BinaryTree.NodeHandle<PayloadT>,
) {
    this.swap(
        firstNodeHandle = firstNodeHandle,
        secondNodeHandle = secondNodeHandle,
    )

    verifyIntegrity()
}

fun <PayloadT> BasicBinaryTree<PayloadT>.removeLeafVerified(
    leafHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.Location<PayloadT> {
    val location = this.removeLeafLocated(
        leafHandle = leafHandle,
    )

    verifyIntegrity()

    return location
}
