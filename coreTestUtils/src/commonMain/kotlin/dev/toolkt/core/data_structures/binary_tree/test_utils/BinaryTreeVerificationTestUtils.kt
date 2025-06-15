package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRightChild

data class IntegrityVerificationResult(
    val computedSubtreeSize: Int,
    val computedSubtreeHeight: Int,
)

interface HeightVerificator {
    fun verifySubtreeHeight(
        leftSubtreeHeight: Int,
        rightSubtreeHeight: Int,
    )
}

interface ColorVerificator<ColorT> {
    fun verifyColor(
        parentColor: ColorT,
        nodeColor: ColorT,
    )
}

fun <PayloadT : Comparable<PayloadT>, ColorT> BinaryTree<PayloadT, ColorT>.verifyIntegrity(
    heightVerificator: HeightVerificator?,
    colorVerificator: ColorVerificator<ColorT>?,
) {
    val rootHandle = this.root ?: return

    verifySubtreeIntegrity(
        nodeHandle = rootHandle,
        expectedParentHandle = null,
        heightVerificator = heightVerificator,
        colorVerificator = colorVerificator,
    )
}

fun <PayloadT : Comparable<PayloadT>, ColorT> BinaryTree<PayloadT, ColorT>.verifySubtreeIntegrity(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    expectedParentHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?,
    heightVerificator: HeightVerificator?,
    colorVerificator: ColorVerificator<ColorT>?,
): IntegrityVerificationResult {
    val actualParentHandle = getParent(nodeHandle = nodeHandle)

    if (actualParentHandle != expectedParentHandle) {
        throw AssertionError("Inconsistent parent")
    }

    val payload = getPayload(nodeHandle = nodeHandle)

    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)
    val rightChildHandle = getRightChild(nodeHandle = nodeHandle)

    val leftSubtreeVerificationResult = leftChildHandle?.let {
        val leftPayload = getPayload(nodeHandle = it)

        if (leftPayload >= payload) {
            throw AssertionError("Left child payload $leftPayload is not less than parent payload $payload")
        }

        verifySubtreeIntegrity(
            nodeHandle = it,
            expectedParentHandle = nodeHandle,
            heightVerificator = heightVerificator,
            colorVerificator = colorVerificator,
        )
    }

    val rightSubtreeVerificationResult = rightChildHandle?.let {
        val rightPayload = getPayload(nodeHandle = it)

        if (rightPayload <= payload) {
            throw AssertionError("Right child payload $rightPayload is not greater than parent payload $payload")
        }

        verifySubtreeIntegrity(
            nodeHandle = it,
            expectedParentHandle = nodeHandle,
            heightVerificator = heightVerificator,
            colorVerificator = colorVerificator,
        )
    }

    val computedLeftSubtreeSize = leftSubtreeVerificationResult?.computedSubtreeSize ?: 0
    val computedRightSubtreeSize = rightSubtreeVerificationResult?.computedSubtreeSize ?: 0
    val computedTotalSubtreeSize = computedLeftSubtreeSize + computedRightSubtreeSize + 1

    val cachedSubtreeSize = getSubtreeSize(subtreeRootHandle = nodeHandle)

    if (cachedSubtreeSize != computedTotalSubtreeSize) {
        throw AssertionError("Inconsistent subtree size, computed: $computedTotalSubtreeSize, cached: $cachedSubtreeSize")
    }

    val computedLeftSubtreeHeight = leftSubtreeVerificationResult?.computedSubtreeHeight ?: 0
    val computedRightSubtreeHeight = rightSubtreeVerificationResult?.computedSubtreeHeight ?: 0

    heightVerificator?.verifySubtreeHeight(
        leftSubtreeHeight = computedLeftSubtreeHeight,
        rightSubtreeHeight = computedRightSubtreeHeight,
    )

    val computedMaxSubtreeHeight = maxOf(computedLeftSubtreeHeight, computedRightSubtreeHeight) + 1

    if (actualParentHandle != null) {
        val parentColor = getColor(actualParentHandle)
        val nodeColor = getColor(nodeHandle = nodeHandle)

        colorVerificator?.verifyColor(
            parentColor = parentColor,
            nodeColor = nodeColor,
        )
    }

    return IntegrityVerificationResult(
        computedSubtreeSize = computedTotalSubtreeSize,
        computedSubtreeHeight = computedMaxSubtreeHeight,
    )
}
