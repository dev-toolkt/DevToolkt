package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.errors.assert

abstract class AbstractBalancedBinaryTree<PayloadT, ColorT>(
    protected val internalTree: MutableUnbalancedBinaryTree<PayloadT, ColorT>,
) : MutableBalancedBinaryTree<PayloadT, ColorT>, BinaryTree<PayloadT, ColorT> by internalTree {

    final override fun insert(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val insertedNodeHandle = internalTree.attach(
            location = location,
            payload = payload,
            color = defaultColor,
        )

        // Rebalance the tree after insertion
        rebalanceAfterAttach(
            putNodeHandle = insertedNodeHandle,
        )

        return insertedNodeHandle
    }

    final override fun remove(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ) {
        val leftChildHandle = internalTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = internalTree.getRightChild(nodeHandle = nodeHandle)

        if (leftChildHandle != null && rightChildHandle != null) {
            // If the node has two children, we can't directly remove it, but we can swap it with its
            // successor

            val successorHandle = internalTree.getInOrderSuccessor(
                nodeHandle = nodeHandle,
            ) ?: throw AssertionError("A node with two children must have a successor")

            internalTree.swap(
                nodeHandle,
                successorHandle,
            )

            // After the swap, the node has at most one child (as the successor
            // was guaranteed to have at most one child)
        }

        removeDirectly(nodeHandle = nodeHandle)
    }

    /**
     * Remove the node directly, which is possible only if it has at most one child.
     */
    private fun removeDirectly(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ) {
        val leftChildHandle = internalTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = internalTree.getRightChild(nodeHandle = nodeHandle)

        assert(leftChildHandle == null || rightChildHandle == null) {
            "The node must have at most one child, but has both left and right children"
        }

        val singleChildHandle = leftChildHandle ?: rightChildHandle

        when (singleChildHandle) {
            null -> {
                val relativeLocation = internalTree.locateRelatively(nodeHandle = nodeHandle)
                val leafColor = internalTree.getColor(nodeHandle = nodeHandle)

                internalTree.cutOff(leafHandle = nodeHandle)

                if (relativeLocation != null) {
                    rebalanceAfterCutOff(
                        cutOffLeafLocation = relativeLocation,
                        cutOffLeafColor = leafColor,
                    )
                } else {
                    // If we cut off the root, there's no need to rebalance
                }
            }

            else -> {
                val elevatedNodeHandle = internalTree.collapse(nodeHandle = nodeHandle)

                rebalanceAfterCollapse(
                    elevatedNodeHandle = elevatedNodeHandle,
                )
            }
        }
    }

    abstract val defaultColor: ColorT

    abstract fun rebalanceAfterAttach(
        putNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )

    abstract fun rebalanceAfterCutOff(
        cutOffLeafLocation: BinaryTree.RelativeLocation<PayloadT, ColorT>,
        cutOffLeafColor: ColorT,
    )

    abstract fun rebalanceAfterCollapse(
        elevatedNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )
}
