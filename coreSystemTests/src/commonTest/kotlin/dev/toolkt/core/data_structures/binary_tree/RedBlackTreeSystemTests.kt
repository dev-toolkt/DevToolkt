package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.test_utils.verifyIntegrityRedBlack
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Ignore
import kotlin.test.Test

class RedBlackTreeSystemTests {
    private val operationCount = 10000

    @Test
    @Ignore
    fun testFuzzy() {
        val random = Random

        val tree = RedBlackTree<Int>()
        val nodeHandles = mutableSetOf<BinaryTree.NodeHandle<Int, RedBlackTree.Color>>()

        (0 until operationCount).forEach { operationIndex ->
            val progress = operationIndex.toDouble() / operationCount

            val removeChance = when {
                progress < 0.5 -> 0.25
                else -> 0.75
            }

            val shouldRemove = random.nextBool(chance = removeChance)

            if (shouldRemove && !tree.isEmpty()) {
                val nodeHandle = nodeHandles.getRandom(random = random)

                tree.remove(
                    nodeHandle = nodeHandle,
                )
            } else {
                val freeLocations = tree.findFreeLocations()
                val location = freeLocations.getRandom(random = random)

                val insertedNodeHandle = tree.insert(
                    location = location,
                    payload = random.nextInt()
                )

                nodeHandles.add(insertedNodeHandle)
            }

            tree.verifyIntegrityRedBlack()
        }
    }
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.findAllLocations(): List<BinaryTree.Location<PayloadT, ColorT>> =
    listOf(BinaryTree.RootLocation) + traverse().flatMap {
        listOf(
            it.getLeftChildLocation(),
            it.getRightChildLocation(),
        )
    }.toList()

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.findFreeLocations(): List<BinaryTree.Location<PayloadT, ColorT>> =
    findAllLocations().filter { resolve(it) == null }

fun <E> List<E>.getRandom(random: Random): E {
    val index = random.nextInt(indices)
    return this[index]
}

fun <E> Collection<E>.getRandom(random: Random): E = toList().getRandom(random = random)

/**
 * @param chance The chance of returning true, between 0.0 and 1.0.
 */
fun Random.nextBool(chance: Double): Boolean {
    require(chance in 0.0..1.0) {
        "Chance must be between 0.0 and 1.0, but was $chance"
    }

    return nextDouble() < chance
}
