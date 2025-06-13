package dev.toolkt.core.data_structures.binary_tree

data class IntegrityVerificationResult(
    val computedSubtreeSize: Int,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifyIntegrity() {
    val rootHandle = this.root ?: return

    verifySubtreeIntegrity(
        nodeHandle = rootHandle,
        expectedParentHandle = null,
    )
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifySubtreeIntegrity(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    expectedParentHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?,
): IntegrityVerificationResult {
    val actualParentHandle = getParent(nodeHandle = nodeHandle)

    if (actualParentHandle != expectedParentHandle) {
        throw AssertionError("Inconsistent parent")
    }

    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)

    val computedLeftSubtreeSize = leftChildHandle?.let {
        verifySubtreeIntegrity(
            nodeHandle = it,
            expectedParentHandle = nodeHandle,
        ).computedSubtreeSize
    } ?: 0

    val computedRightSubtreeSize = getRightChild(nodeHandle = nodeHandle)?.let {
        verifySubtreeIntegrity(
            nodeHandle = it,
            expectedParentHandle = nodeHandle,
        ).computedSubtreeSize
    } ?: 0

    val cachedSubtreeSize = getSubtreeSize(subtreeRootHandle = nodeHandle)

    val computedTotalSubtreeSize = computedLeftSubtreeSize + 1 + computedRightSubtreeSize

    if (cachedSubtreeSize != computedTotalSubtreeSize) {
        throw AssertionError("Inconsistent cached subtree size, true: $computedTotalSubtreeSize, cached: $cachedSubtreeSize")
    }

    return IntegrityVerificationResult(
        computedSubtreeSize = computedTotalSubtreeSize,
    )
}
