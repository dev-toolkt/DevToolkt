package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRightChild
import dev.toolkt.core.range.split
import kotlin.random.Random

private data class ColorVerificationResult(
    val blackHeight: Int,
)

fun <PayloadT : Comparable<PayloadT>> RedBlackTree<PayloadT>.insertVerified(
    location: BinaryTree.Location<PayloadT, RedBlackTree.Color>,
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color> {
    val insertedNodeHandle = insert(
        location = location,
        payload = payload,
    )

    verify()

    return insertedNodeHandle
}

fun <PayloadT : Comparable<PayloadT>> RedBlackTree<PayloadT>.removeVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>,
) {
    remove(
        nodeHandle = nodeHandle,
    )

    verify()
}

fun <PayloadT : Comparable<PayloadT>> RedBlackTree.Companion.loadVerified(
    rootData: NodeData<PayloadT, RedBlackTree.Color>
): RedBlackTree<PayloadT> {
    val internalTree = MutableUnbalancedBinaryTree.load(
        rootData = rootData,
    )

    internalTree.verify()

    return RedBlackTree(
        internalTree = internalTree,
    )
}

fun <PayloadT : Comparable<PayloadT>> BinaryTree<PayloadT, RedBlackTree.Color>.verify() {
    verifyIntegrity()
    verifyBalance()
    verifyColor()
}

private fun <PayloadT : Comparable<PayloadT>> BinaryTree<PayloadT, RedBlackTree.Color>.verifyColor() {
    val rootHandle = this.root ?: return

    verifySubtreeColor(parentColor = null, rootHandle)
}

private fun <PayloadT : Comparable<PayloadT>> BinaryTree<PayloadT, RedBlackTree.Color>.verifySubtreeColor(
    parentColor: RedBlackTree.Color?,
    nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>,
): ColorVerificationResult {
    val nodeColor = getColor(nodeHandle = nodeHandle)

    if (parentColor == RedBlackTree.Color.Red && nodeColor == RedBlackTree.Color.Red) {
        throw AssertionError("Red node cannot have a red parent")
    }

    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)
    val rightChildHandle = getRightChild(nodeHandle = nodeHandle)


    val leftSubtreeVerificationResult = leftChildHandle?.let {
        verifySubtreeColor(
            parentColor = nodeColor,
            nodeHandle = it,
        )
    }

    val rightSubtreeVerificationResult = rightChildHandle?.let {
        verifySubtreeColor(
            parentColor = nodeColor,
            nodeHandle = it,
        )
    }

    val leftSubtreeBlackHeight = leftSubtreeVerificationResult?.blackHeight ?: 1
    val rightSubtreeBlackHeight = rightSubtreeVerificationResult?.blackHeight ?: 1

    val ownBlackHeight = when (nodeColor) {
        RedBlackTree.Color.Red -> 0
        RedBlackTree.Color.Black -> 1
    }

    if (leftSubtreeBlackHeight != rightSubtreeBlackHeight) {
        throw AssertionError("Left and right subtrees must have the same black height, but left: $leftSubtreeBlackHeight, right: $rightSubtreeBlackHeight")
    } else {
        return ColorVerificationResult(
            blackHeight = leftSubtreeBlackHeight + ownBlackHeight,
        )
    }
}

fun RedBlackTree.Companion.buildBalance(
    requiredBlackDepth: Int,
    payloadRange: ClosedFloatingPointRange<Double>,
): NodeData<Double, RedBlackTree.Color>? = buildBalance(
    random = Random(seed = 0), // Pass an explicit seed to make things deterministic
    requiredBlackDepth = requiredBlackDepth,
    payloadRange = payloadRange,
    parentColor = RedBlackTree.Color.Red, // Assume a red parent to avoid red violation
)

private fun RedBlackTree.Companion.buildBalance(
    random: Random,
    requiredBlackDepth: Int,
    payloadRange: ClosedFloatingPointRange<Double>,
    parentColor: RedBlackTree.Color = RedBlackTree.Color.Red,
): NodeData<Double, RedBlackTree.Color>? {
    require(requiredBlackDepth >= 1)

    if (requiredBlackDepth == 1) {
        return null
    }

    val (leftPayloadRange, rightPayloadRange) = payloadRange.split()

    val color = when (parentColor) {
        RedBlackTree.Color.Red -> RedBlackTree.Color.Black

        else -> {
            val x = random.nextDouble()

            when {
                x < 0.4 -> RedBlackTree.Color.Red
                else -> RedBlackTree.Color.Black
            }
        }
    }

    val newRequiredBlackDepth = when (color) {
        RedBlackTree.Color.Black -> requiredBlackDepth - 1
        else -> requiredBlackDepth
    }

    return NodeData(
        payload = rightPayloadRange.start,
        color = color,
        leftChild = buildBalance(
            random = random,
            requiredBlackDepth = newRequiredBlackDepth,
            payloadRange = leftPayloadRange,
            parentColor = color,
        ),
        rightChild = buildBalance(
            random = random,
            requiredBlackDepth = newRequiredBlackDepth,
            payloadRange = rightPayloadRange,
            parentColor = color,
        ),
    )
}
