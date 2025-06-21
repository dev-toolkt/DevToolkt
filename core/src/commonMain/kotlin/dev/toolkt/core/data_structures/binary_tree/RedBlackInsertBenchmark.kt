package dev.toolkt.core.data_structures.binary_tree

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlin.random.Random

@State(Scope.Benchmark)
class RedBlackTreeInsertBenchmark {
    companion object {
        private const val treeMinSize = 10_000_000
    }

    private val tree = RedBlackTree<Int>()

    private val random = Random(0)

    @Setup
    fun prepareTree() {
        repeat(treeMinSize) {
            val location = tree.getRandomFreeLocation(random = random)

            tree.insert(
                location = location,
                payload = random.nextInt(),
            )
        }
    }

    @Benchmark
    fun benchmarkInsert() {
        val location = tree.getRandomFreeLocation(random = random)

        tree.insert(
            location = location,
            payload = random.nextInt(),
        )
    }
}
