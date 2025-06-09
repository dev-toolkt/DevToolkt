package dev.toolkt.core.data_structures.red_black_tree

/**
 * Red-black tree implementation
 *
 * A proper read-black tree implementation must maintain a set of properties
 * to ensure that the tree remains balanced after insertions and deletions:
 *
 * Property 1. Every node is either red or black (axiom)
 * Property 2. All null nodes are considered black (axiom)
 * Property 3. A red node does not have a red child (invariant)
 * Property 4. Every path from a given node to any of its leaf nodes goes through the same number of black nodes (invariant)
 *
 * Violation of Property 3 is called a red violation.
 * Violation of Property 4 is called a black violation.
 *
 * From these properties, we can derive the following conclusions:
 *
 * Conclusion 0. A red node is never adjacent to another red node. Red nodes need to be interleaved with black nodes. Only black nodes can form a single-color chain.
 *   Proof: A direct consequence of Property 3
 * Conclusion 1. If a node N has exactly one proper child, the child must be red.
 *   Proof: If the child was black, its null leaves would sit at a depper black depth than N's null child (violating Property 4)
 * Conclusion 2. A proper non-root black node N has a proper sibling (no black node is a single proper child)
 *   Proof I: If a proper non-root black node didn't have a proper sibling, the path to its (eventual) null descendants would be longer from the path to its null sibling (from Property 4)
 *   Proof II: A non-root node without a sibling is a single proper child of its parent, and such child must be red (from Conclusion 1)
 */
internal object RedBlackTreeImpl {
    enum class Color {
        Red, Black,
    }

    sealed class RotationDirection {
        data object Clockwise : RotationDirection() {
            override val opposite = CounterClockwise

            override val startSide = RedBlackTree.Side.Left
        }

        data object CounterClockwise : RotationDirection() {
            override val opposite = Clockwise

            override val startSide = RedBlackTree.Side.Right
        }

        abstract val opposite: RotationDirection

        abstract val startSide: RedBlackTree.Side

        val endSide: RedBlackTree.Side
            get() = startSide.opposite
    }

    enum class RemovalFixupResult {
        NonApplicable, Applied,
    }

    sealed class ParentLink<T> {
        abstract val parent: ParentNode<T>

        fun clearChild(): NullNode<T> {
            linkChild(newChild = null)

            return NullNode(parentLink = this)
        }

        fun replaceChild(
            newChild: ProperNode<T>,
        ): ChildNode<T> {
            linkChild(newChild)

            return newChild
        }

        abstract fun linkChild(
            newChild: ProperNode<T>?,
        )
    }

    class OriginLink<T>(
        private val origin: OriginNode<T>,
    ) : ParentLink<T>() {
        override val parent: ParentNode<T>
            get() = origin

        override fun linkChild(
            newChild: ProperNode<T>?,
        ) {
            origin.setRoot(
                newRoot = newChild
            )

            newChild?.setParent(
                newParent = origin,
            )
        }
    }

    class ProperParentLink<T>(
        override val parent: ProperNode<T>,
        val childSide: RedBlackTree.Side,
    ) : ParentLink<T>() {
        override fun linkChild(
            newChild: ProperNode<T>?,
        ) {
            ProperNode.linkChild(
                parent = parent,
                child = newChild,
                side = childSide,
            )
        }

        /**
         * The younger family of the node linked by this parent link, or null if
         * that node has no proper sibling
         */
        val youngerFamily: YoungerFamily<T>?
            get() {
                val properSibling = this.properSibling ?: return null

                return YoungerFamily(
                    properParentLink = this,
                    properSibling = properSibling,
                )
            }

        /**
         * The older family of the node linked by this parent link, or null if
         * that node has no proper grandparent
         */
        val olderFamily: OlderFamily<T>?
            get() = parent.properParentLink?.let {
                OlderFamily(
                    parentLink = this,
                    grandparentLink = it,
                )
            }

        val siblingSide: RedBlackTree.Side
            get() = childSide.opposite

        val sibling: ChildNode<T>
            get() = parent.getChild(side = siblingSide)

        private val properSibling: ProperNode<T>?
            get() = parent.getProperChild(side = siblingSide)
    }

