package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.test_utils.verify
import kotlin.random.Random
import kotlin.test.Test

class RedBlackTreeSystemTests {
    private val operationCount = 20_000

    @Test
    fun testFuzzy() {
        val random = Random

        val tree = RedBlackTree<Int>()
        val nodeHandles = ArrayDeque<BinaryTree.NodeHandle<Int, RedBlackTree.Color>>()

        (0 until operationCount).forEach { operationIndex ->
            val progress = operationIndex.toDouble() / operationCount

            val removeChance = when {
                progress < 0.5 -> 0.25
                else -> 0.75
            }

            val shouldRemove = random.nextBool(chance = removeChance)

            if (shouldRemove && !tree.isEmpty()) {
                val nodeHandle = nodeHandles.removeFirst()

                tree.remove(
                    nodeHandle = nodeHandle,
                )
            } else {
                val location = tree.getRandomFreeLocation(random = random)

                val payload = random.nextInt()

                val insertedNodeHandle = tree.insert(
                    location = location,
                    payload = payload,
                )

                nodeHandles.addLast(insertedNodeHandle)
            }

            tree.verify()
        }
    }
}

/**
 * @param chance The chance of returning true, between 0.0 and 1.0.
 */
fun Random.nextBool(chance: Double): Boolean {
    require(chance in 0.0..1.0) {
        "Chance must be between 0.0 and 1.0, but was $chance"
    }

    return nextDouble() < chance
}
