package dev.toolkt.core.data_structures.binary_tree

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNull

class BasicBinaryTreeTests {
    @Test
    fun testInitial() {
        val tree = BasicBinaryTree<Int>()

        assertNull(
            actual = tree.dump(),
        )
    }

    @Test
    fun testInsert_root() {
        val tree = BasicBinaryTree<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        assertEquals(
            expected = tree.dump(),
            actual = DumpedNode(
                payload = 100,
            ),
        )

        assertNull(
            actual = tree.getParent(handle100),
        )

        assertEquals(
            expected = BinaryTree.RootLocation,
            actual = tree.locate(nodeHandle = handle100),
        )
    }

    @Test
    fun testInsert_ordinaryLeaf() {
        val tree = BasicBinaryTree<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle90 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )

        assertEquals(
            expected = handle100,
            actual = tree.getParent(nodeHandle = handle90),
        )

        val handle110 = tree.insertVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
        )

        assertEquals(
            expected = handle100,
            actual = tree.getParent(nodeHandle = handle110),
        )

        assertEquals(
            expected = BinaryTree.RelativeLocation(
                parentHandle = handle100,
                side = BinaryTree.Side.Right,
            ),
            actual = tree.locate(nodeHandle = handle110),
        )

        assertEquals(
            expected = tree.dump(),
            actual = DumpedNode(
                payload = 100,
                leftChild = DumpedNode(
                    payload = 90,
                ),
                rightChild = DumpedNode(
                    payload = 110,
                ),
            ),
        )
    }

    @Test
    fun testRemoveLeaf_root() {
        val tree = BasicBinaryTree<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val emptiedLocation = tree.removeLeafVerified(leafHandle = handle100)

        assertEquals(
            expected = tree.dump(),
            actual = null,
        )

        assertEquals(
            expected = BinaryTree.RootLocation,
            actual = emptiedLocation,
        )
    }

    @Test
    fun testRemoveLeaf_ordinaryLeaf() {
        val tree = BasicBinaryTree<Int>()

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

        val emptiedLocation = tree.removeLeafVerified(leafHandle = handle110)

        assertEquals(
            expected = tree.dump(),
            actual = DumpedNode(
                payload = 100,
                leftChild = DumpedNode(
                    payload = 90,
                ),
            ),
        )

        assertEquals(
            expected = BinaryTree.RelativeLocation(
                parentHandle = handle100,
                side = BinaryTree.Side.Right,
            ),
            actual = emptiedLocation,
        )
    }

    @Test
    fun testRemoveLeaf_nonLeaf() {
        val tree = BasicBinaryTree<Int>()

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
    fun testElevate_root() {
        val tree = BasicBinaryTree<Int>()

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

        assertIs<IllegalArgumentException>(
            assertFails {
                tree.elevate(nodeHandle = handle100)
            },
        )
    }

    @Test
    fun testElevate_rootChild() {
        val tree = BasicBinaryTree<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle90 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
        )


        tree.elevate(nodeHandle = handle90)

        assertEquals(
            expected = DumpedNode(
                payload = 90,
            ),
            actual = tree.dump(),
        )
    }

    @Test
    fun testElevate_ordinarySingleChild() {
        val tree = BasicBinaryTree<Int>()

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

        val handle105 = tree.insertVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
        )

        tree.elevate(nodeHandle = handle105)

        assertEquals(
            expected = DumpedNode(
                payload = 100,
                leftChild = DumpedNode(
                    payload = 90,
                ),
                rightChild = DumpedNode(
                    payload = 105,
                ),
            ),
            actual = tree.dump(),
        )
    }

    @Test
    fun testElevate_nodeWithSibling() {
        val tree = BasicBinaryTree<Int>()

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

        val handle105 = tree.insertVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
        )

        tree.insertVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                tree.elevate(nodeHandle = handle105)
            },
        )
    }

    @Test
    fun testSwap_withRoot() {
        val tree = BasicBinaryTree<Int>()

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

        val handle115 = tree.insertVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
        )

        tree.swapVerified(handle100, handle115)

        assertEquals(
            expected = DumpedNode(
                payload = 115,
                leftChild = DumpedNode(
                    payload = 90,
                ),
                rightChild = DumpedNode(
                    payload = 110,
                    leftChild = DumpedNode(
                        payload = 105,
                    ),
                    rightChild = DumpedNode(
                        payload = 100,
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }

    @Test
    fun testSwap_ordinaryNodes() {
        val tree = BasicBinaryTree<Int>()

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

        tree.insertVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
        )

        tree.insertVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
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
    }

    @Test
    fun testRotate() {
        val tree = BasicBinaryTree<Int>()

        // TODO
    }
}
