package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree
import dev.toolkt.core.range.split
import kotlin.random.Random

data object RedBlackColorVerificator : ColorVerificator<RedBlackTree.Color> {
    override fun verifyColor(
        parentColor: RedBlackTree.Color,
        nodeColor: RedBlackTree.Color,
    ) {
        if (parentColor == RedBlackTree.Color.Red && nodeColor == RedBlackTree.Color.Red) {
            throw AssertionError("Red node cannot have red parent")
        }
    }
}

fun <PayloadT : Comparable<PayloadT>> RedBlackTree<PayloadT>.insertVerified(
    location: BinaryTree.Location<PayloadT, RedBlackTree.Color>,
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color> = insertVerified(
    location = location,
    payload = payload,
    colorVerificator = RedBlackColorVerificator,
)

fun <PayloadT : Comparable<PayloadT>> RedBlackTree<PayloadT>.removeVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>,
) {
    removeVerified(
        nodeHandle = nodeHandle,
        colorVerificator = RedBlackColorVerificator,
    )
}

fun <PayloadT : Comparable<PayloadT>> RedBlackTree.Companion.loadVerified(
    rootData: NodeData<PayloadT, RedBlackTree.Color>
): RedBlackTree<PayloadT> {
    val internalTree = MutableUnbalancedBinaryTree.load(
        rootData = rootData,
    )

    internalTree.verifyIntegrityRedBlack()

    return RedBlackTree(
        internalTree = internalTree,
    )
}

fun <PayloadT : Comparable<PayloadT>> BinaryTree<PayloadT, RedBlackTree.Color>.verifyIntegrityRedBlack(
) {
    this.verifyIntegrityBalanced(
        colorVerificator = RedBlackColorVerificator,
    )
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
    require(requiredBlackDepth >= 0)

    if (requiredBlackDepth == 0) {
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
        RedBlackTree.Color.Black -> requiredBlackDepth
        else -> requiredBlackDepth - 1
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
