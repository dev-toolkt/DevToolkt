package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRightChild

sealed interface NodeMatcher<PayloadT, ColorT> {
    class Proper<PayloadT, ColorT>(
        val expectedHandle: BinaryTree.NodeHandle<PayloadT, ColorT>? = null,
        val parentMatcher: NodeMatcher<PayloadT, ColorT>? = null,
        val expectedPayload: PayloadT,
        val expectedColor: ColorT,
        val leftChildMatcher: NodeMatcher<PayloadT, ColorT>? = null,
        val rightChildMatcher: NodeMatcher<PayloadT, ColorT>? = null,
    ) : NodeMatcher<PayloadT, ColorT> {
        override fun assertMatches(
            tree: BinaryTree<PayloadT, ColorT>, nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?
        ) {
            if (nodeHandle == null) {
                throw AssertionError("Expected a proper node ($expectedPayload), but got a null node")
            }

            if (expectedHandle != null && nodeHandle != expectedHandle) {
                throw AssertionError("Unexpected node handle")
            }

            val parent = tree.getParent(nodeHandle = nodeHandle)

            parentMatcher?.assertMatches(
                tree = tree,
                nodeHandle = parent,
            )

            val payload = tree.getPayload(nodeHandle = nodeHandle)

            if (payload != expectedPayload) {
                throw AssertionError("Expected payload $expectedPayload, but got $payload")
            }

            val color = tree.getColor(nodeHandle = nodeHandle)

            if (color != expectedColor) {
                throw AssertionError("Expected color $expectedColor, but got $color")
            }

            val leftChildHandle = tree.getLeftChild(nodeHandle = nodeHandle)

            leftChildMatcher?.assertMatches(
                tree = tree,
                nodeHandle = leftChildHandle,
            )

            val rightChildHandle = tree.getRightChild(nodeHandle = nodeHandle)

            rightChildMatcher?.assertMatches(
                tree = tree,
                nodeHandle = rightChildHandle,
            )
        }
    }

    class Null<PayloadT, ColorT>() : NodeMatcher<PayloadT, ColorT> {
        override fun assertMatches(
            tree: BinaryTree<PayloadT, ColorT>, nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?
        ) {
            if (nodeHandle != null) {
                val payload = tree.getPayload(nodeHandle = nodeHandle)
                throw AssertionError("Expected a null node, but got a proper node: $payload")
            }
        }
    }

    fun assertMatches(
        tree: BinaryTree<PayloadT, ColorT>,
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?,
    )
}
