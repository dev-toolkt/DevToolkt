package dev.toolkt.core.data_structures.binary_tree

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNull

class MutableOrderStatisticTreeTests {
    @Test
    fun testInitial() {
        val tree = MutableOrderStatisticTree.create<Int>()

        assertNull(
            actual = tree.select(0),
        )
    }

    @Test
    fun testInsert_root() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        assertEquals(
            expected = handle100,
            actual = tree.select(0),
        )

        assertEquals(
            expected = 0,
            actual = tree.getRank(handle100),
        )
    }

    @Test
    fun testInsert_ordinaryLeaf() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle90 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )

        val handle110 = tree.insertVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
        )

        val handle105 = tree.insertVerified(
            location = handle110.getLeftChildLocation(),
            payload = 115,
        )

        assertOrder(
            expectedOrder = listOf(
                handle90,
                handle100,
                handle105,
                handle110,
            ),
            actualTree = tree,
        )
    }

    @Test
    fun testRemoveLeaf_root() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        tree.removeLeafVerified(leafHandle = handle100)

        assertNull(
            actual = tree.select(0),
        )
    }

    @Test
    fun testRemoveLeaf_ordinaryLeaf() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle90 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )

        val handle110 = tree.insertVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
        )

        tree.removeLeafVerified(leafHandle = handle110)

        assertOrder(
            expectedOrder = listOf(
                handle90,
                handle100,
            ),
            actualTree = tree,
        )
    }

    @Test
    fun testRemoveLeaf_nonLeaf() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )

        val handle110 = tree.insertVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
        )

        tree.insertVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
        )

        tree.insertVerified(
            location = handle110.getRightChildLocation(),
            payload = 120,
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                tree.removeLeaf(leafHandle = handle110)
            },
        )
    }

    @Test
    fun testElevate_rootChild() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle90 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )

        tree.elevate(nodeHandle = handle90)

        assertOrder(
            expectedOrder = listOf(
                handle90,
            ),
            actualTree = tree,
        )
    }

    @Test
    fun testElevate_ordinarySingleChild() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle90 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )

        val handle110 = tree.insertVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
        )

        val handle105 = tree.insertVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
        )

        tree.elevate(nodeHandle = handle105)

        assertOrder(
            expectedOrder = listOf(
                handle90,
                handle100,
                handle105,
            ),
            actualTree = tree,
        )
    }

    @Test
    fun testSwap_ordinaryNodes() {
        val tree = MutableOrderStatisticTree.create<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle90 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )

        val handle110 = tree.insertVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
        )

        val handle105 = tree.insertVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
        )

        val handle115 = tree.insertVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
        )

        assertEquals(
            expected = DumpedNode(
                payload = 100,
                leftChild = DumpedNode(
                    payload = 90,
                ),
                rightChild = DumpedNode(
                    payload = 110,
                    leftChild = DumpedNode(
                        payload = 105,
                    ),
                    rightChild = DumpedNode(
                        payload = 115,
                    ),
                ),
            ),
            actual = tree.dump(),
        )

        assertOrder(
            expectedOrder = listOf(
                handle90,
                handle100,
                handle105,
                handle110,
                handle115,
            ),
            actualTree = tree,
        )

        tree.swapVerified(handle90, handle110)

        assertEquals(
            expected = DumpedNode(
                payload = 100,
                leftChild = DumpedNode(
                    payload = 110,
                ),
                rightChild = DumpedNode(
                    payload = 90,
                    leftChild = DumpedNode(
                        payload = 105,
                    ),
                    rightChild = DumpedNode(
                        payload = 115,
                    ),
                ),
            ),
            actual = tree.dump(),
        )

        assertOrder(
            expectedOrder = listOf(
                handle110,
                handle100,
                handle105,
                handle90,
                handle115,
            ),
            actualTree = tree,
        )
    }

    @Test
    fun testRotate() {
        val tree = BasicBinaryTree<Int>()

        // TODO
    }
}
