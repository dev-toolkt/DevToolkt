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

    /**
     * Before rotation:
     *
     *                100
     *        50                150
     *   25       75       125        175
     * 10  30   60 80   110  130   160    180
     *
     *
     * After rotation:
     *
     *                100
     *      50                     175
     *   25      75           150        180
     * 10  30   60 80      125  160
     *                  110  130
     */
    @Test
    fun testRotate() {
        val tree = BasicBinaryTree<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        val handle50 = tree.insertVerified(
            location = handle100.getLeftChildLocation(),
            payload = 50,
        )

        val handle150 = tree.insertVerified(
            location = handle100.getRightChildLocation(),
            payload = 150,
        )

        val handle25 = tree.insertVerified(
            location = handle50.getLeftChildLocation(),
            payload = 25,
        )

        val handle75 = tree.insertVerified(
            location = handle50.getRightChildLocation(),
            payload = 75,
        )

        val handle125 = tree.insertVerified(
            location = handle150.getLeftChildLocation(),
            payload = 125,
        )

        val handle175 = tree.insertVerified(
            location = handle150.getRightChildLocation(),
            payload = 175,
        )

        tree.insertVerified(
            location = handle25.getLeftChildLocation(),
            payload = 10,
        )

        tree.insertVerified(
            location = handle25.getRightChildLocation(),
            payload = 30,
        )

        tree.insertVerified(
            location = handle75.getLeftChildLocation(),
            payload = 60,
        )

        tree.insertVerified(
            location = handle75.getRightChildLocation(),
            payload = 80,
        )

        tree.insertVerified(
            location = handle125.getLeftChildLocation(),
            payload = 110,
        )

        tree.insertVerified(
            location = handle125.getRightChildLocation(),
            payload = 130,
        )

        tree.insertVerified(
            location = handle175.getLeftChildLocation(),
            payload = 160,
        )

        tree.insertVerified(
            location = handle175.getRightChildLocation(),
            payload = 180,
        )

        tree.rotateVerified(
            pivotNodeHandle = handle150,
            direction = BinaryTree.RotationDirection.CounterClockwise,
        )

        assertEquals(
            expected = DumpedNode(
                payload = 100,
                leftChild = DumpedNode(
                    payload = 50,
                    leftChild = DumpedNode(
                        payload = 25,
                        leftChild = DumpedNode(
                            payload = 10,
                        ),
                        rightChild = DumpedNode(
                            payload = 30,
                        ),
                    ),
                    rightChild = DumpedNode(
                        payload = 75,
                        leftChild = DumpedNode(
                            payload = 60,
                        ),
                        rightChild = DumpedNode(
                            payload = 80,
                        ),
                    ),
                ),
                rightChild = DumpedNode(
                    payload = 175,
                    leftChild = DumpedNode(
                        payload = 150,
                        leftChild = DumpedNode(
                            payload = 125,
                            leftChild = DumpedNode(
                                payload = 110,
                            ),
                            rightChild = DumpedNode(
                                payload = 130,
                            ),
                        ),
                        rightChild = DumpedNode(
                            payload = 160,
                        ),
                    ),
                    rightChild = DumpedNode(
                        payload = 180,
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }
}
