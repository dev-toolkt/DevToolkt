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
        val payload: PayloadT,
    ) : ParentNode<PayloadT> {
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

        fun verifyIntegrity(
            expectedParent: ParentNode<PayloadT>,
        ) {
            if (parent != expectedParent) {
                throw AssertionError("Inconsistent parent, expected: $expectedParent, actual: ${this.parent}")
            }

            leftChild?.verifyIntegrity(expectedParent = this)
            rightChild?.verifyIntegrity(expectedParent = this)
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

        val pushedUpNode = pivotNode.getChild(side = direction.startSide)
            ?: throw IllegalStateException("The new root has to be a proper node")

        val centralNode = pushedUpNode.getChild(side = direction.endSide)

        val pulledDownNode = pivotNode.getChild(side = direction.endSide)

        BasicNode.link(
            parent = pivotNode,
            child = centralNode,
            side = direction.startSide,
        )

        BasicNode.link(
            parent = pushedUpNode,
            child = pivotNode,
            side = direction.endSide,
        )

        parentLink.replaceChild(
            newChild = pushedUpNode,
        )

        return pushedUpNode.pack()
    }

    override fun insert(
        location: BinaryTree.Location<PayloadT>, payload: PayloadT
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
            }
        }

        return newNode.pack()
    }

    override fun removeLeaf(
        leafHandle: BinaryTree.NodeHandle<PayloadT>,
    ) {
        val node = leafHandle.unpack()

        if (!node.isLeaf()) {
            throw IllegalArgumentException("The given node is not a leaf")
        }

        val parentLink = node.parentLink

        parentLink.clearChild()
    }

    override fun elevate(
        nodeHandle: BinaryTree.NodeHandle<PayloadT>,
    ) {
        val node = nodeHandle.unpack()

        val parentLink = node.properParentLink ?: throw IllegalArgumentException("Cannot elevate the root node")

        if (parentLink.sibling != null) throw IllegalArgumentException("Cannot elevate a node with a sibling")

        val grandparentLink = parentLink.parent.parentLink

        grandparentLink.replaceChild(node)
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

        val secondParentLink = secondNode.parentLink
        val secondLeftChild = secondNode.leftChild
        val secondRightChild = secondNode.rightChild

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

    fun verifyIntegrity() {
        origin.root?.verifyIntegrity(expectedParent = origin)
    }
}

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
