package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRightChild
import dev.toolkt.core.pairs.sorted

private data class IntegrityVerificationResult(
    val computedSubtreeSize: Int,
)

private data class BalanceVerificationResult(
    val computedSubtreeHeight: Int,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifyIntegrity() {
    val rootHandle = this.root ?: return

    verifySubtreeIntegrity(
        nodeHandle = rootHandle,
        expectedParentHandle = null,
    )
}

fun <PayloadT : Comparable<PayloadT>, ColorT> BinaryTree<PayloadT, ColorT>.verifyBalance() {
    val rootHandle = this.root ?: return

    verifySubtreeBalance(
        nodeHandle = rootHandle,
    )
}

private fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifySubtreeIntegrity(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    expectedParentHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?,
): IntegrityVerificationResult {
    val actualParentHandle = getParent(nodeHandle = nodeHandle)

    if (actualParentHandle != expectedParentHandle) {
        throw AssertionError("Inconsistent parent")
    }

    val payload = getPayload(nodeHandle = nodeHandle)

    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)
    val rightChildHandle = getRightChild(nodeHandle = nodeHandle)

    val leftSubtreeVerificationResult = leftChildHandle?.let {
        verifySubtreeIntegrity(
            nodeHandle = it,
            expectedParentHandle = nodeHandle,
        )
    }

    val rightSubtreeVerificationResult = rightChildHandle?.let {
        verifySubtreeIntegrity(
            nodeHandle = it,
            expectedParentHandle = nodeHandle,
        )
    }

    val computedLeftSubtreeSize = leftSubtreeVerificationResult?.computedSubtreeSize ?: 0
    val computedRightSubtreeSize = rightSubtreeVerificationResult?.computedSubtreeSize ?: 0
    val computedTotalSubtreeSize = computedLeftSubtreeSize + computedRightSubtreeSize + 1

    val cachedSubtreeSize = getSubtreeSize(subtreeRootHandle = nodeHandle)

    if (cachedSubtreeSize != computedTotalSubtreeSize) {
        throw AssertionError("Inconsistent subtree size, computed: $computedTotalSubtreeSize, cached: $cachedSubtreeSize")
    }

    return IntegrityVerificationResult(
        computedSubtreeSize = computedTotalSubtreeSize,
    )
}

private fun <PayloadT : Comparable<PayloadT>, ColorT> BinaryTree<PayloadT, ColorT>.verifySubtreeBalance(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BalanceVerificationResult {
    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)
    val rightChildHandle = getRightChild(nodeHandle = nodeHandle)

    val leftSubtreeVerificationResult = leftChildHandle?.let {
        verifySubtreeBalance(
            nodeHandle = it,
        )
    }

    val rightSubtreeVerificationResult = rightChildHandle?.let {
        verifySubtreeBalance(
            nodeHandle = it,
        )
    }

    val (minPathLength, maxPathLength) = Pair(
        leftSubtreeVerificationResult?.computedSubtreeHeight ?: 1,
        rightSubtreeVerificationResult?.computedSubtreeHeight ?: 1,
    ).sorted()

    if (maxPathLength > 2 * minPathLength) {
        throw AssertionError("Unbalanced subtree, min subtree height: $minPathLength, max subtree height: $maxPathLength")
    }

    return BalanceVerificationResult(
        computedSubtreeHeight = maxPathLength,
    )
}
