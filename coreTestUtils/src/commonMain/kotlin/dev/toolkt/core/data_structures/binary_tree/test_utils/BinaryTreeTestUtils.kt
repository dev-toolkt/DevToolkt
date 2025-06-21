package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getChild
import dev.toolkt.core.data_structures.binary_tree.getSibling
import dev.toolkt.core.data_structures.binary_tree.locateRelatively
import dev.toolkt.core.data_structures.binary_tree.traverse

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.traverseNaively(): Sequence<BinaryTree.NodeHandle<PayloadT, MetadataT>> =
    this.traverseNaivelyOrEmpty(
        subtreeRootHandle = root,
    )

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.traverseNaively(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): Sequence<BinaryTree.NodeHandle<PayloadT, MetadataT>> {
    val leftChild = getChild(
        nodeHandle = subtreeRootHandle,
        side = BinaryTree.Side.Left,
    )

    val rightChild = getChild(
        nodeHandle = subtreeRootHandle,
        side = BinaryTree.Side.Right,
    )

    return sequence {
        yieldAll(traverseNaivelyOrEmpty(subtreeRootHandle = leftChild))
        yield(subtreeRootHandle)
        yieldAll(traverseNaivelyOrEmpty(subtreeRootHandle = rightChild))
    }
}

private fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.traverseNaivelyOrEmpty(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>?,
): Sequence<BinaryTree.NodeHandle<PayloadT, MetadataT>> {
    if (subtreeRootHandle == null) return emptySequence()

    return this.traverseNaively(
        subtreeRootHandle = subtreeRootHandle,
    )
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getHandle(
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, ColorT> = traverse().single {
    getPayload(nodeHandle = it) == payload
}
