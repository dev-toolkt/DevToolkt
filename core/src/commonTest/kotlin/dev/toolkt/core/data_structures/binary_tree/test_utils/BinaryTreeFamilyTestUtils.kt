package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getChild
import dev.toolkt.core.data_structures.binary_tree.getSibling
import dev.toolkt.core.data_structures.binary_tree.locateRelatively

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifyFamily(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    expectedParentHandle: BinaryTree.NodeHandle<PayloadT, ColorT>? = null,
    expectedSiblingHandle: BinaryTree.NodeHandle<PayloadT, ColorT>? = null,
    expectedCloseNephewHandle: BinaryTree.NodeHandle<PayloadT, ColorT>? = null,
    expectedDistantNephewHandle: BinaryTree.NodeHandle<PayloadT, ColorT>? = null,
    expectedGrandparentHandle: BinaryTree.NodeHandle<PayloadT, ColorT>? = null,
    expectedUncleHandle: BinaryTree.NodeHandle<PayloadT, ColorT>? = null,
) {
    val relativeLocation = locateRelatively(nodeHandle = nodeHandle)
    val parentHandle = relativeLocation?.parentHandle

    when {
        relativeLocation != null -> {
            val siblingHandle = relativeLocation.getSibling(tree = this)

            when {
                siblingHandle != null -> {
                    val closeNephewHandle = getChild(
                        nodeHandle = siblingHandle,
                        side = relativeLocation.side,
                    )

                    if (closeNephewHandle != expectedCloseNephewHandle) {
                        throw AssertionError("Unexpected close nephew")
                    }

                    val distantNephewHandle = getChild(
                        nodeHandle = siblingHandle,
                        side = relativeLocation.siblingSide,
                    )

                    if (distantNephewHandle != expectedDistantNephewHandle) {
                        throw AssertionError("Unexpected distant nephew")
                    }
                }

                else -> {
                    throw AssertionError("Cannot expect nephews of a node without a sibling")
                }
            }

            if (siblingHandle != expectedSiblingHandle) {
                throw AssertionError("Unexpected sibling")
            }

            val parentRelativeLocation = locateRelatively(nodeHandle = relativeLocation.parentHandle)
            val grandparentHandle = parentRelativeLocation?.parentHandle

            when {
                parentRelativeLocation != null -> {
                    val uncleHandle = parentRelativeLocation.getSibling(tree = this)

                    if (uncleHandle != expectedUncleHandle) {
                        throw AssertionError("Unexpected uncle")
                    }
                }

                else -> {
                    if (expectedUncleHandle != null) {
                        throw AssertionError("Cannot expect an uncle of a node without a grandparent")
                    }
                }

            }

            if (grandparentHandle != expectedGrandparentHandle) {
                throw AssertionError("Unexpected grandparent")
            }
        }

        else -> {
            if (expectedSiblingHandle != null || expectedCloseNephewHandle != null || expectedDistantNephewHandle != null || expectedGrandparentHandle != null) {
                throw AssertionError("Cannot expect a family of a node without a parent")
            }
        }
    }

    if (parentHandle != expectedParentHandle) {
        throw AssertionError("Unexpected parent")
    }
}
