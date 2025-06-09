package dev.toolkt.core.data_structures.binary_tree

abstract class BalancedBinaryTree<DataT, BalanceMetadataT>(
    /**
     * The tree that is being balanced
     */
    subjectTree: RawEnhancedTree<DataT, BalanceMetadataT>,
) : EnhancedBinaryTree<DataT, BalanceMetadataT>(
    subjectTree = subjectTree,
) {
    final override fun repairEnhancementAfterLeafInsertion(
        rawInsertedNodeHandle: RawEnhancedNodeHandle<DataT, BalanceMetadataT>
    ) {
        restoreBalanceAfterLeafInsertion(
            insertedNodeHandle = rawInsertedNodeHandle,
        )
    }

    final override fun repairEnhancementAfterLeafRemoval(
        rawRemovedLocation: RawEnhancedLocation<DataT, BalanceMetadataT>
    ) {
        restoreBalanceAfterLeafRemoval(
            rawLocation = rawRemovedLocation,
        )
    }

    final override fun repairEnhancementAfterElevation(
        elevatedNodeHandle: RawEnhancedNodeHandle<DataT, BalanceMetadataT>
    ) {
        restoreBalanceAfterElevation(
            elevatedNodeHandle = elevatedNodeHandle,
        )
    }

    final override val defaultEnhancement: BalanceMetadataT
        get() = defaultBalanceMetadata

    protected abstract val defaultBalanceMetadata: BalanceMetadataT

    protected abstract fun restoreBalanceAfterLeafInsertion(
        insertedNodeHandle: RawEnhancedNodeHandle<DataT, BalanceMetadataT>,
    )

    protected abstract fun restoreBalanceAfterLeafRemoval(
        rawLocation: RawEnhancedLocation<DataT, BalanceMetadataT>,
    )

    protected abstract fun restoreBalanceAfterElevation(
        elevatedNodeHandle: RawEnhancedNodeHandle<DataT, BalanceMetadataT>,
    )
}
