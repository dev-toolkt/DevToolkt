package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.errors.assert

/**
 * @constructor The constructor that accepts an existing mutable [internalTree]
 * is a low-level functionality. The ownership of that tree passes to this object.
 * The given tree is assumed to initially be a valid red-black tree.
 */
class RedBlackTree<PayloadT>(
    internalTree: MutableUnbalancedBinaryTree<PayloadT, Color> = MutableUnbalancedBinaryTree.create(),
) : AbstractBalancedBinaryTree<PayloadT, RedBlackTree.Color>(
    internalTree = internalTree,
) {
    override val defaultColor: Color
        get() = Color.Red

    enum class Color {
        Red, Black,
    }

    companion object;

    override fun rebalanceAfterAttach(
        putNodeHandle: BinaryTree.NodeHandle<PayloadT, Color>,
    ) {
        fixPotentialRedViolationRecursively(
            nodeHandle = putNodeHandle,
        )
    }

    /**
     * Fix a (potential) red violation in the subtree with the root corresponding
     * to the [nodeHandle].
     */
    private fun fixPotentialRedViolationRecursively(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, Color>,
    ) {
        val color = internalTree.getColor(nodeHandle = nodeHandle)

        assert(color == Color.Red) {
            throw AssertionError("Red violation fixup must start with a red node")
        }

        val relativeLocation = internalTree.locateRelatively(
            nodeHandle = nodeHandle,
        ) ?: run {
            // Case #3
            // If this is the root, it can't be in a red violation with its
            // parent, as it has no parent. We can fix the red violation by simply changing the root's color to black.

            internalTree.paint(
                nodeHandle = nodeHandle,
                newColor = Color.Black,
            )

            return
        }

        val parentHandle = relativeLocation.parentHandle

        val side = relativeLocation.side

        if (internalTree.getColor(nodeHandle = parentHandle) == Color.Black) {
            // Case #1
            // If the parent is black, there's no red violation between this
            // node and its parent
            return
        }

        // From now on, we know that the parent is red

        val parentRelativeLocation = internalTree.locateRelatively(
            nodeHandle = parentHandle,
        ) ?: run {
            // Case #4
            // The parent is the root, so it can't get into a red
            // violation with its parent (as it has no parent). We can fix the
            // red violation by simply changing the root's color to black.

            internalTree.paint(
                nodeHandle = parentHandle,
                newColor = Color.Black,
            )

            return
        }

        val grandparentHandle = parentRelativeLocation.parentHandle

        assert(internalTree.getColor(nodeHandle = grandparentHandle) == Color.Black) {
            "The grandparent must be black, as the parent is red"
        }

        val uncleHandle = internalTree.getSibling(location = parentRelativeLocation)
        val uncleSide = parentRelativeLocation.siblingSide
        val uncleColor = uncleHandle?.let { internalTree.getColor(nodeHandle = it) }

        when (uncleColor) {
            Color.Red -> {
                // Case #2

                // As the uncle is also red (like this node and its parent),
                // we can swap the color of the grandparent (black) with the
                // color of its children (red). This fixed the red violation
                // between this node and its parent.

                internalTree.paint(
                    nodeHandle = parentHandle,
                    newColor = Color.Black,
                )

                internalTree.paint(
                    nodeHandle = uncleHandle,
                    newColor = Color.Black,
                )

                internalTree.paint(
                    nodeHandle = grandparentHandle,
                    newColor = Color.Red,
                )

                // The subtree starting at the fixed node is now balanced

                // While we fixed one red violation, we might've introduced
                // another. Let's fix this recursively.
                fixPotentialRedViolationRecursively(
                    nodeHandle = grandparentHandle,
                )
            }

            else -> {
                // N and P are red, he uncle is black

                if (side == uncleSide) {
                    // Case #5: N is the closer grandchild of G.
                    // We can reduce this to a fit for case #6 by a single rotation
                    internalTree.rotate(
                        pivotNodeHandle = parentHandle,
                        direction = uncleSide.directionFrom,
                    )

                    // This operation pushes the fixed node one level down, but
                    // this doesn't affect the remaining logic
                }

                // Case #6: N is the distant grandchild of G
                val newSubtreeRootHandle = internalTree.rotate(
                    pivotNodeHandle = grandparentHandle,
                    direction = uncleSide.directionTo,
                )

                assert(newSubtreeRootHandle == nodeHandle || newSubtreeRootHandle == parentHandle) {
                    "The new subtree root must be either this node or its parent"
                }

                internalTree.paint(
                    nodeHandle = newSubtreeRootHandle,
                    newColor = Color.Black,
                )

                internalTree.paint(
                    nodeHandle = grandparentHandle,
                    newColor = Color.Red,
                )

                // The violation is fixed!
            }
        }
    }

    override fun rebalanceAfterCutOff(
        cutOffLeafLocation: BinaryTree.RelativeLocation<PayloadT, Color>,
        cutOffLeafColor: Color,
    ) {
        if (cutOffLeafColor == Color.Black) {
            fixBlackViolationRecursively(
                nodeHandle = null,
                relativeLocation = cutOffLeafLocation,
            )
        }
    }

    private fun fixBlackViolationRecursively(
        /**
         * A handle to the node to be fixed, null represents a null node
         */
        nodeHandle: BinaryTree.NodeHandle<PayloadT, Color>?,
        /**
         * The relative location of the node to be fixed
         */
        relativeLocation: BinaryTree.RelativeLocation<PayloadT, Color>,
    ) {
        val color = nodeHandle?.let {
            internalTree.getColor(nodeHandle = it)
        }

        assert(color != Color.Red) {
            "Black violation fixup phase cannot start with a red node"
        }

        // The parent of the fixed node doesn't change during a single fixup phase.
        // Other parts of the close family (sibling, nephews) may change.
        val parentHandle = relativeLocation.parentHandle

        // The side of the fixed node in relation to its parent. It also doesn't
        // change during a single fixup phase.
        val side = relativeLocation.side

        // The primary cases considered below (#3 - #6) are quasi-final, i.e.
        // are either final or lead to a final case. All these cases require
        // that the fixed node has a proper sibling and leave the tree in the
        // state when it still has a proper sibling.

        // Case #3: The sibling S is red, so P and the nephews C and D have to be black
        val wasCase3Applied = run {
            // If the node is proper, it has a proper sibling from Conclusion 2.
            // If it's a null node (which is possible on the first recursion level),
            // its sibling also must be proper, as it must have a black height one,
            // which was the black height of the node we deleted.
            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            if (siblingColor != Color.Red) return@run false

            internalTree.rotate(
                pivotNodeHandle = parentHandle,
                direction = side.directionTo,
            )

            internalTree.paint(
                nodeHandle = parentHandle,
                newColor = Color.Red,
            )

            internalTree.paint(
                nodeHandle = siblingHandle,
                newColor = Color.Black,
            )

            // Now the parent is red and the sibling (old close nephew) is black. Depending on the color of the nephews,
            // it's a match for cases #4, #5 or #6
            true
        }

        // Case #4: P is red (the sibling S is black) and S’s children are black
        run {
            val parentColor = internalTree.getColor(nodeHandle = parentHandle)
            if (parentColor != Color.Red) return@run

            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            assert(siblingColor == Color.Black) {
                "The sibling must be black, as the parent is red"
            }

            val (closeNephewHandle, distantNephewHandle) = internalTree.getChildren(
                nodeHandle = siblingHandle,
                side = side,
            )

            val closeNephewColor = closeNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            val distantNephewColor = distantNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            if (closeNephewColor == Color.Red) return@run
            if (distantNephewColor == Color.Red) return@run

            internalTree.paint(
                nodeHandle = parentHandle,
                newColor = Color.Black,
            )

            internalTree.paint(
                nodeHandle = siblingHandle,
                newColor = Color.Red,
            )

            // The violation was fixed!
            return
        }

        // Case #5 S’s close child C is red (the sibling S is black), and S’s distant child D is black
        val wasCase5Applied = run {
            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            val (closeNephewHandle, distantNephewHandle) = internalTree.getChildren(
                nodeHandle = siblingHandle,
                side = side,
            )

            val closeNephewColor = closeNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            val distantNephewColor = distantNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            if (closeNephewColor != Color.Red) return@run false
            if (distantNephewColor == Color.Red) return@run false

            // From now on, we know that the close nephew is red and the distant nephew is effectively black

            assert(siblingColor == Color.Black) {
                "The sibling must be black, as the close nephew is red"
            }

            internalTree.rotate(
                pivotNodeHandle = siblingHandle,
                direction = side.directionFrom,
            )

            internalTree.paint(
                nodeHandle = closeNephewHandle,
                newColor = Color.Black,
            )

            internalTree.paint(
                nodeHandle = siblingHandle,
                newColor = Color.Red,
            )

            // Now the parent color is unchanged and the new sibling (old close nephew) is black. The distant nephew (old
            // sibling) is now red. This is a fit for case #6.
            true
        }

        // Case #6: S’s distant child D is red (the sibling S is black)
        run {
            val parentColor = internalTree.getColor(nodeHandle = parentHandle)

            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            val (_, distantNephewHandle) = internalTree.getChildren(
                nodeHandle = siblingHandle,
                side = side,
            )

            val distantNephewColor = distantNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            if (distantNephewColor != Color.Red) return@run

            // From now on, we know that the distant nephew is red

            assert(siblingColor == Color.Black) {
                "The sibling must be black, as the distant nephew is red"
            }

            internalTree.rotate(
                pivotNodeHandle = parentHandle,
                direction = side.directionTo,
            )

            internalTree.setColor(
                nodeHandle = parentHandle,
                newColor = Color.Black,
            )

            internalTree.setColor(
                nodeHandle = siblingHandle,
                newColor = parentColor,
            )

            internalTree.paint(
                nodeHandle = distantNephewHandle,
                newColor = Color.Black,
            )

            // The violation was fixed!
            return
        }

        if (wasCase3Applied) {
            throw AssertionError("Case #3 application should always lead to Case #4 or Case #6 application")
        }

        if (wasCase5Applied) {
            throw AssertionError("Case #5 application should always lead to Case #6 application")
        }

        // Now we know that none of the primary cases applied

        val parentColor = internalTree.getColor(nodeHandle = parentHandle)

        assert(parentColor == Color.Black) {
            // If the parent was red, it should've triggered case #4 (if both nephews were black) or cases #5/#6 (otherwise)
            "The parent is not black, which is unexpected at this point"
        }

        val siblingHandle = internalTree.getSibling(
            location = relativeLocation,
        ) ?: throw AssertionError("The node has no sibling")

        val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

        assert(siblingColor == Color.Black) {
            // If the sibling was red, it should've triggered case #3 (and later one of the final cases)
            "The sibling is not black, which is unexpected at this point"
        }

        val (closeNephewHandle, distantNephewHandle) = internalTree.getChildren(
            nodeHandle = siblingHandle,
            side = side,
        )

        val closeNephewColor = closeNephewHandle?.let {
            internalTree.getColor(nodeHandle = it)
        }

        val distantNephewColor = distantNephewHandle?.let {
            internalTree.getColor(nodeHandle = it)
        }

        assert(distantNephewColor != Color.Red) {
            // If the distant nephew was red, it should've triggered case #6 (a final case)
            "The distant nephew is red, which is unexpected at this point"
        }

        assert(closeNephewColor != Color.Red) {
            // We just checked that the distant nephew is black
            // If the close nephew was red, it should've triggered case #5 (and later case #6)
            "The close nephew is red, which is unexpected at this point"
        }

        // Case #2: P, S, and S’s children are black
        internalTree.paint(
            nodeHandle = siblingHandle,
            newColor = Color.Red,
        )

        // After paining the sibling red, the subtree starting at this node is balanced

        when (val parentRelativeLocation = internalTree.locateRelatively(nodeHandle = parentHandle)) {
            null -> {
                // Case #1: The parent is root
                // The violation was fixed!
                return
            }

            else -> {
                // Although the subtree is balanced (has the same black height on each path), it's still one less than
                // all the other paths in the whole tree. We need to fix it recursively.
                fixBlackViolationRecursively(
                    nodeHandle = parentHandle,
                    relativeLocation = parentRelativeLocation,
                )
            }
        }
    }

    override fun rebalanceAfterCollapse(
        elevatedNodeHandle: BinaryTree.NodeHandle<PayloadT, Color>,
    ) {
        // As the elevated node was a single child of its parent, it must be
        // a red node
        internalTree.paint(
            nodeHandle = elevatedNodeHandle,
            newColor = Color.Black,
        )
    }
}
