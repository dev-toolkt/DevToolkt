package dev.toolkt.core.data_structures.binary_tree

abstract class EnhancedUnbalancedBinaryTree<DataT, EnhancementT>(
    enhancedTree: RawEnhancedTree<DataT, EnhancementT>,
) : EnhancedBinaryTree<DataT, EnhancementT>(
    subjectTree = enhancedTree,
), UnbalancedBinaryTree<DataT> {
    final override fun rotate(
        pivotNodeHandle: BinaryTree.NodeHandle<DataT>,
        direction: BinaryTree.RotationDirection
    ): BinaryTree.NodeHandle<DataT> {
        val rawNewRootHandle = subjectTree.rotate(
            pivotNodeHandle = pivotNodeHandle.cast(),
            direction = direction,
        )

        repairEnhancementAfterRotation()

        return rawNewRootHandle.cast()
    }

    protected abstract fun repairEnhancementAfterRotation()
}
