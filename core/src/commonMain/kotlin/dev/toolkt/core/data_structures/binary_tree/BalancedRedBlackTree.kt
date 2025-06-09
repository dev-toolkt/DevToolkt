package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.errors.assert

class BalancedRedBlackTree<DataT> internal constructor(
    subjectTree: RawRedBlackTree<DataT>,
) : BalancedBinaryTree<DataT, BalancedRedBlackTree.Color>(
    subjectTree = subjectTree,
) {
    enum class Color {
        Red, Black,
    }

    companion object {
        fun <DataT> create(
            innerPrototype: UnbalancedBinaryTree.Prototype = BasicBinaryTree,
        ): BalancedRedBlackTree<DataT> = BalancedRedBlackTree(
            subjectTree = innerPrototype.create(),
        )
    }

    constructor() : this(subjectTree = BasicBinaryTree())

    override val defaultBalanceMetadata: Color
        get() = Color.Red

    override fun restoreBalanceAfterLeafInsertion(
        insertedNodeHandle: RawRedBlackNodeHandle<DataT>,
    ) {
        insertedNodeHandle.fixPotentialRedViolationRecursively()
    }

    override fun restoreBalanceAfterLeafRemoval(
        rawLocation: RawRedBlackLocation<DataT>,
    ) {
        val rawRelativeLocation = rawLocation as? RawRedBlackRelativeLocation<DataT> ?: return

        fixBlackViolationRecursively(
            nodeHandle = null,
            relativeLocation = rawRelativeLocation,
        )
    }

    override fun restoreBalanceAfterElevation(
        elevatedNodeHandle: RawRedBlackNodeHandle<DataT>,
    ) {
        elevatedNodeHandle.paint(Color.Black)
    }

    private fun RawRedBlackNodeHandle<DataT>.fixPotentialRedViolationRecursively() {
        val color = getColor()

        assert(color == Color.Red) {
            throw AssertionError("Red violation phase fixup must start with a red node")
        }

        val relativeLocation = locateRelativelyRaw() ?: run {
            // Case #3
            // If this is the root, it can't be in a red violation with its
            // parent, as it has no parent. There is no red violation.

            return
        }

        val parentHandle = relativeLocation.parentHandle
        val side = relativeLocation.side

        val parentColor = parentHandle.getColor()

        if (parentColor == Color.Black) {
            // Case #1
            // If the parent is black, there's no red violation between this
            // node and its parent
            return
        }

        // From now on, we know that the parent is red

        val parentRelativeLocation = parentHandle.locateRelativelyRaw() ?: run {
            // Case #4
            // The parent is the root, so it can't get into a red
            // violation with its parent (as it has no parent). We can fix the
            // red violation by simply changing the root's color to black.

            parentHandle.paint(Color.Black)

            return
        }

        val grandparentHandle = parentRelativeLocation.parentHandle

        assert(grandparentHandle.getColor() == Color.Black) {
            "The grandparent must be black, as the parent is red"
        }

        val uncleHandle = parentRelativeLocation.getSiblingRaw()
        val uncleSide = parentRelativeLocation.siblingSide

        when (uncleHandle?.getColor()) {
            Color.Red -> {
                // Case #2

                // As the uncle is also red (like this node and its parent),
                // we can swap the color of the grandparent (black) with the
                // color of its children (red). This fixed the red violation
                // between this node and its parent.
                parentHandle.paint(Color.Black)
                uncleHandle.paint(Color.Black)
                grandparentHandle.paint(Color.Red)

                // The subtree starting at the fixed node is now balanced

                // While we fixed one red violation, we might've introduced
                // another. Let's fix this recursively.
                grandparentHandle.fixPotentialRedViolationRecursively()
            }

            else -> {
                // N and P are red, he uncle is black

                if (side == uncleSide) {
                    // Case #5: N is the closer grandchild of G.
                    // We can reduce this to a fit for case #6 by a single rotation
                    parentHandle.rotateRaw(
                        direction = uncleSide.directionFrom,
                    )

                    // This operation pushes the fixed node one level down, but
                    // this doesn't affect the remaining logic
                }

                // Case #6: N is the distant grandchild of G

                val newSubtreeRootHandle = grandparentHandle.rotateRaw(
                    uncleSide.directionTo,
                )

                assert(newSubtreeRootHandle == this || newSubtreeRootHandle == parentHandle) {
                    "The new subtree root must be either this node or its parent"
                }

                swapColors(
                    redNodeHandle = newSubtreeRootHandle,
                    blackNodeHandle = grandparentHandle,
                )

                // The violation is fixed!
            }
        }
    }

    private fun fixBlackViolationRecursively(
        /**
         * A handle to the node to be fixed, null represents a null node
         */
        nodeHandle: RawRedBlackNodeHandle<DataT>?,
        /**
         * The relative location of the node to be fixed
         */
        relativeLocation: RawRedBlackRelativeLocation<DataT>,
    ) {
        assert(nodeHandle?.getColor() == Color.Red) {
            "Black violation fixup phase cannot start with a black node"
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
        val wasCase3Applied = nodeHandle.run {
            // If the node is proper, it has a proper sibling from Conclusion 2.
            // If it's a null node (which is possible on the first recursion level),
            // its sibling also must be proper, as it must have a black height one,
            // which was the black height of the node we deleted.
            val sibling = relativeLocation.getSiblingRaw() ?: throw AssertionError("The node has no sibling")

            if (sibling.getColor() != Color.Red) return@run false

            parentHandle.rotateRaw(
                direction = side.directionTo,
            )

            // Now the parent is red and the sibling (old close nephew) is black. Depending on the color of the nephews,
            // it's a match for cases #4, #5 or #6
            true
        }

        // Case #4: P is red (the sibling S is black) and S’s children are black
        nodeHandle.run {
            if (parentHandle.getColor() != Color.Red) return@run

            val sibling = relativeLocation.getSiblingRaw() ?: throw AssertionError("The node has no sibling")

            assert(sibling.getColor() == Color.Black) {
                "The sibling must be black, as the parent is red"
            }

            val (closeNephew, distantNephew) = sibling.getChildrenRaw(
                referenceSide = side,
            )

            if (closeNephew?.getColor() == Color.Red) return@run
            if (distantNephew?.getColor() == Color.Red) return@run

            swapColors(
                redNodeHandle = parentHandle,
                blackNodeHandle = sibling,
            )

            // The violation was fixed!
            return
        }

        // Case #5 S’s close child C is red (the sibling S is black), and S’s distant child D is black
        val wasCase5Applied = nodeHandle.run {
            val sibling = relativeLocation.getSiblingRaw() ?: throw AssertionError("The node has no sibling")

            val (closeNephew, distantNephew) = sibling.getChildrenRaw(
                referenceSide = side,
            )

            if (closeNephew?.getColor() != Color.Red) return@run false
            if (distantNephew?.getColor() == Color.Red) return@run false

            // From now on, we know that the close nephew is red and the distant nephew is effectively black

            assert(sibling.getColor() == Color.Black) {
                "The sibling must be black, as the close nephew is red"
            }

            sibling.rotateRaw(
                direction = side.directionFrom,
            )

            swapColors(
                redNodeHandle = closeNephew,
                blackNodeHandle = sibling,
            )

            // Now the parent color is unchanged and the new sibling (old close nephew) is black. The distant nephew (old
            // sibling) is now red. This is a fit for case #6.
            true
        }

        // Case #6: S’s distant child D is red (the sibling S is black)
        nodeHandle.run {
            val sibling = relativeLocation.getSiblingRaw() ?: throw AssertionError("The node has no sibling")

            val (closeNephew, distantNephew) = sibling.getChildrenRaw(
                referenceSide = side,
            )

            if (distantNephew?.getColor() != Color.Red) return@run

            assert(closeNephew?.getColor() != Color.Red) {
                // FIXME: Maybe there are no assumptions about C's color
                "The close nephew has to be black (DOES IT?)"
            }

            // From now on, we know that the distant nephew is red

            assert(sibling.getColor() == Color.Black) {
                "The sibling must be black, as the distant nephew is red"
            }

            parentHandle.rotateRaw(
                direction = side.directionTo,
            )

            parentHandle.setColor(Color.Black)
            distantNephew.paint(Color.Black)

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

        assert(parentHandle.getColor() == Color.Black) {
            // If the parent was red, it should've triggered case #4 (if the nephews were black) or cases #5/#6 (otherwise)
            "The parent is not black, which is unexpected at this point"
        }

        val siblingHandle = relativeLocation.getSiblingRaw() ?: throw AssertionError("The node has no sibling")

        assert(siblingHandle.getColor() == Color.Black) {
            // If the sibling was red, it should've triggered case #3
            "The sibling is not black, which is unexpected at this point"
        }

        val (closeNephew, distantNephew) = siblingHandle.getChildrenRaw(
            referenceSide = side,
        )

        assert(distantNephew?.getColor() != Color.Red) {
            // If the distant nephew was red, it should've triggered case #6
            "The distant nephew is red, which is unexpected at this point"
        }

        assert(closeNephew?.getColor() != Color.Red) {
            // If the close nephew was red (and the distant nephew is black, which we know), it should've triggered
            // case #5
            "The close nephew is red, which is unexpected at this point"
        }

        // Case #2: P, S, and S’s children are black
        siblingHandle.paint(Color.Red)

        // After paining the sibling red, the subtree starting at this node is balanced

        when (val parentRelativeLocation = parentHandle.locateRelativelyRaw()) {
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

    private fun RawRedBlackNodeHandle<DataT>.getColor(): Color = getEnhancementRaw()

    private fun swapColors(
        redNodeHandle: RawRedBlackNodeHandle<DataT>,
        blackNodeHandle: RawRedBlackNodeHandle<DataT>,
    ) {
        redNodeHandle.paint(Color.Black)
        blackNodeHandle.paint(Color.Red)
    }

    private fun RawRedBlackNodeHandle<DataT>.paint(
        newColor: Color,
    ) {
        val color = getColor()

        if (color == newColor) {
            throw IllegalStateException("The node is already painted $newColor")
        }

        setColor(
            newColor = newColor,
        )
    }

    private fun RawRedBlackNodeHandle<DataT>.setColor(
        newColor: Color,
    ) {
        setEnhancementRaw(
            newEnhancement = newColor,
        )
    }
}

private typealias RawRedBlackEnhancement = BalancedRedBlackTree.Color

private typealias RawRedBlackTree<DataT> = RawEnhancedTree<DataT, RawRedBlackEnhancement>

private typealias RawRedBlackNodeHandle<DataT> = RawEnhancedNodeHandle<DataT, RawRedBlackEnhancement>

private typealias RawRedBlackLocation<DataT> = RawEnhancedLocation<DataT, RawRedBlackEnhancement>

private typealias RawRedBlackRelativeLocation<DataT> = RawEnhancedRelativeLocation<DataT, RawRedBlackEnhancement>
