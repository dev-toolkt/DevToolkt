package dev.toolkt.core.data_structures.binary_tree

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.select(
    index: Int,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? {
    val rootHandle = this.root ?: return null

    return this.select(
        nodeHandle = rootHandle,
        index = index,
    )
}

private tailrec fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.select(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
    index: Int,
): BinaryTree.NodeHandle<PayloadT, MetadataT>? {
    val downRank = getDownRank(nodeHandle = nodeHandle)

    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)
    val rightChildHandle = getRightChild(nodeHandle = nodeHandle)

    return when {
        index == downRank -> nodeHandle

        index < downRank -> when (leftChildHandle) {
            null -> null

            else -> select(
                nodeHandle = leftChildHandle,
                index = index,
            )
        }

        else -> when (rightChildHandle) {
            null -> null

            else -> select(
                nodeHandle = rightChildHandle,
                index = index - downRank - 1,
            )
        }
    }
}

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getRank(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): Int {
    val downRank = getDownRank(
        nodeHandle = nodeHandle,
    )

    val upRank = getUpRank(
        nodeHandle = nodeHandle,
    )

    return downRank + upRank
}

/**
 * Get the rank of the node corresponding to the given [nodeHandle] in its supertree (the whole tree minus the node's
 * descendants)
 */
private tailrec fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getUpRank(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): Int {
    val relativeLocation = locateRelatively(
        nodeHandle = nodeHandle,
    ) ?: return 0

    val parentHandle = relativeLocation.parentHandle
    val side = relativeLocation.side

    return when (side) {
        BinaryTree.Side.Left -> getUpRank(nodeHandle = parentHandle)
        BinaryTree.Side.Right -> getRank(nodeHandle = parentHandle) + 1
    }
}

/**
 * Get the rank of the node corresponding to the given [nodeHandle] in its own subtree
 */
private fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.getDownRank(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, MetadataT>,
): Int {
    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)

    return leftChildHandle?.let {
        getSubtreeSize(subtreeRootHandle = it)
    } ?: 0
}
