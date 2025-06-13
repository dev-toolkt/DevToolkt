package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree

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

fun <PayloadT> RedBlackTree<PayloadT>.insertVerified(
    location: BinaryTree.Location<PayloadT, RedBlackTree.Color>,
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color> = insertVerified(
    location = location,
    payload = payload,
    colorVerificator = RedBlackColorVerificator,
)

fun <PayloadT> RedBlackTree<PayloadT>.removeVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackTree.Color>,
) {
    removeVerified(
        nodeHandle = nodeHandle,
        colorVerificator = RedBlackColorVerificator,
    )
}
