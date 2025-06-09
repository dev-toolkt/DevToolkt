package dev.toolkt.core.data_structures.binary_tree

import kotlin.test.assertEquals

fun <DataT> OrderStatisticTree<DataT>.dumpOrder(
    nodeCount: Int,
): List<BinaryTree.NodeHandle<DataT>?> = (0 until nodeCount).map {
    select(index = it)
}

fun <DataT> assertOrder(
    expectedOrder: List<BinaryTree.NodeHandle<DataT>>,
    actualTree: OrderStatisticTree<DataT>,
) {
    val dumpedOrder = actualTree.dumpOrder(expectedOrder.size)

    assertEquals(
        expected = expectedOrder,
        actual = dumpedOrder,
        message = "Dumped order does not match expected order",
    )

    expectedOrder.forEachIndexed { expectedIndex, nodeHandle ->
        val actualIndex = actualTree.getRank(nodeHandle = nodeHandle)

        assertEquals(
            expected = expectedIndex,
            actual = actualIndex,
            message = "Node handle $nodeHandle should have rank $expectedIndex, but got $actualIndex",
        )
    }
}

fun <PayloadT> MutableOrderStatisticTree<PayloadT>.insertVerified(
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

fun <PayloadT> MutableOrderStatisticTree<PayloadT>.swapVerified(
    firstNodeHandle: BinaryTree.NodeHandle<PayloadT>,
    secondNodeHandle: BinaryTree.NodeHandle<PayloadT>,
) {
    this.swap(
        firstNodeHandle = firstNodeHandle,
        secondNodeHandle = secondNodeHandle,
    )

    verifyIntegrity()
}

fun <PayloadT> MutableOrderStatisticTree<PayloadT>.removeLeafVerified(
    leafHandle: BinaryTree.NodeHandle<PayloadT>,
): BinaryTree.Location<PayloadT> {
    val location = this.removeLeafLocated(
        leafHandle = leafHandle,
    )

    verifyIntegrity()

    return location
}
