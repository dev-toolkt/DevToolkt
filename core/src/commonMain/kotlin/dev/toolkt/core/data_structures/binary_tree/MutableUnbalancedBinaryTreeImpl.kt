package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.BinaryTree.Side
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTreeImpl.HandleImpl
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTreeImpl.ProperNode
import kotlin.jvm.JvmInline

class MutableUnbalancedBinaryTreeImpl<PayloadT, ColorT> internal constructor(
    internal val origin: OriginNode<PayloadT, ColorT> = OriginNode(),
) : MutableUnbalancedBinaryTree<PayloadT, ColorT> {
    internal sealed interface ParentNode<PayloadT, ColorT> {
        fun buildUpLink(
            child: ProperNode<PayloadT, ColorT>,
        ): UpLink<PayloadT, ColorT>
    }

    internal class OriginNode<PayloadT, ColorT>(
        private var mutableRoot: ProperNode<PayloadT, ColorT>? = null,
    ) : ParentNode<PayloadT, ColorT> {
        val root: ProperNode<PayloadT, ColorT>?
            get() = mutableRoot

        fun setRoot(
            newRoot: ProperNode<PayloadT, ColorT>?,
        ) {
            mutableRoot = newRoot
        }

        override fun buildUpLink(
            child: ProperNode<PayloadT, ColorT>,
        ): UpLink<PayloadT, ColorT> {
            if (child != root) {
                throw IllegalArgumentException("Child node must be the root of the tree")
            }

            return OriginLink(
                origin = this,
            )
        }

    }

    internal class ProperNode<PayloadT, ColorT>(
        private var mutableParent: ParentNode<PayloadT, ColorT>,
        private var mutableLeftChild: ProperNode<PayloadT, ColorT>? = null,
        private var mutableRightChild: ProperNode<PayloadT, ColorT>? = null,
        private var mutableSubtreeSize: Int = 1,
        private var mutableColor: ColorT,
        private var mutablePayload: PayloadT,
    ) : ParentNode<PayloadT, ColorT> {
        enum class Validity {
            Valid, Invalid,
        }

        data class IntegrityVerificationResult(
            val computedSubtreeSize: Int,
        )

        private var validity = Validity.Valid

        val isValid: Boolean
            get() = validity == Validity.Valid

        companion object {
            fun <PayloadT, ColorT> link(
                parent: ProperNode<PayloadT, ColorT>,
                side: Side,
                child: ProperNode<PayloadT, ColorT>?,
            ) {
                if (parent == child) {
                    throw IllegalArgumentException("Cannot link a node with itself")
                }

                parent.setChild(
                    child = child,
                    side = side,
                )

                child?.setParent(
                    parent = parent,
                )
            }
        }

        override fun buildUpLink(
            child: ProperNode<PayloadT, ColorT>,
        ): UpLink<PayloadT, ColorT> = ParentLink(
            parent = this,
            childSide = getChildSide(child = child),
        )

        val parent: ParentNode<PayloadT, ColorT>
            get() = mutableParent

        val upLink: UpLink<PayloadT, ColorT>
            get() = parent.buildUpLink(
                child = this,
            )

        val parentLink: ParentLink<PayloadT, ColorT>?
            get() = upLink as? ParentLink<PayloadT, ColorT>

        val properParent: ProperNode<PayloadT, ColorT>?
            get() = parentLink?.parent

        val leftChild: ProperNode<PayloadT, ColorT>?
            get() = mutableLeftChild

        val rightChild: ProperNode<PayloadT, ColorT>?
            get() = mutableRightChild

        val subtreeSize: Int
            get() = mutableSubtreeSize

        val payload: PayloadT
            get() = mutablePayload

        val color: ColorT
            get() = mutableColor

        fun isLeaf(): Boolean = leftChild == null && rightChild == null

        fun getChild(
            side: Side,
        ): ProperNode<PayloadT, ColorT>? = when (side) {
            Side.Left -> leftChild
            Side.Right -> rightChild
        }

        fun getChildSide(
            child: ProperNode<PayloadT, ColorT>,
        ): Side = when {
            child === leftChild -> Side.Left
            child === rightChild -> Side.Right
            else -> throw IllegalArgumentException("The given node is not a child of this node")
        }

        fun setChild(
            child: ProperNode<PayloadT, ColorT>?,
            side: Side,
        ) {
            requireValid()

            when (side) {
                Side.Left -> mutableLeftChild = child
                Side.Right -> mutableRightChild = child
            }
        }

        fun setParent(
            parent: ParentNode<PayloadT, ColorT>,
        ) {
            requireValid()

            if (parent == this) {
                throw IllegalArgumentException("Cannot set a node as its own parent")
            }

            mutableParent = parent
        }

        fun setSubtreeSize(
            size: Int,
        ) {
            requireValid()

            require(size >= 0)

            mutableSubtreeSize = size
        }

        fun setPayload(
            payload: PayloadT,
        ) {
            requireValid()

            mutablePayload = payload
        }

        fun setColor(
            color: ColorT,
        ) {
            requireValid()

            mutableColor = color
        }

        fun verifyIntegrity(
            expectedParent: ParentNode<PayloadT, ColorT>,
        ): IntegrityVerificationResult {
            requireValid()

            if (parent != expectedParent) {
                throw AssertionError("Inconsistent parent, expected: $expectedParent, actual: ${this.parent}")
            }

            val computedLeftSubtreeSize = leftChild?.verifyIntegrity(
                expectedParent = this,
            )?.computedSubtreeSize ?: 0

            val computedRightSubtreeSize = rightChild?.verifyIntegrity(
                expectedParent = this,
            )?.computedSubtreeSize ?: 0

            val computedTotalSubtreeSize = computedLeftSubtreeSize + 1 + computedRightSubtreeSize

            if (mutableSubtreeSize != computedTotalSubtreeSize) {
                throw AssertionError("Inconsistent cached subtree size, true: $computedTotalSubtreeSize, cached: $mutableSubtreeSize")
            }

            return IntegrityVerificationResult(
                computedSubtreeSize = computedTotalSubtreeSize,
            )
        }

        fun updateSubtreeSizeRecursively(
            /**
             * The number of gained descendants. If negative, it means the node
             * lost descendants.
             */
            delta: Int,
        ) {
            requireValid()

            setSubtreeSize(subtreeSize + delta)

            properParent?.updateSubtreeSizeRecursively(
                delta = delta,
            )
        }

        private fun requireValid() {
            if (validity == Validity.Invalid) {
                throw IllegalStateException("The node is already invalidated")
            }
        }

        fun invalidate() {
            if (validity == Validity.Invalid) {
                throw IllegalStateException("The node is already invalidated")
            }

            validity = Validity.Invalid
        }
    }

    internal sealed class UpLink<PayloadT, ColorT> {
        abstract val parent: ParentNode<PayloadT, ColorT>

        abstract val childLocation: BinaryTree.Location<PayloadT, ColorT>

        fun clearChild() {
            linkChild(newChild = null)
        }

        abstract fun linkChild(
            newChild: ProperNode<PayloadT, ColorT>?,
        )
    }

    internal class OriginLink<PayloadT, ColorT>(
        private val origin: OriginNode<PayloadT, ColorT>,
    ) : UpLink<PayloadT, ColorT>() {
        override val parent: ParentNode<PayloadT, ColorT>
            get() = origin

        override val childLocation: BinaryTree.Location<PayloadT, ColorT>
            get() = BinaryTree.RootLocation

        override fun linkChild(
            newChild: ProperNode<PayloadT, ColorT>?,
        ) {
            origin.setRoot(
                newRoot = newChild
            )

            newChild?.setParent(
                parent = origin,
            )
        }
    }

    internal class ParentLink<PayloadT, ColorT>(
        override val parent: ProperNode<PayloadT, ColorT>,
        val childSide: Side,
    ) : UpLink<PayloadT, ColorT>() {
        val siblingSide: Side
            get() = childSide.opposite

        val sibling: ProperNode<PayloadT, ColorT>?
            get() = parent.getChild(side = siblingSide)

        override val childLocation: BinaryTree.Location<PayloadT, ColorT>
            get() = BinaryTree.RelativeLocation(
                parentHandle = parent.pack(),
                side = childSide,
            )

        override fun linkChild(
            newChild: ProperNode<PayloadT, ColorT>?,
        ) {
            ProperNode.link(
                parent = parent,
                child = newChild,
                side = childSide,
            )
        }
    }

    @JvmInline
    internal value class HandleImpl<PayloadT, ColorT>(
        private val properNode: ProperNode<PayloadT, ColorT>,
    ) : BinaryTree.NodeHandle<PayloadT, ColorT> {
        init {
            require(properNode.isValid) {
                "The node is already invalidated"
            }
        }

        fun resolve(): ProperNode<PayloadT, ColorT> {
            if (!properNode.isValid) {
                throw IllegalStateException("The node has been invalidated")
            }

            return properNode
        }

        override val isValid: Boolean
            get() = properNode.isValid
    }

    /**
     * Put a new node with the given [payload] and [color] at the given free
     * [location].
     *
     * @return A handle to the put node
     * @throws IllegalArgumentException if the location is taken
     */
    override fun attach(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
        color: ColorT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val newNode = ProperNode(
            mutableParent = origin,
            mutableColor = color,
            mutablePayload = payload,
        )

        when (location) {
            BinaryTree.RootLocation -> {
                if (origin.root != null) {
                    throw IllegalStateException("The tree already has a root")
                }

                origin.setRoot(
                    newRoot = newNode,
                )
            }

            is BinaryTree.RelativeLocation<PayloadT, ColorT> -> {
                val parent = location.parentHandle.unpack()
                val side = location.side

                val existingChild = resolveImpl(location = location)

                if (existingChild != null) {
                    throw IllegalStateException("Cannot insert leaf to a non-empty location")
                }

                ProperNode.link(
                    parent = parent,
                    side = side,
                    child = newNode,
                )

                parent.updateSubtreeSizeRecursively(
                    delta = +1,
                )
            }
        }

        return newNode.pack()
    }

    /**
     * Remove the leaf corresponding to the given [leafHandle] from the tree.
     *
     * @throws IllegalArgumentException if the node is not really a leaf
     */
    override fun cutOff(
        leafHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.Location<PayloadT, ColorT> {
        val node = leafHandle.unpack()

        if (!node.isLeaf()) {
            throw IllegalArgumentException("The given node is not a leaf")
        }

        val parentLink = node.upLink

        val properParent = parentLink.parent.asProper

        parentLink.clearChild()

        properParent?.updateSubtreeSizeRecursively(
            delta = -1,
        )

        node.invalidate()

        return parentLink.childLocation
    }

    /**
     * Elevate the node corresponding to the given [nodeHandle] (replace its
     * parent with this node). Requires that this node is not the root and has
     * no sibling. May result in the tree re-balancing.
     *
     * Remove a single-child node corresponding to the given [nodeHandle] from
     * the tree by replacing it with its child.
     *
     * @throws IllegalArgumentException if the node is a root or has a sibling
     */
    override fun collapse(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val node = nodeHandle.unpack()

        if (node.leftChild != null && node.rightChild != null) {
            throw IllegalArgumentException("Cannot collapse a node with two children")
        }

        val singleChild = node.leftChild ?: node.rightChild ?: throw IllegalArgumentException("Cannot collapse a leaf")

        val parentLink = node.upLink

        parentLink.linkChild(singleChild)

        parentLink.parent.asProper?.updateSubtreeSizeRecursively(
            delta = -1,
        )

        node.invalidate()

        return singleChild.pack()
    }

    /**
     * Swap two given nodes. Doesn't affect the colors, meaning that the first
     * node will have the second node's color after the swap and the second
     * node will have the first node's color.
     */
    override fun swap(
        firstNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        secondNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ) {
        val firstNode = firstNodeHandle.unpack()
        val secondNode = secondNodeHandle.unpack()

        require(firstNode != secondNode) {
            "Cannot swap a node with itself"
        }

        when {
            firstNode.parent == secondNode -> {
                swapWithParent(
                    parent = secondNode,
                    node = firstNode,
                )
            }

            secondNode.parent == firstNode -> {
                swapWithParent(
                    parent = firstNode,
                    node = secondNode,
                )
            }

            else -> {
                swapDisjoint(
                    firstNode = firstNode,
                    secondNode = secondNode,
                )
            }
        }
    }

    private fun swapWithParent(
        parent: ProperNode<PayloadT, ColorT>,
        node: ProperNode<PayloadT, ColorT>,
    ) {
        val parentSubtreeSize = parent.subtreeSize
        val parentColor = parent.color
        val grandparentLink = parent.upLink

        val nodeSubtreeSize = node.subtreeSize
        val nodeColor = node.color

        val side = parent.getChildSide(child = node)
        val siblingSide = side.opposite

        val sibling = parent.getChild(side = siblingSide)

        val leftChild = node.leftChild
        val rightChild = node.rightChild

        grandparentLink.linkChild(
            newChild = node,
        )

        ProperNode.link(
            parent = node,
            child = parent,
            side = side,
        )

        ProperNode.link(
            parent = node,
            child = sibling,
            side = siblingSide,
        )

        node.setSubtreeSize(parentSubtreeSize)
        node.setColor(parentColor)

        ProperNode.link(
            parent = parent,
            child = leftChild,
            side = Side.Left,
        )

        ProperNode.link(
            parent = parent,
            child = rightChild,
            side = Side.Right,
        )

        parent.setSubtreeSize(nodeSubtreeSize)
        parent.setColor(nodeColor)
    }

    private fun swapDisjoint(
        firstNode: ProperNode<PayloadT, ColorT>,
        secondNode: ProperNode<PayloadT, ColorT>,
    ) {
        val firstParentLink = firstNode.upLink
        val firstLeftChild = firstNode.leftChild
        val firstRightChild = firstNode.rightChild
        val firstSubtreeSize = firstNode.subtreeSize
        val firstColor = firstNode.color

        val secondParentLink = secondNode.upLink
        val secondLeftChild = secondNode.leftChild
        val secondRightChild = secondNode.rightChild
        val secondSubtreeSize = secondNode.subtreeSize
        val secondColor = secondNode.color

        firstParentLink.linkChild(secondNode)

        ProperNode.link(
            parent = secondNode,
            child = firstLeftChild,
            side = Side.Left,
        )

        ProperNode.link(
            parent = secondNode,
            child = firstRightChild,
            side = Side.Right,
        )

        secondNode.setSubtreeSize(firstSubtreeSize)
        secondNode.setColor(firstColor)

        secondParentLink.linkChild(firstNode)

        ProperNode.link(
            parent = firstNode,
            child = secondLeftChild,
            side = Side.Left,
        )

        ProperNode.link(
            parent = firstNode,
            child = secondRightChild,
            side = Side.Right,
        )

        firstNode.setSubtreeSize(secondSubtreeSize)
        firstNode.setColor(secondColor)
    }

    /**
     * Rotate the subtree starting at node corresponding to [pivotNodeHandle] in
     * the given direction.
     *
     * @return A handle to the new root of the subtree after the rotation
     * @throws IllegalStateException if the pivot has no child on the respective side
     */
    override fun rotate(
        pivotNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        direction: BinaryTree.RotationDirection,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val pivotNode = pivotNodeHandle.unpack()

        val parentLink = pivotNode.upLink

        val ascendingChild = pivotNode.getChild(side = direction.startSide)
            ?: throw IllegalStateException("The pivot node has no child on the ${direction.startSide} side")

        val closeGrandchild = ascendingChild.getChild(side = direction.endSide)

        val distantGrandchild = ascendingChild.getChild(side = direction.startSide)

        ProperNode.link(
            parent = pivotNode,
            child = closeGrandchild,
            side = direction.startSide,
        )

        ProperNode.link(
            parent = ascendingChild,
            child = pivotNode,
            side = direction.endSide,
        )

        parentLink.linkChild(
            newChild = ascendingChild,
        )

        val originalPivotNodeSubtreeSize = pivotNode.subtreeSize
        val originalDistantGrandchildSize = distantGrandchild.subtreeSizeOrZero

        // The ascending node has exactly the same set of descendants as the pivot
        // node had before (with the exception that the parent-child relation
        // inverted, but that doesn't affect the subtree size)
        ascendingChild.setSubtreeSize(originalPivotNodeSubtreeSize)

        // The pivot node lost descendants in the subtree of its original
        // distant grandchild. It also lost the ascending child.
        pivotNode.setSubtreeSize(originalPivotNodeSubtreeSize - originalDistantGrandchildSize - 1)

        return ascendingChild.pack()
    }

    override val root: BinaryTree.NodeHandle<PayloadT, ColorT>?
        get() = origin.root?.pack()

    override val size: Int
        get() = origin.root?.subtreeSize ?: 0

    private fun resolveImpl(
        location: BinaryTree.Location<PayloadT, ColorT>,
    ): ProperNode<PayloadT, ColorT>? = when (location) {
        BinaryTree.RootLocation -> origin.root

        is BinaryTree.RelativeLocation<PayloadT, ColorT> -> {
            val parent = location.parentHandle.unpack()
            val side = location.side

            parent.getChild(
                side = side,
            )
        }
    }

    override fun resolve(
        location: BinaryTree.Location<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>? = resolveImpl(
        location = location,
    )?.pack()

    /**
     * Get the payload of the node associated with the given [nodeHandle].
     */
    override fun getPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): PayloadT = nodeHandle.unpack().payload

    override fun getSubtreeSize(
        subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): Int = subtreeRootHandle.unpack().subtreeSizeOrZero

    override fun getColor(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): ColorT = nodeHandle.unpack().color

    /**
     * Get the handle to the parent of the node associated with the given [nodeHandle].
     */
    override fun getParent(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>? {
        val node = nodeHandle.unpack()
        return node.properParent?.pack()
    }

    override fun setPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        payload: PayloadT,
    ) {
        val node = nodeHandle.unpack()

        node.setPayload(
            payload = payload,
        )
    }

    override fun setColor(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        newColor: ColorT,
    ) {
        val node = nodeHandle.unpack()

        node.setColor(
            color = newColor,
        )
    }
}

private val <PayloadT, ColorT> ProperNode<PayloadT, ColorT>?.subtreeSizeOrZero: Int
    get() = this?.subtreeSize ?: 0

private val <PayloadT, ColorT> MutableUnbalancedBinaryTreeImpl.ParentNode<PayloadT, ColorT>.asProper: ProperNode<PayloadT, ColorT>?
    get() = this as? ProperNode<PayloadT, ColorT>

private fun <PayloadT, ColorT> BinaryTree.NodeHandle<PayloadT, ColorT>.unpack(): ProperNode<PayloadT, ColorT> {
    @Suppress("UNCHECKED_CAST") val handleImpl =
        this as? HandleImpl<PayloadT, ColorT> ?: throw IllegalArgumentException("Unrelated handle type")

    val properNode = handleImpl.resolve()

    return properNode
}

private fun <PayloadT, ColorT> ProperNode<PayloadT, ColorT>.pack(): BinaryTree.NodeHandle<PayloadT, ColorT> =
    HandleImpl(
        properNode = this,
    )
