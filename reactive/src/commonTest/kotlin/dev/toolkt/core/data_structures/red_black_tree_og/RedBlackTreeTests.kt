package dev.toolkt.core.data_structures.red_black_tree_og

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO: Nuke
class RedBlackTreeTests {
    @Test
    fun testInsertRoot() {
        val tree = RedBlackTreeOg<Int>()

        tree.insertExtremal(
            side = RedBlackTreeOg.Side.Right,
            value = 10,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10),
            actual = tree.inOrderTraversal.toList(),
        )
    }

    @Test
    fun testInsertExtremal() {
        val tree = RedBlackTreeOg<Int>()

        tree.insertExtremal(
            value = 10,
            side = RedBlackTreeOg.Side.Right,
        )

        tree.verifyIntegrity()

        tree.insertExtremal(
            value = 20,
            side = RedBlackTreeOg.Side.Right,
        )

        tree.verifyIntegrity()

        tree.insertExtremal(
            value = -10,
            side = RedBlackTreeOg.Side.Left,
        )

        tree.verifyIntegrity()

        tree.insertExtremal(
            value = 30,
            side = RedBlackTreeOg.Side.Right,
        )

        tree.verifyIntegrity()

        tree.insertExtremal(
            value = -20,
            side = RedBlackTreeOg.Side.Left,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(-20, -10, 10, 20, 30),
            actual = tree.inOrderTraversal.toList(),
        )
    }

    @Test
    fun testInsertAdjacent() {
        val tree = RedBlackTreeOg<Int>()

        val handle10 = tree.insertExtremal(
            value = 10,
            side = RedBlackTreeOg.Side.Left,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10),
            actual = tree.inOrderTraversal.toList(),
        )

        val handle20 = tree.insertAdjacent(
            handle = handle10,
            value = 20,
            side = RedBlackTreeOg.Side.Right,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10, 20),
            actual = tree.inOrderTraversal.toList(),
        )

        println("Before insert 30")

        println(tree.dump())

        val handle30 = tree.insertAdjacent(
            handle = handle20,
            value = 30,
            side = RedBlackTreeOg.Side.Right,
        )

        println("After insert 30")

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10, 20, 30),
            actual = tree.inOrderTraversal.toList(),
        )

        @Suppress("UNUSED_VARIABLE") val handle15 = tree.insertAdjacent(
            handle = handle20,
            value = 15,
            side = RedBlackTreeOg.Side.Left,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10, 15, 20, 30),
            actual = tree.inOrderTraversal.toList(),
        )

        @Suppress("UNUSED_VARIABLE") val handle12 = tree.insertAdjacent(
            handle = handle10,
            value = 12,
            side = RedBlackTreeOg.Side.Right,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10, 12, 15, 20, 30),
            actual = tree.inOrderTraversal.toList(),
        )

        @Suppress("UNUSED_VARIABLE") val handle5 = tree.insertAdjacent(
            handle = handle10,
            value = 5,
            side = RedBlackTreeOg.Side.Left,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(5, 10, 12, 15, 20, 30),
            actual = tree.inOrderTraversal.toList(),
        )
    }

    @Test
    @Ignore // FIXME: "The associated node is no loner valid"
    fun testInsertRemove() {
        val tree = RedBlackTreeOg<Int>()

        val handle10 = tree.insertExtremal(
            value = 10,
            side = RedBlackTreeOg.Side.Left,
        )

        val handle20 = tree.insertAdjacent(
            handle = handle10,
            value = 20,
            side = RedBlackTreeOg.Side.Right,
        )

        val handle12 = tree.insertAdjacent(
            handle = handle10,
            value = 12,
            side = RedBlackTreeOg.Side.Right,
        )

        val handle15 = tree.insertAdjacent(
            handle = handle20,
            value = 15,
            side = RedBlackTreeOg.Side.Left,
        )

        val handle30 = tree.insertAdjacent(
            handle = handle20,
            value = 30,
            side = RedBlackTreeOg.Side.Right,
        )

        tree.verifyIntegrity()

        if (tree.inOrderTraversal.toList() != listOf(10, 12, 15, 20, 30)) {
            throw AssertionError("Tree structure is incorrect after insertions")
        }

        tree.remove(
            handle = handle15,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10, 12, 20, 30),
            actual = tree.inOrderTraversal.toList(),
        )

        tree.remove(
            handle = handle12,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10, 20, 30),
            actual = tree.inOrderTraversal.toList(),
        )

        tree.remove(
            handle = handle20,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(10, 30),
            actual = tree.inOrderTraversal.toList(),
        )

        tree.remove(
            handle = handle10,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = listOf(30),
            actual = tree.inOrderTraversal.toList(),
        )

        tree.remove(
            handle = handle30,
        )

        tree.verifyIntegrity()

        assertEquals(
            expected = emptyList(),
            actual = tree.inOrderTraversal.toList(),
        )
    }
}
