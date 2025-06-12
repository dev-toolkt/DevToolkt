package dev.toolkt.core.data_structures.binary_tree

import kotlin.jvm.JvmInline

class BasicBinaryTree<PayloadT> internal constructor(
    private val origin: OriginNode<PayloadT> = OriginNode(),
) : UnbalancedBinaryTree<PayloadT> {
    internal sealed interface ParentNode<PayloadT> {
        fun buildParentLink(
            child: BasicNode<PayloadT>,
        ): ParentLink<PayloadT>
    }

    internal class OriginNode<PayloadT>(
        private var mutableRoot: BasicNode<PayloadT>? = null,
    ) : ParentNode<PayloadT> {
        val root: BasicNode<PayloadT>?
            get() = mutableRoot

        fun setRoot(
            newRoot: BasicNode<PayloadT>?,
        ) {
            mutableRoot = newRoot
        }

        override fun buildParentLink(
            child: BasicNode<PayloadT>,
        ): ParentLink<PayloadT> {
            if (child != root) {
                throw IllegalArgumentException("Child node must be the root of the tree")
            }

            return OriginLink(
                origin = this,
            )
        }

    }

    internal class BasicNode<PayloadT>(
        private var mutableParent: ParentNode<PayloadT>,
        private var mutableLeftChild: BasicNode<PayloadT>? = null,
        private var mutableRightChild: BasicNode<PayloadT>? = null,
        private var mutableSubtreeSize: Int = 1,
        val payload: PayloadT,
    ) : ParentNode<PayloadT> {
        data class IntegrityVerificationResult(
            val computedSubtreeSize: Int,
        )

        companion object {
            fun <T> link(
                parent: BasicNode<T>,
                side: BinaryTree.Side,
                child: BasicNode<T>?,
            ) {
                parent.setChild(
                    child = child,
                    side = side,
                )

                child?.setParent(
                    parent = parent,
                )
            }
        }

        override fun buildParentLink(
            child: BasicNode<PayloadT>,
        ): ParentLink<PayloadT> = ProperParentLink(
            parent = this,
            childSide = getChildSide(child = child),
        )

        val parent: ParentNode<PayloadT>
            get() = mutableParent

        val parentLink: ParentLink<PayloadT>
            get() = parent.buildParentLink(
                child = this,
            )

        val properParentLink: ProperParentLink<PayloadT>?
            get() = parentLink as? ProperParentLink<PayloadT>

        val properParent: BasicNode<PayloadT>?
            get() = properParentLink?.parent

        val leftChild: BasicNode<PayloadT>?
            get() = mutableLeftChild

        val rightChild: BasicNode<PayloadT>?
            get() = mutableRightChild


        val singleChildOrNull: BasicNode<PayloadT>?
            get() = when {
                leftChild != null && rightChild == null -> leftChild
                leftChild == null && rightChild != null -> rightChild
                else -> null
            }

        val subtreeSize: Int
            get() = mutableSubtreeSize

        fun isLeaf(): Boolean = leftChild == null && rightChild == null

        fun getChild(
            side: BinaryTree.Side,
        ): BasicNode<PayloadT>? = when (side) {
            BinaryTree.Side.Left -> leftChild
            BinaryTree.Side.Right -> rightChild
        }

        fun getChildSide(
            child: BasicNode<PayloadT>,
        ): BinaryTree.Side = when {
            child === leftChild -> BinaryTree.Side.Left
            child === rightChild -> BinaryTree.Side.Right
            else -> throw IllegalArgumentException("The given node is not a child of this node")
        }

        fun getAncestors(): Sequence<BasicNode<PayloadT>> {
            val parent = this.properParent ?: return emptySequence()

            return generateSequence(
                seed = parent,
            ) { currentNodeHandle ->
                currentNodeHandle.properParent
            }
        }

        fun setChild(
            child: BasicNode<PayloadT>?,
            side: BinaryTree.Side,
        ) {
            when (side) {
                BinaryTree.Side.Left -> mutableLeftChild = child
                BinaryTree.Side.Right -> mutableRightChild = child
            }
        }

        fun setParent(
            parent: ParentNode<PayloadT>,
        ) {
            mutableParent = parent
        }

        fun setSubtreeSize(
            size: Int,
        ) {
            require(size >= 0)

            mutableSubtreeSize = size
        }

        fun verifyIntegrity(
            expectedParent: ParentNode<PayloadT>,
        ): IntegrityVerificationResult {
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
            setSubtreeSize(subtreeSize + delta)

            properParent?.updateSubtreeSizeRecursively(
                delta = delta,
            )
        }
    }

    internal sealed class ParentLink<T> {
        abstract val parent: ParentNode<T>

        abstract val childLocation: BinaryTree.Location<T>

        fun clearChild() {
            linkChild(newChild = null)
        }

        // TODO: Nuke?
        fun replaceChild(
            newChild: BasicNode<T>,
        ) {
            linkChild(newChild)
        }

        abstract fun linkChild(
            newChild: BasicNode<T>?,
        )
    }

    internal class OriginLink<T>(
        private val origin: OriginNode<T>,
    ) : ParentLink<T>() {
        override val parent: ParentNode<T>
            get() = origin

        override val childLocation: BinaryTree.Location<T>
            get() = BinaryTree.RootLocation

        override fun linkChild(
            newChild: BasicNode<T>?,
        ) {
            origin.setRoot(
                newRoot = newChild
            )

            newChild?.setParent(
                parent = origin,
            )
        }
    }

    internal class ProperParentLink<T>(
        override val parent: BasicNode<T>,
        val childSide: BinaryTree.Side,
    ) : ParentLink<T>() {
        val siblingSide: BinaryTree.Side
            get() = childSide.opposite

        val sibling: BasicNode<T>?
            get() = parent.getChild(side = siblingSide)

        override val childLocation: BinaryTree.Location<T>
            get() = BinaryTree.RelativeLocation(
                parentHandle = parent.pack(),
                side = childSide,
            )

        override fun linkChild(
            newChild: BasicNode<T>?,
        ) {
            BasicNode.link(
                parent = parent,
                child = newChild,
                side = childSide,
            )
        }
    }

    @JvmInline
    value class BasicHandle<PayloadT> internal constructor(
        internal val basicNode: BasicNode<PayloadT>,
    ) : BinaryTree.NodeHandle<PayloadT>

    companion object : UnbalancedBinaryTree.Prototype {
        override fun <PayloadT> create(): UnbalancedBinaryTree<PayloadT> = BasicBinaryTree()
    }

    override fun rotate(
        pivotNodeHandle: BinaryTree.NodeHandle<PayloadT>,
        direction: BinaryTree.RotationDirection,
    ): BinaryTree.NodeHandle<PayloadT> {
        val pivotNode = pivotNodeHandle.unpack()

        val parentLink = pivotNode.parentLink

        val ascendingChild = pivotNode.getChild(side = direction.startSide)
            ?: throw IllegalStateException("The pivot node has no child on the ${direction.startSide} side")

        val closeGrandchild = ascendingChild.getChild(side = direction.endSide)

        val distantGrandchild = ascendingChild.getChild(side = direction.startSide)

        BasicNode.link(
            parent = pivotNode,
            child = closeGrandchild,
            side = direction.startSide,
        )

        BasicNode.link(
            parent = ascendingChild,
            child = pivotNode,
            side = direction.endSide,
        )

        parentLink.replaceChild(
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

    override fun insert(
        location: BinaryTree.Location<PayloadT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT> = insertDirectly(
        location = location,
        payload = payload,
    )

    /**
     * Insert a new value at the given free [location] without performing any other
     * tree-reshaping operations.
     *
     * @return A handle to the new inserted node
     * @throws IllegalArgumentException if the location is taken
     */
    fun insertDirectly(
        location: BinaryTree.Location<PayloadT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT> {
        val newNode = BasicNode(
            mutableParent = origin,
            payload = payload,
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

            is BinaryTree.RelativeLocation<PayloadT> -> {
                val parent = location.parentHandle.unpack()
                val side = location.side
                val previousChild = location.resolve()

                if (previousChild != null) {
                    throw IllegalStateException("Cannot insert leaf to a non-empty location")
                }

                BasicNode.link(
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

    override fun removeLeaf(
        leafHandle: BinaryTree.NodeHandle<PayloadT>,
    ) {
        removeLeafDirectly(
            leafHandle = leafHandle,
        )
    }

    /**
     * Remove the leaf without performing any other tree-reshaping operations.
     *
     * @return The location from which the leaf was removed.
     */
    fun removeLeafDirectly(
        leafHandle: BinaryTree.NodeHandle<PayloadT>,
    ): BinaryTree.Location<PayloadT> {
        val node = leafHandle.unpack()

        if (!node.isLeaf()) {
            throw IllegalArgumentException("The given node is not a leaf")
        }

        val parentLink = node.parentLink

        val properParent = parentLink.parent.asProper

        parentLink.clearChild()

        properParent?.updateSubtreeSizeRecursively(
            delta = -1,
        )

        return parentLink.childLocation
    }

    override fun elevate(
        nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    ) {
        elevateDirectly(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Elevate the node corresponding to the given [nodeHandle] without
     * performing any other tree-reshaping operations.
     */
    fun elevateDirectly(
        nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    ) {
        val node = nodeHandle.unpack()

        if (node.properParent == null) {
            throw IllegalArgumentException("Cannot elevate the root node")
        }

        val parentLink = node.properParentLink ?: throw IllegalArgumentException("Cannot elevate the root node")

        if (parentLink.sibling != null) throw IllegalArgumentException("Cannot elevate a node with a sibling")

        val grandparentLink = parentLink.parent.parentLink
        val properGrandparent = grandparentLink.parent.asProper

        grandparentLink.replaceChild(node)

        properGrandparent?.updateSubtreeSizeRecursively(
            delta = -1,
        )
    }

    override fun swap(
        firstNodeHandle: BinaryTree.NodeHandle<PayloadT>,
        secondNodeHandle: BinaryTree.NodeHandle<PayloadT>,
    ) {
        val firstNode = firstNodeHandle.unpack()
        val secondNode = secondNodeHandle.unpack()

        val firstParentLink = firstNode.parentLink
        val firstLeftChild = firstNode.leftChild
        val firstRightChild = firstNode.rightChild
        val firstSubtreeSize = firstNode.subtreeSize

        val secondParentLink = secondNode.parentLink
        val secondLeftChild = secondNode.leftChild
        val secondRightChild = secondNode.rightChild
        val secondSubtreeSize = secondNode.subtreeSize

        firstParentLink.replaceChild(secondNode)

        BasicNode.link(
            parent = secondNode,
            child = firstLeftChild,
            side = BinaryTree.Side.Left,
        )

        BasicNode.link(
            parent = secondNode,
            child = firstRightChild,
            side = BinaryTree.Side.Right,
        )

        secondNode.setSubtreeSize(firstSubtreeSize)

        secondParentLink.replaceChild(firstNode)

        BasicNode.link(
            parent = firstNode,
            child = secondLeftChild,
            side = BinaryTree.Side.Left,
        )

        BasicNode.link(
            parent = firstNode,
            child = secondRightChild,
            side = BinaryTree.Side.Right,
        )

        firstNode.setSubtreeSize(secondSubtreeSize)
    }

    override fun getPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    ): PayloadT {
        val node = nodeHandle.unpack()
        return node.payload
    }

    override fun getParent(
        nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    ): BinaryTree.NodeHandle<PayloadT>? {
        val node = nodeHandle.unpack()
        val parent = node.parent as? BasicNode<PayloadT> ?: return null
        return parent.pack()
    }

    override fun resolve(
        location: BinaryTree.Location<PayloadT>,
    ): BinaryTree.NodeHandle<PayloadT>? = when (location) {
        BinaryTree.RootLocation -> origin.root?.pack()

        is BinaryTree.RelativeLocation<PayloadT> -> location.resolve()?.pack()
    }

    override val root: BinaryTree.NodeHandle<PayloadT>?
        get() {
            val root = this.origin.root ?: return null
            return root.pack()
        }

    val size: Int
        get() = origin.root.subtreeSizeOrZero

    fun verifyIntegrity() {
        origin.root?.verifyIntegrity(expectedParent = origin)
    }
}

internal val <PayloadT> BasicBinaryTree.ParentNode<PayloadT>.asProper: BasicBinaryTree.BasicNode<PayloadT>?
    get() = this as? BasicBinaryTree.BasicNode<PayloadT>

internal val <PayloadT> BasicBinaryTree.BasicNode<PayloadT>?.subtreeSizeOrZero: Int
    get() = this?.subtreeSize ?: 0

private fun <PayloadT> BinaryTree.NodeHandle<PayloadT>.unpack(): BasicBinaryTree.BasicNode<PayloadT> {
    @Suppress("UNCHECKED_CAST") val basicHandle =
        this as? BasicBinaryTree.BasicHandle<PayloadT> ?: throw IllegalArgumentException("Unrelated handle type")

    return basicHandle.basicNode
}

private fun <PayloadT> BasicBinaryTree.BasicNode<PayloadT>.pack(): BinaryTree.NodeHandle<PayloadT> =
    BasicBinaryTree.BasicHandle(
        basicNode = this,
    )

private fun <PayloadT> BinaryTree.RelativeLocation<PayloadT>.resolve(): BasicBinaryTree.BasicNode<@UnsafeVariance PayloadT>? =
    parentHandle.unpack().getChild(side = side)
