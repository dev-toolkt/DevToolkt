package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.cutOffVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.dump
import dev.toolkt.core.data_structures.binary_tree.test_utils.putVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.rotateVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.swapVerified
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNull

enum class TestColor {
    Green, Blue, Yellow,
}

@Ignore
class MutableUnbalancedBinaryTreeTests {
    @Test
    fun testInitial() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        assertNull(
            actual = tree.dump(),
        )
    }

    @Test
    fun testPut_root() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        assertEquals(
            expected = tree.dump(),
            actual = NodeData(
                payload = 100,
                color = TestColor.Green,
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
    fun testPut_ordinaryLeaf() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        assertEquals(
            expected = handle100,
            actual = tree.getParent(nodeHandle = handle90),
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
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
            actual = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 90,
                    color = TestColor.Blue,
                ),
                rightChild = NodeData(
                    payload = 110,
                    color = TestColor.Green,
                ),
            ),
        )
    }

    @Test
    fun testCutOff_root() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val emptiedLocation = tree.cutOffVerified(leafHandle = handle100)

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
    fun testCutOff_ordinaryLeaf() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        val emptiedLocation = tree.cutOffVerified(leafHandle = handle110)

        assertEquals(
            expected = tree.dump(),
            actual = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 90,
                    color = TestColor.Blue,
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
    fun testCutOff_nonLeaf() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Blue,
        )

        tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle110.getRightChildLocation(),
            payload = 120,
            color = TestColor.Green,
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                tree.cutOff(leafHandle = handle110)
            },
        )
    }

    @Test
    fun testCollapse_root() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                tree.collapse(nodeHandle = handle100)
            },
        )
    }

    @Test
    fun testCollapse_rootChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        tree.collapse(nodeHandle = handle90)

        assertEquals(
            expected = NodeData(
                payload = 90,
                color = TestColor.Green,
            ),
            actual = tree.dump(),
        )
    }

    @Test
    fun testCollapse_ordinarySingleChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        val handle105 = tree.putVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        tree.collapse(nodeHandle = handle105)

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 90,
                    color = TestColor.Green,
                ),
                rightChild = NodeData(
                    payload = 105,
                    color = TestColor.Green,
                ),
            ),
            actual = tree.dump(),
        )
    }

    @Test
    fun testCollapse_nodeWithSibling() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        val handle105 = tree.putVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
            color = TestColor.Green,
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                tree.collapse(nodeHandle = handle105)
            },
        )
    }

    @Test
    fun testSwap_withRoot() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Yellow,
        )

        tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        val handle115 = tree.putVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
            color = TestColor.Blue,
        )

        tree.swapVerified(handle100, handle115)

        assertEquals(
            expected = NodeData(
                payload = 115,
                color = TestColor.Yellow,
                leftChild = NodeData(
                    payload = 90,
                    color = TestColor.Green,
                ),
                rightChild = NodeData(
                    payload = 110,
                    color = TestColor.Green,
                    leftChild = NodeData(
                        payload = 105,
                        color = TestColor.Green,
                    ),
                    rightChild = NodeData(
                        payload = 100,
                        color = TestColor.Blue,
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }

    @Test
    fun testSwap_ordinaryNodes() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        val handle110 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Yellow,
        )

        tree.putVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
            color = TestColor.Green,
        )

        tree.swapVerified(handle90, handle110)

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 110,
                    color = TestColor.Blue,
                ),
                rightChild = NodeData(
                    payload = 90,
                    color = TestColor.Yellow,
                    leftChild = NodeData(
                        payload = 105,
                        color = TestColor.Green,
                    ),
                    rightChild = NodeData(
                        payload = 115,
                        color = TestColor.Green,
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
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.putVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle50 = tree.putVerified(
            location = handle100.getLeftChildLocation(),
            payload = 50,
            color = TestColor.Blue,
        )

        val handle150 = tree.putVerified(
            location = handle100.getRightChildLocation(),
            payload = 150,
            color = TestColor.Yellow,
        )

        val handle25 = tree.putVerified(
            location = handle50.getLeftChildLocation(),
            payload = 25,

            color = TestColor.Green,
        )

        val handle75 = tree.putVerified(
            location = handle50.getRightChildLocation(),
            payload = 75,
            color = TestColor.Yellow,
        )

        val handle125 = tree.putVerified(
            location = handle150.getLeftChildLocation(),
            payload = 125,
            color = TestColor.Blue,
        )

        val handle175 = tree.putVerified(
            location = handle150.getRightChildLocation(),
            payload = 175,
            color = TestColor.Blue,
        )

        tree.putVerified(
            location = handle25.getLeftChildLocation(),
            payload = 10,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle25.getRightChildLocation(),
            payload = 30,
            color = TestColor.Yellow,
        )

        tree.putVerified(
            location = handle75.getLeftChildLocation(),
            payload = 60,
            color = TestColor.Yellow,
        )

        tree.putVerified(
            location = handle75.getRightChildLocation(),
            payload = 80,
            color = TestColor.Blue,
        )

        tree.putVerified(
            location = handle125.getLeftChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.putVerified(
            location = handle125.getRightChildLocation(),
            payload = 130,
            color = TestColor.Blue,
        )

        tree.putVerified(
            location = handle175.getLeftChildLocation(),
            payload = 160,
            color = TestColor.Yellow,
        )

        tree.putVerified(
            location = handle175.getRightChildLocation(),
            payload = 180,
            color = TestColor.Green,
        )

        tree.rotateVerified(
            pivotNodeHandle = handle150,
            direction = BinaryTree.RotationDirection.CounterClockwise,
        )

        // FIXME: Preserve colors
        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 50,
                    color = TestColor.Green,
                    leftChild = NodeData(
                        payload = 25,
                        color = TestColor.Green,
                        leftChild = NodeData(
                            payload = 10,
                            color = TestColor.Green,
                        ),
                        rightChild = NodeData(
                            payload = 30,
                            color = TestColor.Green,
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 75,
                        color = TestColor.Green,
                        leftChild = NodeData(
                            payload = 60,
                            color = TestColor.Green,
                        ),
                        rightChild = NodeData(
                            payload = 80,
                            color = TestColor.Green,
                        ),
                    ),
                ),
                rightChild = NodeData(
                    payload = 175,
                    color = TestColor.Green,
                    leftChild = NodeData(
                        payload = 150,
                        color = TestColor.Green,
                        leftChild = NodeData(
                            payload = 125,
                            color = TestColor.Green,
                            leftChild = NodeData(
                                payload = 110,
                                color = TestColor.Green,
                            ),
                            rightChild = NodeData(
                                payload = 130,
                                color = TestColor.Green,
                            ),
                        ),
                        rightChild = NodeData(
                            payload = 160,
                            color = TestColor.Green,
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 180,
                        color = TestColor.Green,
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }
}