    /**
     * The "younger" family is the node's proper parent (which it must have), its
     * proper sibling (which it must have) and its nephews (not necessarily
     * proper). This part of the tree is crucial during removal fixups.
     */
    class YoungerFamily<T>(
        val properParentLink: ProperParentLink<T>,
        val properSibling: ProperNode<T>,
    ) {
        val parent: ProperNode<T>
            get() = properParentLink.parent

        val childSide: RedBlackTree.Side
            get() = properParentLink.childSide

        val closeNephew: ChildNode<T>
            get() = properSibling.getChild(childSide)

        val distantNephew: ChildNode<T>
            get() = properSibling.getChild(childSide.opposite)
    }

    /**
     * The "older" family is the node's its proper parent (which it must have),
     * its proper grandparent (which it must have) and the parent's sibling called
     * "the uncle" (not necessarily proper). This part of the tree is crucial during
     * insert fixups.
     */
    class OlderFamily<T>(
        private val parentLink: ProperParentLink<T>,
        private val grandparentLink: ProperParentLink<T>,
    ) {
        val parent: ProperNode<T>
            get() = parentLink.parent

        val childSide: RedBlackTree.Side
            get() = parentLink.childSide

        val grandparent: ProperNode<T>
            get() = grandparentLink.parent

        val uncle: ChildNode<T>
            get() = grandparentLink.sibling

        val uncleSide: RedBlackTree.Side
            get() = grandparentLink.siblingSide
    }

    sealed interface ParentNode<T> {
        fun link(child: ProperNode<T>): ParentLink<T>

        fun addChildDescending(
            child: ProperNode<T>,
            side: RedBlackTree.Side,
        )

        fun dump(): String

        fun dumpNode(): RedBlackTree.DumpedNode<T>?

        val inOrderTraversal: Sequence<T>
    }

    sealed class ChildNode<T> {
        /**
         * Fixes a potential black violation that might have been introduced
         * by removing this node from the tree.
         *
         * Postcondition: the tree is balanced, having no red or black violations.
         */
        fun fixupRemoval() {
            if (effectiveColor != Color.Black) {
                throw IllegalArgumentException("Unexpected node color")
            }

            if (parent is OriginNode<T>) {
                // Case #1
                // There's no black violation (anymore?)
                return
            }

            // There is a black violation, but we can fix it

            // If the node is proper, it has a proper sibling from Conclusion 2.
            // If it's a null node (which is possible on the first recursion level),
            // its sibling also must be proper, as it must have a black height one,
            // which was the black height of the node we deleted. As the node
            // has a proper sibling, we can build its younger family. This
            // is an invariant, none of the fixups leave the node without a proper
            // sibling after mutating the tree.

            val siblingResult = tryRedSiblingRemovalFixup()

            val parentResult = tryRedParentRemovalFixup()

            if (parentResult == RemovalFixupResult.Applied) {
                // This fixup is final
                return
            }

            val closeNephewResult = tryRedCloseNephewRemovalFixup()

            val distantNephewResult = tryRedDistantNephewRemovalFixup()

            if (distantNephewResult == RemovalFixupResult.Applied) {
                // This fixup is final
                return
            }

            if (closeNephewResult == RemovalFixupResult.Applied) {
                throw AssertionError("Case #5 application should always lead to Case #6 application")
            }

            if (siblingResult == RemovalFixupResult.Applied) {
                throw AssertionError("Case #3 application should always lead to Case #4 or Case #6 application")
            }

            val finalFamily =
                youngerFamily ?: throw IllegalStateException("Could not construct the node's final younger family")

            val finalParent = finalFamily.parent
            val finalSibling = finalFamily.properSibling

            if (finalParent.properColor != Color.Black) throw AssertionError("Unexpected final parent color")
            if (finalSibling.properColor != Color.Black) throw AssertionError("Unexpected final sibling color")
            if (finalFamily.closeNephew.effectiveColor != Color.Black) throw AssertionError("Unexpected final close nephew color")
            if (finalFamily.distantNephew.effectiveColor != Color.Black) throw AssertionError("Unexpected final distant nephew color")

            // Case #2
            finalSibling.paintRed()

            finalParent.fixupRemoval()
        }

        /**
         * The younger family of this node, or null if this node has no proper
         * parent or no proper sibling
         */
        private val youngerFamily: YoungerFamily<T>?
            get() = this.properParentLink?.youngerFamily

        /**
         * The older family of this node, or null if this node has no proper
         * parent or no proper grandparent
         */
        val olderFamily: OlderFamily<T>?
            get() = this.properParentLink?.olderFamily

        private val asProper: ProperNode<T>?
            get() = this as? ProperNode<T>

        val properParentLink: ProperParentLink<T>?
            get() = parentLink as? ProperParentLink<T>

