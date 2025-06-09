package dev.toolkt.core.data_structures.binary_tree

class MutableOrderStatisticTree<DataT>(
    orderedTree: RawOrderStatisticTree<DataT>,
) : EnhancedUnbalancedBinaryTree<DataT, Int>(
    enhancedTree = orderedTree,
), OrderStatisticTree<DataT> {
    class Prototype(
        private val innerPrototype: UnbalancedBinaryTree.Prototype = BasicBinaryTree,
    ) : UnbalancedBinaryTree.Prototype {
        override fun <PayloadT> create(): UnbalancedBinaryTree<PayloadT> = create(
            innerPrototype = innerPrototype,
        )
    }

    companion object {
        fun <DataT> create(
            innerPrototype: UnbalancedBinaryTree.Prototype = BasicBinaryTree,
        ): MutableOrderStatisticTree<DataT> = MutableOrderStatisticTree(
            innerPrototype.create(),
        )
    }

    override fun repairEnhancementAfterLeafInsertion(
        rawInsertedNodeHandle: RawOrderStatisticNodeHandle<DataT>,
    ) {
        incrementAncestorSizes(
            descendantHandle = rawInsertedNodeHandle,
        )
    }

    override fun repairEnhancementAfterLeafRemoval(
        rawRemovedLocation: RawOrderStatisticLocation<DataT>,
    ) {
        val parentHandle = rawRemovedLocation.parentHandle ?: return

        decrementAncestorSizes(
            descendantHandle = parentHandle,
        )
    }

    override fun repairEnhancementAfterElevation(
        elevatedNodeHandle: RawOrderStatisticNodeHandle<DataT>,
    ) {
        decrementAncestorSizes(
            descendantHandle = elevatedNodeHandle,
        )
    }

    override fun repairEnhancementAfterRotation() {
        TODO("Not yet implemented")
    }

    private fun incrementAncestorSizes(
        descendantHandle: RawOrderStatisticNodeHandle<DataT>,
    ) {
        updateAncestorSizes(
            descendantHandle = descendantHandle,
            delta = +1,
        )
    }

    private fun decrementAncestorSizes(
        descendantHandle: RawOrderStatisticNodeHandle<DataT>,
    ) {
        updateAncestorSizes(
            descendantHandle = descendantHandle,
            delta = -1,
        )
    }

    private fun updateAncestorSizes(
        descendantHandle: RawOrderStatisticNodeHandle<DataT>,
        delta: Int,
    ) {
        subjectTree.getAncestors(
            nodeHandle = descendantHandle,
        ).forEach { ancestorHandle ->
            ancestorHandle.setSize(
                newSize = ancestorHandle.getSize() + delta,
            )
        }
    }

    override val defaultEnhancement: Int
        get() = 0

    private fun RawOrderStatisticNodeHandle<DataT>.getSize() = getEnhancementRaw()

    private fun RawOrderStatisticNodeHandle<DataT>.setSize(
        newSize: Int,
    ) {
        setEnhancementRaw(
            newEnhancement = newSize,
        )
    }

    override fun select(
        index: Int,
    ): BinaryTree.NodeHandle<DataT>? {
        if (index < 0) return null

        val root = subjectTree.root ?: return null

        return selectRecursively(
            nodeHandle = root,
            index = index,
        )?.cast()
    }

    private tailrec fun selectRecursively(
        nodeHandle: RawOrderStatisticNodeHandle<DataT>,
        index: Int,
    ): RawOrderStatisticNodeHandle<DataT>? {
        val leftChild = subjectTree.getLeftChild(nodeHandle)
        val rightChild = subjectTree.getRightChild(nodeHandle)

        val leftSize = leftChild.getSizeOrZero()

        return when {
            index < leftSize -> selectRecursively(
                nodeHandle = leftChild ?: throw AssertionError("No left child"),
                index = index,
            )

            index == leftSize -> return nodeHandle

            else -> selectRecursively(
                index = index - leftSize - 1,
                nodeHandle = rightChild ?: throw AssertionError("No right child"),
            )
        }
    }

    private fun RawOrderStatisticNodeHandle<DataT>?.getSizeOrZero(): Int = this?.getSize() ?: 0

    override fun getRank(
        nodeHandle: BinaryTree.NodeHandle<DataT>,
    ): Int {
        val rawNodeHandle: RawOrderStatisticNodeHandle<DataT> = nodeHandle.cast()

        val leftChild = subjectTree.getLeftChild(rawNodeHandle)

        val rank = getRankRecursively(
            nodeHandle = rawNodeHandle,
            subtreeRank = leftChild.getSizeOrZero(),
        )

        return rank
    }

    /**
     * Return the rank of the node associated with [nodeHandle] in its parent's
     * subtree
     */
    private tailrec fun getRankRecursively(
        /**
         * Handle to the node whose rank we want to determine
         */
        nodeHandle: RawOrderStatisticNodeHandle<DataT>,
        /**
         * The rank of the node in its own subtree
         */
        subtreeRank: Int,
    ): Int {
        val relativeLocation = subjectTree.locateRelatively(
            nodeHandle = nodeHandle,
        ) ?: return subtreeRank

        val parentHandle = relativeLocation.parentHandle

        val delta = when (relativeLocation.side) {
            BinaryTree.Side.Left -> 0
            BinaryTree.Side.Right -> relativeLocation.getSibling(subjectTree).getSizeOrZero() + 1
        }

        return getRankRecursively(
            nodeHandle = parentHandle,
            subtreeRank = subtreeRank + delta,
        )
    }

    fun verifyIntegrity() {
        verifyIntegrity(nodeHandle = subjectTree.root)
    }

    private fun verifyIntegrity(
        nodeHandle: RawOrderStatisticNodeHandle<DataT>?,
    ) {
        if (nodeHandle == null) {
            return
        }

        val cachedSize = nodeHandle.getSize()
        val calculatedSize = subjectTree.getSubtreeSize(subtreeRootHandle = nodeHandle)

        if (cachedSize != calculatedSize) {
            throw AssertionError(
                "Node handle $nodeHandle has cached size $cachedSize, but calculated size is $calculatedSize"
            )
        }

        val leftChild = subjectTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChild = subjectTree.getRightChild(nodeHandle = nodeHandle)

        verifyIntegrity(nodeHandle = leftChild)
        verifyIntegrity(nodeHandle = rightChild)
    }
}

private typealias RawOrderStatisticEnhancement = Int

private typealias RawOrderStatisticTree<DataT> = RawEnhancedTree<DataT, RawOrderStatisticEnhancement>

private typealias RawOrderStatisticNodeHandle<DataT> = RawEnhancedNodeHandle<DataT, RawOrderStatisticEnhancement>

private typealias RawOrderStatisticLocation<DataT> = RawEnhancedLocation<DataT, RawOrderStatisticEnhancement>
