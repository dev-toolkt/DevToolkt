package dev.toolkt.core.data_structures.binary_tree.explicit

import dev.toolkt.core.data_structures.binary_tree.explicit.ExplicitBinaryTreeImpl.ProperNodeImpl
import dev.toolkt.core.data_structures.binary_tree.explicit.ExplicitBinaryTreeImpl.ProperNodeImpl.Companion.link
import dev.toolkt.core.data_structures.binary_tree.explicit.ExplicitMutableBinaryTree.MutableProperNode
import kotlin.jvm.JvmInline

@JvmInline
internal value class ExplicitBinaryTreeImpl<DataT> private constructor(
    override val origin: OriginNodeImpl<DataT>,
) : ExplicitMutableUnbalancedBinaryTree<DataT> {
    sealed interface NodeImpl<PayloadT> : ExplicitMutableBinaryTree.MutableNode<PayloadT> {
        override fun buildLink(
            child: ExplicitBinaryTree.ProperNode<PayloadT>,
        ): LinkImpl<PayloadT>


        fun updateSubtreeSizeRecursively(
            /**
             * The number of gained descendants. If negative, it means the node
             * lost descendants.
             */
            delta: Int,
        )
    }

    class OriginNodeImpl<PayloadT>(
        private var mutableRoot: ProperNodeImpl<PayloadT>? = null,
    ) : ExplicitMutableBinaryTree.MutableOriginNode<PayloadT>, NodeImpl<PayloadT> {
        override val root: ProperNodeImpl<PayloadT>?
            get() = mutableRoot

        fun setRoot(
            newRoot: ProperNodeImpl<PayloadT>?,
        ) {
            mutableRoot = newRoot
        }

        override fun buildLink(
            child: ExplicitBinaryTree.ProperNode<PayloadT>,
        ): LinkImpl<PayloadT> {
            if (child != root) {
                throw IllegalArgumentException("Child node must be the root of the tree")
            }

            return RootLinkImpl(
                origin = this,
            )
        }

        override fun updateSubtreeSizeRecursively(
            delta: Int,
        ) {
        }
    }

    class ProperNodeImpl<PayloadT>(
        private var mutableParent: NodeImpl<PayloadT>,
        private var mutableLeftChild: ProperNodeImpl<PayloadT>? = null,
        private var mutableRightChild: ProperNodeImpl<PayloadT>? = null,
        private var mutableSubtreeSize: Int = 1,
        override val payload: PayloadT,
    ) : ExplicitMutableBinaryTree.MutableProperNode<PayloadT>, NodeImpl<PayloadT> {
        data class IntegrityVerificationResult(
            val computedSubtreeSize: Int,
        )

        companion object {
            fun <T> link(
                parent: ProperNodeImpl<T>,
                side: ExplicitBinaryTree.Side,
                child: ProperNodeImpl<T>?,
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

        override fun buildLink(
            child: ExplicitBinaryTree.ProperNode<PayloadT>,
        ): ProperLinkImpl<PayloadT> = ProperLinkImpl(
            parent = this,
            side = getChildSide(child = child),
        )

        override val parent: NodeImpl<PayloadT>
            get() = mutableParent

        override val leftChild: ProperNodeImpl<PayloadT>?
            get() = mutableLeftChild

        override val rightChild: ProperNodeImpl<PayloadT>?
            get() = mutableRightChild

        override val subtreeSize: Int
            get() = mutableSubtreeSize

        fun setChild(
            child: ProperNodeImpl<PayloadT>?,
            side: ExplicitBinaryTree.Side,
        ) {
            when (side) {
                ExplicitBinaryTree.Side.Left -> mutableLeftChild = child
                ExplicitBinaryTree.Side.Right -> mutableRightChild = child
            }
        }

        fun setParent(
            parent: NodeImpl<PayloadT>,
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
            expectedParent: ExplicitBinaryTree.Node<PayloadT>,
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

        override fun updateSubtreeSizeRecursively(
            /**
             * The number of gained descendants. If negative, it means the node
             * lost descendants.
             */
            delta: Int,
        ) {
            setSubtreeSize(subtreeSize + delta)

            parent.updateSubtreeSizeRecursively(
                delta = delta,
            )
        }
    }

    class RootLinkImpl<T>(
        override val origin: OriginNodeImpl<T>,
    ) : ExplicitMutableBinaryTree.MutableRootLink<T>(), LinkImpl<T> {
        override val parent: OriginNodeImpl<T>
            get() = origin

        override fun insert(
            payload: T,
        ): ExplicitMutableBinaryTree.MutableNode<T> {

            val node = ProperNodeImpl(
                mutableParent = origin,
                payload = payload,
            )

            origin.setRoot(node)

            return node
        }

        override fun linkChild(
            newChild: ProperNodeImpl<T>?,
        ) {
            origin.setRoot(
                newRoot = newChild
            )

            newChild?.setParent(
                parent = origin,
            )
        }
    }

    sealed interface LinkImpl<T> : ExplicitMutableBinaryTree.MutableLink<T> {
        fun linkChild(
            newChild: ProperNodeImpl<T>?,
        )
    }

    class ProperLinkImpl<T>(
        override val parent: ProperNodeImpl<T>,
        override val side: ExplicitBinaryTree.Side,
    ) : ExplicitMutableBinaryTree.MutableProperLink<T>(), LinkImpl<T> {
        override fun linkChild(
            newChild: ProperNodeImpl<T>?,
        ) {
            link(
                parent = parent,
                child = newChild,
                side = side,
            )
        }

        override fun insert(
            payload: T,
        ): ExplicitMutableBinaryTree.MutableNode<T> {
            val existingChild = parent.getChild(
                side = side,
            )

            if (existingChild != null) {
                throw IllegalStateException("Cannot insert a new node at the location occupied by an existing child")
            }

            val node = ProperNodeImpl(
                mutableParent = parent,
                payload = payload,
            )

            parent.setChild(
                child = node,
                side = side,
            )

            return node
        }
    }

    constructor() : this(
        origin = OriginNodeImpl(),
    )

    override fun rotate(
        pivot: ExplicitMutableBinaryTree.MutableNode<DataT>,
        direction: ExplicitBinaryTree.RotationDirection
    ): ExplicitMutableBinaryTree.MutableProperNode<DataT> {
        val pivotNode = pivot as ProperNodeImpl<DataT>

        val parentLink = pivot.parentLink

        val ascendingChild = pivotNode.getChild(side = direction.startSide)
            ?: throw IllegalStateException("The pivot node has no child on the ${direction.startSide} side")

        val closeGrandchild = ascendingChild.getChild(side = direction.endSide)

        val distantGrandchild = ascendingChild.getChild(side = direction.startSide)

        link(
            parent = pivotNode,
            child = closeGrandchild,
            side = direction.startSide,
        )

        link(
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

        return ascendingChild
    }

    override fun collapse(node: ExplicitMutableBinaryTree.MutableNode<DataT>) {
        node as? ProperNodeImpl<DataT>
            ?: throw IllegalArgumentException("Unexpected node type")

        if (node.leftChild != null && node.rightChild != null) {
            throw IllegalStateException("Cannot remove a node with two children")
        }

        // Single child, or null if this is a leaf
        val singleChild = node.leftChild ?: node.rightChild

        val parentLink = node.parentLink

        parentLink.linkChild(singleChild)
    }

    override fun collapseDirectly(node: ExplicitMutableBinaryTree.MutableNode<DataT>) {
        TODO("Not yet implemented")
    }

    override fun removeLeafDirectly(leaf: ExplicitMutableBinaryTree.MutableNode<DataT>): ExplicitMutableBinaryTree.MutableLink<DataT> {
        TODO("Not yet implemented")
    }

    override fun removeLeaf(leaf: ExplicitMutableBinaryTree.MutableNode<DataT>) {
        TODO("Not yet implemented")
    }

    override fun remove(node: ExplicitMutableBinaryTree.MutableProperNode<DataT>) {
        TODO("Not yet implemented")
    }

    override fun swap(
        firstNode: MutableProperNode<DataT>,
        secondNode: MutableProperNode<DataT>,
    ) {
        firstNode as? ProperNodeImpl<DataT> ?: throw IllegalArgumentException("Unexpected node type")
        secondNode as? ProperNodeImpl<DataT> ?: throw IllegalArgumentException("Unexpected node type")

        val firstParentLink = firstNode.parentLink
        val firstLeftChild = firstNode.leftChild
        val firstRightChild = firstNode.rightChild
        val firstSubtreeSize = firstNode.subtreeSize

        val secondParentLink = secondNode.parentLink
        val secondLeftChild = secondNode.leftChild
        val secondRightChild = secondNode.rightChild
        val secondSubtreeSize = secondNode.subtreeSize

        firstParentLink.linkChild(secondNode)

        link(
            parent = secondNode,
            child = firstLeftChild,
            side = ExplicitBinaryTree.Side.Left,
        )

        link(
            parent = secondNode,
            child = firstRightChild,
            side = ExplicitBinaryTree.Side.Right,
        )

        secondNode.setSubtreeSize(firstSubtreeSize)

        secondParentLink.linkChild(firstNode)

        link(
            parent = firstNode,
            child = secondLeftChild,
            side = ExplicitBinaryTree.Side.Left,
        )

        link(
            parent = firstNode,
            child = secondRightChild,
            side = ExplicitBinaryTree.Side.Right,
        )

        firstNode.setSubtreeSize(secondSubtreeSize)
    }

    fun verifyIntegrity() {
        origin.root?.verifyIntegrity(expectedParent = origin)
    }
}

private val <PayloadT> ProperNodeImpl<PayloadT>.parentLink: ExplicitBinaryTreeImpl.LinkImpl<PayloadT>
    get() = this.parent.buildLink(child = this)

private fun <PayloadT> ProperNodeImpl<PayloadT>.getChild(
    side: ExplicitBinaryTree.Side,
): ProperNodeImpl<PayloadT>? = when (side) {
    ExplicitBinaryTree.Side.Left -> leftChild
    ExplicitBinaryTree.Side.Right -> rightChild
}
