package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getChild
import dev.toolkt.core.data_structures.binary_tree.getSibling
import dev.toolkt.core.data_structures.binary_tree.locateRelatively
import dev.toolkt.core.data_structures.binary_tree.traverse

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getHandle(
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, ColorT> = traverse().single {
    getPayload(nodeHandle = it) == payload
}