        /**
         * Case #3
         */
        private fun tryRedSiblingRemovalFixup(): RemovalFixupResult {
            val family = youngerFamily ?: throw IllegalStateException("Could not construct the node's younger family")
            val properSibling = family.properSibling

            if (properSibling.properColor != Color.Red) {
                return RemovalFixupResult.NonApplicable
            }

            val parent = family.parent

            require(parent.properColor == Color.Black)
            require(family.closeNephew.effectiveColor == Color.Black)
            require(family.distantNephew.effectiveColor == Color.Black)

            val direction = family.childSide.directionTo

            parent.rotate(
                direction = direction,
            )

            ProperNode.swapColors(
                redNode = properSibling,
                blackNode = parent,
            )

            return RemovalFixupResult.Applied
        }

        /**
         * Case #4
         */
        private fun tryRedParentRemovalFixup(): RemovalFixupResult {
            val family = youngerFamily ?: throw IllegalStateException("Could not construct the node's younger family")
            val parent = family.parent
            val properSibling = family.properSibling

            if (parent.properColor != Color.Red) return RemovalFixupResult.NonApplicable

            require(properSibling.properColor == Color.Black)

            if (family.closeNephew.effectiveColor != Color.Black) return RemovalFixupResult.NonApplicable
            if (family.distantNephew.effectiveColor != Color.Black) return RemovalFixupResult.NonApplicable

            ProperNode.swapColors(
                redNode = parent,
                blackNode = properSibling,
            )

            return RemovalFixupResult.Applied
        }

        /**
         * Case #5
         */
        private fun tryRedCloseNephewRemovalFixup(): RemovalFixupResult {
            val family = youngerFamily ?: throw IllegalStateException("Could not construct the node's younger family")
            val properSibling = family.properSibling

            if (properSibling.properColor != Color.Black) return RemovalFixupResult.NonApplicable

            val closeNephew = family.closeNephew.asProper ?: return RemovalFixupResult.NonApplicable
            val distantNephew = family.distantNephew

            if (closeNephew.properColor != Color.Red) return RemovalFixupResult.NonApplicable
            if (distantNephew.effectiveColor != Color.Black) return RemovalFixupResult.NonApplicable

            properSibling.rotate(
                direction = family.childSide.directionFrom,
            )

            ProperNode.swapColors(
                redNode = closeNephew,
                blackNode = properSibling,
            )

            return RemovalFixupResult.Applied
        }

        /**
         * Case #6
         */
        private fun tryRedDistantNephewRemovalFixup(): RemovalFixupResult {
            val family = youngerFamily ?: throw IllegalStateException("Could not construct the node's younger family")
            val parent = family.parent
            val properSibling = family.properSibling

            if (properSibling.properColor != Color.Black) return RemovalFixupResult.NonApplicable

            val closeNephew = family.closeNephew
            val distantNephew = family.distantNephew.asProper ?: return RemovalFixupResult.NonApplicable

            if (distantNephew.properColor != Color.Red) return RemovalFixupResult.NonApplicable

            require(closeNephew.effectiveColor == Color.Black)

            parent.rotate(
                direction = family.childSide.directionTo,
            )

            parent.paintBlack()
            distantNephew.paintBlack()

            return RemovalFixupResult.Applied
        }

        abstract val parent: ParentNode<T>

        abstract val parentLink: ParentLink<T>

        abstract fun paintBlack()

        abstract val effectiveColor: Color
    }

    class OriginNode<T> : ParentNode<T> {
        private var currentRoot: ProperNode<T>? = null

        fun verifyIntegrity() {
            currentRoot?.verifyIntegrity(
                expectedParent = this,
            )
        }

        override fun dump(): String = currentRoot.dumpOrNullString()

        override fun dumpNode(): RedBlackTree.DumpedNode<T>? = currentRoot?.dumpNode()

        fun setRoot(newRoot: ProperNode<T>?) {
            currentRoot = newRoot
        }

        override fun link(
            child: ProperNode<T>,
        ): ParentLink<T> {
            if (child != currentRoot) {
                throw IllegalArgumentException("The child must be the current root of the tree")
            }

            return OriginLink(
                origin = this,
            )
        }

        override fun addChildDescending(
            child: ProperNode<T>,
            side: RedBlackTree.Side,
        ) {
            when (val foundRoot = currentRoot) {
                null -> {
                    if (child.parent != this) {
                        throw IllegalArgumentException("The child's parent is not the origin node")
                    }

                    setRoot(newRoot = child)
                }

                else -> {
                    foundRoot.addChildDescending(
                        child = child,
                        side = side,
                    )
                }
            }
        }


