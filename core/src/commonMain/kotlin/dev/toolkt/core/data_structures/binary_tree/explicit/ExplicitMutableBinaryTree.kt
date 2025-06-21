package dev.toolkt.core.data_structures.binary_tree.explicit

/**
 * A mutable binary tree (either balanced or unbalanced).
 */
interface ExplicitMutableBinaryTree<PayloadT> : ExplicitBinaryTree<PayloadT> {
    sealed interface MutableNode<PayloadT> : ExplicitBinaryTree.Node<PayloadT> {
        override fun buildLink(
            child: ExplicitBinaryTree.ProperNode<@UnsafeVariance PayloadT>,
        ): MutableLink<PayloadT>
    }

    interface MutableProperNode<PayloadT> : MutableNode<PayloadT>, ExplicitBinaryTree.ProperNode<PayloadT> {
        override val parent: MutableNode<PayloadT>

        override val leftChild: MutableProperNode<PayloadT>?

        override val rightChild: MutableProperNode<PayloadT>?
    }

    interface MutableOriginNode<PayloadT> : MutableNode<PayloadT>, ExplicitBinaryTree.OriginNode<PayloadT> {
        override val root: MutableProperNode<PayloadT>?
    }

    sealed interface MutableLink<PayloadT> : ExplicitBinaryTree.Link<PayloadT> {
        override val parent: MutableNode<PayloadT>

        /**
         * Insert a new node at the free location this links points to
         */
        fun insert(
            payload: PayloadT,
        ): MutableNode<PayloadT>
    }

    sealed class MutableRootLink<PayloadT>() : ExplicitBinaryTree.RootLink<PayloadT>(), MutableLink<PayloadT> {
        abstract override val origin: MutableOriginNode<PayloadT>

        override val parent: MutableOriginNode<PayloadT> = origin
    }

    sealed class MutableProperLink<PayloadT>() : ExplicitBinaryTree.ProperLink<PayloadT>(), MutableLink<PayloadT> {
        abstract override val parent: MutableProperNode<PayloadT>

        abstract override val side: ExplicitBinaryTree.Side
    }

    override val origin: MutableOriginNode<PayloadT>

    // FIXME: While the hierarchy and separation of the potentially-balancing and
    //  non-balancing operations might make sense, it appears that things will _not_
    //  work without handles. Mapping can be performed on the binary tree view level,
    //  but not on the node level. It will require an awful hack to cast the
    //  node handles. Repacking will not work (it wasn't safe anyways).

    // NEWS: Maybe mapping is not needed at all?! Expose read-only metadata
    // on the read-only interface, include it in the type
    /**
     * Remove a single-child [node] from the tree by replacing it with its child
     * without otherwise affecting the tree's order.
     *
     * As this operation modifies the tree structure, it may trigger rebalancing.
     *
     * @throws IllegalStateException if this node has zero or two children
     */
    fun collapse(
        node: MutableNode<PayloadT>,
    )

    /**
     * Remove a leaf from the tree without otherwise affecting the tree's order.
     *
     * As this operation modifies the tree structure, it may trigger rebalancing.
     *
     * @throws IllegalStateException if [leaf] is not actually a leaf
     */
    fun removeLeaf(
        leaf: MutableNode<PayloadT>,
    )

    /**
     * Removes a node from the tree without otherwise affecting the tree's order.
     *
     * As this operation modifies the tree structure, it may trigger rebalancing.
     */
    fun remove(
        node: MutableProperNode<PayloadT>,
    )

    /**
     * Swaps two nodes. Will affect the tree's order.
     *
     * Will not trigger the rebalancing.
     */
    fun swap(
        firstNode: ExplicitMutableBinaryTree.MutableProperNode<PayloadT>,
        secondNode: ExplicitMutableBinaryTree.MutableProperNode<PayloadT>,
    )
}

val <PayloadT> ExplicitMutableBinaryTree.MutableProperNode<PayloadT>.parentLink: ExplicitMutableBinaryTree.MutableLink<PayloadT>
    get() = this.parent.buildLink(child = this)

fun <PayloadT> ExplicitMutableBinaryTree.MutableProperNode<PayloadT>.getChild(
    side: ExplicitBinaryTree.Side,
): ExplicitMutableBinaryTree.MutableProperNode<PayloadT>? = when (side) {
    ExplicitBinaryTree.Side.Left -> leftChild
    ExplicitBinaryTree.Side.Right -> rightChild
}

fun <PayloadT> mutableBinaryTree(): ExplicitMutableBinaryTree<PayloadT> = ExplicitBinaryTreeImpl()
