package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.iterable.append
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlin.random.Random

@State(Scope.Benchmark)
class RedBlackTreeRemoveBenchmark {
    companion object {
        private const val treeMaxSize = 10_000_000
    }

    private val tree = RedBlackTree<Int>()

    private val random = Random(0)

    private val handles = mutableListOf<BinaryTree.NodeHandle<Int, RedBlackTree.Color>>()

    private var index = 0

    @Setup
    fun prepareTree() {
        repeat(treeMaxSize) {
            val location = tree.getRandomFreeLocation(random = random)

            val insertedNodeHandle = tree.insert(
                location = location,
                payload = random.nextInt(),
            )

            handles.append(insertedNodeHandle)
        }
    }

    @Benchmark
    fun benchmarkRemove() {
        if (index >= handles.size) {
            throw AssertionError("Out of handles")
        }

        val handle = handles[index++]

        tree.remove(
            nodeHandle = handle,
        )
    }
}