        override val inOrderTraversal: Sequence<T>
            get() = currentRoot?.inOrderTraversal ?: emptySequence()
    }

    /**
     * A null node is a special node representing the lack of a proper child. By
     * definition, it has no child nodes, proper or null. Null nodes are considered
     * black.
     */
    class NullNode<T>(
        override val parentLink: ParentLink<T>,
    ) : ChildNode<T>() {
        override val parent: ParentNode<T>
            get() = parentLink.parent

        override fun paintBlack() {
            // The null node is black by definition
        }

        override val effectiveColor: Color
            get() = Color.Black
    }

    /**
     * A proper node is a node carrying meaningful information. It has exactly
     * two children, but one or both of them can be null nodes. Zero, on or both
     * of the children might be proper.
     */
    class ProperNode<T>(
        private var currentParent: ParentNode<T>,
        private var currentColor: Color,
        private var currentValue: T,
    ) : ChildNode<T>(), ParentNode<T> {
        private enum class State {
            Valid, Invalid,
        }

        companion object {
            fun <T> linkChild(
                parent: ProperNode<T>,
                child: ProperNode<T>?,
                side: RedBlackTree.Side,
            ) {
                parent.setChild(
                    child = child,
                    side = side,
                )

                child?.setParent(
                    newParent = parent,
                )
            }

            fun <T> swapColors(
                redNode: ProperNode<T>,
                blackNode: ProperNode<T>,
            ) {
                if (redNode.effectiveColor != Color.Red) {
                    throw IllegalArgumentException("The first node is supposed to be red")
                }

                if (blackNode.effectiveColor != Color.Black) {
                    throw IllegalArgumentException("The second node is supposed to be black")
                }

                redNode.paintBlack()
                blackNode.paintRed()
            }
        }

        private var state = State.Valid

        override val effectiveColor: Color
            get() = currentColor

        override val parent: ParentNode<T>
            get() = currentParent

        override val parentLink: ParentLink<T>
            get() = currentParent.link(child = this)

        override fun paintBlack() {
            if (currentColor != Color.Red) {
                throw IllegalStateException("The node is already black, can't paint it black again")
            }

            currentColor = Color.Black
        }

        override fun link(
            child: ProperNode<T>,
        ): ParentLink<T> {
            val side = getChildSide(child = child)

            return ProperParentLink(
                parent = this,
                childSide = side,
            )
        }

        /**
         * Add a [child] on the given [side], following that side until the first
         * node that does not have a child on that side.
         *
         * The tree might not be balanced after this operation.
         */
        override fun addChildDescending(
            child: ProperNode<T>,
            side: RedBlackTree.Side,
        ) {
            addChildRecursive(
                child = child,
                firstSide = side,
                secondSide = side,
            )
        }

        val asValid: ProperNode<T>?
            get() = when (state) {
                State.Valid -> this
                State.Invalid -> null
            }

        val properColor: Color
            get() = currentColor

        val childCount: Int
            get() {
                val l = if (leftChild != null) 1 else 0
                val r = if (rightChild != null) 1 else 0
                return l + r
            }

        val singleChildOrNull: ProperNode<T>?
            get() {
                val leftChild = this.leftChild
                val rightChild = this.rightChild

                return when {
                    leftChild == null && rightChild == null -> null
                    else -> leftChild ?: rightChild
                }
            }

        fun getChild(
            side: RedBlackTree.Side,
        ): ChildNode<T> = getProperChild(
            side = side,
        ) ?: NullNode(
            ProperParentLink(
                parent = this,
                childSide = side,
            ),
        )

        fun getProperChild(
            side: RedBlackTree.Side,
        ): ProperNode<T>? = when (side) {
            RedBlackTree.Side.Left -> leftChild
            RedBlackTree.Side.Right -> rightChild
        }

        fun getInOrderSuccessor(): ProperNode<T>? = getInOrderNeighbour(side = RedBlackTree.Side.Right)

        fun setParent(
            newParent: ParentNode<T>,
        ) {
            this.currentParent = newParent
        }

        private fun setChild(
            child: ProperNode<T>?,
            side: RedBlackTree.Side,
        ) {
            when (side) {
                RedBlackTree.Side.Left -> leftChild = child
                RedBlackTree.Side.Right -> rightChild = child
            }
        }

        /**
         * Add a [child] on the given [side], making the new node greater than
         * this node and smaller than its current in-order successor.
         */
        fun addChildAdjacent(
            child: ProperNode<T>,
            side: RedBlackTree.Side,
        ) {
            addChildRecursive(
                child = child,
                firstSide = side,
                secondSide = side.opposite,
            )
        }

        /**
         * Copy the value from another node, assuming that node is going to be
         * discarded.
         */
        fun stealValue(
            sourceNode: ProperNode<T>,
        ) {
            this.currentValue = sourceNode.currentValue
        }

        /**
         * Rotates the subtree rooted at this node in the specified direction.
         * Requires that the child on the starting side of the rotation direction
         * is a proper (non-null) node.
         *
         * @return the new root of the subtree after rotation.
         */
        fun rotate(
            direction: RotationDirection,
        ): ProperNode<T> {
            val originalParentLink = this.parentLink

            val heir = getProperChild(direction.startSide)
                ?: throw IllegalStateException("The new root has to be a proper node")

            val originalInnerGrandchild = heir.getProperChild(direction.endSide)

            linkChild(
                parent = this,
                child = originalInnerGrandchild,
                side = direction.startSide,
            )

            linkChild(
                parent = heir,
                child = this,
                side = direction.endSide,
            )

            originalParentLink.replaceChild(newChild = heir)

            return heir
        }

        /**
         * Fixes a potential red violation between this node and its parent that
         * might have been introduced by inserting this node into the tree.
         *
         * Postcondition: the tree is balanced, having no red or black violations.
         */
        fun fixupInsertion() {
            if (effectiveColor != Color.Red) {
                throw AssertionError("Rebalancing must start with a red node")
            }

            val parent = this.parent as? ProperNode<T> ?: run {
                // Case #3
                // If this is the root, it can't be in a red violation with its
                // parent, as it has no parent. There is no red violation.

                return
            }

            // The parent is red. This is a red violation, but we can fix it.

            if (parent.properColor == Color.Black) {
                // Case #1
                // If the parent is black, there's no red violation between this
                // node and its parent
                return
            }

            // From now

            val olderFamily = this.olderFamily ?: run {
                // Case #4
                // The parent is the root, so it can't get into a red
                // violation with its parent (as it has no parent). We can fix the
                // red violation by simply changing the root's color to black.

                parent.paintBlack()

                return
            }

            val grandparent = olderFamily.grandparent
            val uncle = olderFamily.uncle

            if (grandparent.properColor != Color.Black) {
                // The grandparent should be black, as otherwise it would mean
                // that tree was found in the red violation state
                throw AssertionError("Unexpected grandparent color")
            }

            when (uncle.effectiveColor) {
                Color.Black -> {
                    performInsertionFixup()
                }

                Color.Red -> {
                    // As the uncle is also red (like this node and its parent),
                    // we can swap the color of the grandparent (black) with the
                    // color of its children (red). This fixed the red violation
                    // between this node and its parent.
                    parent.paintBlack()
                    uncle.paintBlack()
                    grandparent.paintRed()

                    // While we fixed one red violation, we might've introduced
                    // another. Let's fix this recursively.
                    grandparent.fixupInsertion()
                }
            }
        }

        fun paintRed() {
            if (currentColor != Color.Black) {
                throw IllegalStateException("The node is already red, can't paint it red again")
            }

            currentColor = Color.Red
        }

        fun invalidate() {
            println("Invalidating node ${hashCode()}")

            when (state) {
                State.Invalid -> {
                    throw IllegalStateException("The node is already invalidated")
                }

                State.Valid -> {
                    state = State.Invalid
                }
            }
        }

        /**
         * Verify the integrity of this node and its children, ensuring that:
         *
         * 1. The node is valid (not invalidated).
         * 2. The parent is consistent with the expected parent.
         * 3. The parent and this node do not violate the red-black tree properties
         *
         * @return the black height
         */
        fun verifyIntegrity(
            expectedParent: ParentNode<T>,
        ): Int {
            if (this.state != State.Valid) {
                throw AssertionError("This note was invalidated")
            }

            val parent = this.parent

            if (parent != expectedParent) {
                throw AssertionError("Inconsistent parent, expected: $expectedParent, actual: ${this.parent}")
            }

            val properParent = parent as? ProperNode<T>

            if (properParent?.properColor == Color.Red && properColor == Color.Red) {
                throw AssertionError("Red violation: both parent and this node are red")
            }

            val leftBlackHeight = leftChild.verifyIntegrityOrOne(
                expectedParent = this,
            )

            val rightBlackHeight = rightChild.verifyIntegrityOrOne(
                expectedParent = this,
            )

            if (leftBlackHeight != rightBlackHeight) {
                throw AssertionError("Black violation: left and right children have different black heights: $leftBlackHeight != $rightBlackHeight")
            }

            val thisBlackHeight = when (properColor) {
                Color.Black -> 1
                Color.Red -> 0
            }

            return thisBlackHeight + leftBlackHeight // or rightBlackHeight, they are equal
        }

        override fun dump(): String =
            "(${leftChild.dumpOrNullString()} [$currentValue | ${currentColor.name}] ${rightChild.dumpOrNullString()})"

        override fun dumpNode(): RedBlackTree.DumpedNode<T> = RedBlackTree.DumpedNode(
            leftNode = leftChild?.dumpNode(),
            value = currentValue,
            rightNode = rightChild?.dumpNode(),
        )

        private var leftChild: ProperNode<T>? = null

        private var rightChild: ProperNode<T>? = null

        private fun getChildSide(
            child: ProperNode<T>,
        ): RedBlackTree.Side = when {
            leftChild === child -> RedBlackTree.Side.Left
            rightChild === child -> RedBlackTree.Side.Right
            else -> throw IllegalArgumentException("The given node is not a child of this node")
        }

        /**
         * Get the in-order neighbour on the given [side] (predecessor for the left side, successor
         * for the right side), or null if there's no such neighbour.
         */
        private fun getInOrderNeighbour(
            side: RedBlackTree.Side,
        ): ProperNode<T>? {
            val sideChild = getProperChild(side = side) ?: return null

            return sideChild.getSideMostDescendant(side = side.opposite)
        }

        /**
         * Get the [side]-most (left-most / right-most) descendant of this node,
         * or this node if this node doesn't have a child on the given [side].
         */
        private fun getSideMostDescendant(
            side: RedBlackTree.Side,
        ): ProperNode<T> {
            val sideChild = getProperChild(side = side) ?: return this
            return sideChild.getSideMostDescendant(side = side)
        }

        /**
         * Cases #5/#6
         */
        private fun performInsertionFixup() {
            if (properColor != Color.Red) {
                throw IllegalArgumentException("The node color should be red")
            }

            val family = olderFamily ?: throw IllegalArgumentException("Could not construct the node's older family")
            val parent = family.parent

            when (family.childSide) {
                family.uncleSide -> {
                    // Case #5
                    parent.rotate(direction = family.uncleSide.directionFrom)

                    parent.performProperInsertionFixup()
                }

                else -> {
                    performProperInsertionFixup()
                }
            }
        }

        /**
         * Case #6
         */
        private fun performProperInsertionFixup() {
            val family = olderFamily ?: throw IllegalArgumentException("Could not construct the node's older family")
            val parent = family.parent
            val grandparent = family.grandparent

            grandparent.rotate(
                direction = family.uncleSide.directionTo,
            )

            swapColors(
                redNode = parent,
                blackNode = grandparent,
            )
        }

        /**
         * Add a [child] on the [firstSide] (if it has no proper child on that
         * side), or add the child on the [secondSide], descending, otherwise.
         */
        private fun addChildRecursive(
            child: ProperNode<T>,
            firstSide: RedBlackTree.Side,
            secondSide: RedBlackTree.Side,
        ) {
            when (val sideChild = getProperChild(side = firstSide)) {
                null -> {
                    linkChild(
                        parent = this,
                        child = child,
                        side = firstSide,
                    )
                }

                else -> sideChild.addChildDescending(
                    child = child,
                    side = secondSide,
                )
            }
        }

        override val inOrderTraversal: Sequence<T>
            get() = leftChild.inOrderTraversalOrEmpty + sequenceOf(currentValue) + rightChild.inOrderTraversalOrEmpty
    }
}

internal val <T> RedBlackTreeImpl.ProperNode<T>?.inOrderTraversalOrEmpty: Sequence<T>
    get() {
        val self = this ?: return emptySequence()
        return self.inOrderTraversal
    }

internal fun <T> RedBlackTreeImpl.ProperNode<T>?.verifyIntegrityOrOne(
    expectedParent: RedBlackTreeImpl.ParentNode<T>,
): Int = when (this) {
    null -> 1
    else -> this.verifyIntegrity(expectedParent = expectedParent)
}


internal fun <T> RedBlackTreeImpl.ProperNode<T>?.dumpOrNullString(): String = when (this) {
    null -> "NULL"
    else -> this.dump()
}
