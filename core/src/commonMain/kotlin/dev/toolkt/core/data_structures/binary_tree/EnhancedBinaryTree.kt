package dev.toolkt.core.data_structures.binary_tree

import kotlin.jvm.JvmInline

abstract class EnhancedBinaryTree<DataT, EnhancementT>(
    /**
     * The tree that is being enhanced
     */
    protected val subjectTree: RawEnhancedTree<DataT, EnhancementT>,
) : MutableBinaryTree<DataT> {
    data class EnhancedPayload<DataT, EnhancementT>(
        val data: DataT,
        private var mutableEnhancement: EnhancementT,
    ) {
        val enhancement: EnhancementT
            get() = mutableEnhancement

        fun setEnhancement(
            newEnhancement: EnhancementT,
        ) {
            mutableEnhancement = newEnhancement
        }
    }

    @JvmInline
    value class EnhancedNodeHandle<DataT, EnhancementT>(
        val rawHandle: RawEnhancedNodeHandle<DataT, EnhancementT>,
    ) : BinaryTree.NodeHandle<DataT>

    override val root: BinaryTree.NodeHandle<DataT>?
        get() = subjectTree.root?.cast()

    final override fun getPayload(
        nodeHandle: BinaryTree.NodeHandle<DataT>,
    ): DataT = subjectTree.getPayload(
        nodeHandle = nodeHandle.cast(),
    ).data

    final override fun getParent(
        nodeHandle: BinaryTree.NodeHandle<DataT>,
    ): BinaryTree.NodeHandle<DataT>? = subjectTree.getParent(
        nodeHandle = nodeHandle.cast(),
    )?.cast()

    override fun resolve(
        location: BinaryTree.Location<DataT>,
    ): BinaryTree.NodeHandle<DataT>? = subjectTree.resolve(
        location = location.cast(),
    )?.cast()

    final override fun insert(
        location: BinaryTree.Location<DataT>,
        payload: DataT,
    ): BinaryTree.NodeHandle<DataT> {
        val rawInsertedNodeHandle = subjectTree.insert(
            location = location.cast(),
            payload = EnhancedPayload(
                data = payload,
                mutableEnhancement = defaultEnhancement,
            ),
        )

        repairEnhancementAfterLeafInsertion(
            rawInsertedNodeHandle = rawInsertedNodeHandle,
        )

        return rawInsertedNodeHandle.cast()
    }

    final override fun removeLeaf(
        leafHandle: BinaryTree.NodeHandle<DataT>,
    ) {
        val rawRemovedLocation = subjectTree.removeLeafLocated(
            leafHandle = leafHandle.cast(),
        )

        repairEnhancementAfterLeafRemoval(
            rawRemovedLocation = rawRemovedLocation,
        )
    }

    final override fun elevate(
        nodeHandle: BinaryTree.NodeHandle<DataT>,
    ) {
        subjectTree.elevate(
            nodeHandle = nodeHandle.cast(),
        )

        repairEnhancementAfterElevation(
            elevatedNodeHandle = nodeHandle.cast(),
        )
    }

    final override fun swap(
        firstNodeHandle: BinaryTree.NodeHandle<DataT>,
        secondNodeHandle: BinaryTree.NodeHandle<DataT>,
    ) {
        val firstPayload = subjectTree.getPayload(firstNodeHandle.cast())
        val firstEnhancement = firstPayload.enhancement

        val secondPayload = subjectTree.getPayload(secondNodeHandle.cast())
        val secondEnhancement = secondPayload.enhancement

        subjectTree.swap(
            firstNodeHandle.cast(),
            secondNodeHandle.cast(),
        )

        // We "un-swap" the enhancements
        firstPayload.setEnhancement(secondEnhancement)
        secondPayload.setEnhancement(firstEnhancement)
    }

    protected fun RawEnhancedNodeHandle<DataT, EnhancementT>.getChildRaw(
        side: BinaryTree.Side,
    ): RawEnhancedNodeHandle<DataT, EnhancementT>? = subjectTree.getChild(
        nodeHandle = this,
        side = side,
    )

    /**
     * Get children of this node, starting from the given [referenceSide]. The first
     * child will be the "closer" child, the second one will be the "distant"
     * child.
     */
    protected fun RawEnhancedNodeHandle<DataT, EnhancementT>.getChildrenRaw(
        referenceSide: BinaryTree.Side,
    ): Pair<
            RawEnhancedNodeHandle<DataT, EnhancementT>?,
            RawEnhancedNodeHandle<DataT, EnhancementT>?,
            > {
        val closerChild = getChildRaw(referenceSide)
        val distantChild = getChildRaw(referenceSide.opposite)

        return Pair(closerChild, distantChild)
    }

    protected fun RawEnhancedNodeHandle<DataT, EnhancementT>.locateRelativelyRaw(): RawEnhancedRelativeLocation<DataT, EnhancementT>? =
        subjectTree.locateRelatively(nodeHandle = this)

    protected fun RawEnhancedRelativeLocation<DataT, EnhancementT>.getSiblingRaw(): RawEnhancedNodeHandle<DataT, EnhancementT>? =
        this.getSibling(tree = subjectTree)

    protected fun RawEnhancedNodeHandle<DataT, EnhancementT>.getEnhancementRaw(): EnhancementT =
        subjectTree.getPayload(
            nodeHandle = this,
        ).enhancement

    protected fun RawEnhancedNodeHandle<DataT, EnhancementT>.setEnhancementRaw(
        newEnhancement: EnhancementT,
    ) {
        val payload = subjectTree.getPayload(this)

        payload.setEnhancement(
            newEnhancement = newEnhancement,
        )
    }

    protected fun RawEnhancedNodeHandle<DataT, EnhancementT>.rotateRaw(
        direction: BinaryTree.RotationDirection,
    ): RawEnhancedNodeHandle<DataT, EnhancementT> = subjectTree.rotate(
        pivotNodeHandle = this,
        direction = direction,
    )

    protected abstract fun repairEnhancementAfterLeafInsertion(
        rawInsertedNodeHandle: RawEnhancedNodeHandle<DataT, EnhancementT>,
    )

    protected abstract fun repairEnhancementAfterLeafRemoval(
        rawRemovedLocation: RawEnhancedLocation<DataT, EnhancementT>,
    )

    protected abstract fun repairEnhancementAfterElevation(
        elevatedNodeHandle: RawEnhancedNodeHandle<DataT, EnhancementT>,
    )

    protected abstract val defaultEnhancement: EnhancementT
}

internal typealias RawEnhancedPayload<DataT, EnhancementT> = EnhancedBinaryTree.EnhancedPayload<DataT, EnhancementT>

internal typealias RawEnhancedTree<DataT, EnhancementT> = UnbalancedBinaryTree<RawEnhancedPayload<DataT, EnhancementT>>

internal typealias RawEnhancedNodeHandle<DataT, EnhancementT> = BinaryTree.NodeHandle<RawEnhancedPayload<DataT, EnhancementT>>

internal typealias RawEnhancedLocation<DataT, EnhancementT> = BinaryTree.Location<RawEnhancedPayload<DataT, EnhancementT>>

internal typealias RawEnhancedRelativeLocation<DataT, EnhancementT> = BinaryTree.RelativeLocation<RawEnhancedPayload<DataT, EnhancementT>>
