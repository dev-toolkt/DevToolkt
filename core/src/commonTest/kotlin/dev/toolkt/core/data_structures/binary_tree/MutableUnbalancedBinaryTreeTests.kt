package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.attachVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.cutOffVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.dump
import dev.toolkt.core.data_structures.binary_tree.test_utils.rotateVerified
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNull

class MutableUnbalancedBinaryTreeTests {
    private enum class TestColor {
        Green, Blue, Yellow,
    }

    @Test
    fun testInitial() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        assertNull(
            actual = tree.dump(),
        )
    }

    @Test
    fun testAttach_root() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
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
    fun testAttach_ordinaryLeaf() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        assertEquals(
            expected = handle100,
            actual = tree.getParent(nodeHandle = handle90),
        )

        val handle110 = tree.attachVerified(
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

        val handle100 = tree.attachVerified(
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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        val handle110 = tree.attachVerified(
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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        tree.attachVerified(
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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.attachVerified(
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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle95 = tree.attachVerified(
            location = handle90.getRightChildLocation(),
            payload = 95,
            color = TestColor.Blue,
        )

        assertEquals(
            expected = handle95,
            actual = tree.collapse(nodeHandle = handle90),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 95,
                    color = TestColor.Blue,
                ),
            ),
            actual = tree.dump(),
        )
    }

    @Test
    fun testCollapse_ordinarySingleChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Yellow,
        )

        val handle110 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

       val handle105 =  tree.attachVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        assertEquals(
            expected = handle105,
            actual = tree.collapse(nodeHandle = handle110),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Blue,
                leftChild = NodeData(
                    payload = 90,
                    color = TestColor.Yellow,
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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        val handle105 = tree.attachVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        tree.attachVerified(
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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Yellow,
        )

        tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Green,
        )

        val handle110 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        val handle115 = tree.attachVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
            color = TestColor.Blue,
        )

        tree.swap(handle100, handle115)

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 100,
            actual = tree.getPayload(
                nodeHandle = handle100,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle100,
            ),
        )

        assertEquals(
            expected = 115,
            actual = tree.getPayload(
                nodeHandle = handle115,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle115,
            ),
        )

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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        val handle110 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Yellow,
        )

        tree.attachVerified(
            location = handle110.getLeftChildLocation(),
            payload = 105,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
            color = TestColor.Green,
        )

        tree.swap(handle90, handle110)

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 90,
            actual = tree.getPayload(
                nodeHandle = handle90,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle90,
            ),
        )

        assertEquals(
            expected = 110,
            actual = tree.getPayload(
                nodeHandle = handle110,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle110,
            ),
        )

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

    @Test
    fun testSwap_parentChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle90.getLeftChildLocation(),
            payload = 85,
            color = TestColor.Green,
        )

        val handle95 = tree.attachVerified(
            location = handle90.getRightChildLocation(),
            payload = 95,
            color = TestColor.Yellow,
        )

        tree.swap(handle90, handle95)

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 90,
            actual = tree.getPayload(
                nodeHandle = handle90,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle90,
            ),
        )

        assertEquals(
            expected = 95,
            actual = tree.getPayload(
                nodeHandle = handle95,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle95,
            ),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 95,
                    color = TestColor.Blue,
                    leftChild = NodeData(
                        payload = 85,
                        color = TestColor.Green,
                    ),
                    rightChild = NodeData(
                        payload = 90,
                        color = TestColor.Yellow,
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }
    @Test
    fun testSwap_childParent() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle90 = tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle90.getLeftChildLocation(),
            payload = 85,
            color = TestColor.Green,
        )

        val handle95 = tree.attachVerified(
            location = handle90.getRightChildLocation(),
            payload = 95,
            color = TestColor.Yellow,
        )

        tree.swap(handle95, handle90)

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 90,
            actual = tree.getPayload(
                nodeHandle = handle90,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle90,
            ),
        )

        assertEquals(
            expected = 95,
            actual = tree.getPayload(
                nodeHandle = handle95,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle95,
            ),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 95,
                    color = TestColor.Blue,
                    leftChild = NodeData(
                        payload = 85,
                        color = TestColor.Green,
                    ),
                    rightChild = NodeData(
                        payload = 90,
                        color = TestColor.Yellow,
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

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle50 = tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 50,
            color = TestColor.Blue,
        )

        val handle150 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 150,
            color = TestColor.Yellow,
        )

        val handle25 = tree.attachVerified(
            location = handle50.getLeftChildLocation(),
            payload = 25,

            color = TestColor.Green,
        )

        val handle75 = tree.attachVerified(
            location = handle50.getRightChildLocation(),
            payload = 75,
            color = TestColor.Yellow,
        )

        val handle125 = tree.attachVerified(
            location = handle150.getLeftChildLocation(),
            payload = 125,
            color = TestColor.Blue,
        )

        val handle175 = tree.attachVerified(
            location = handle150.getRightChildLocation(),
            payload = 175,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle25.getLeftChildLocation(),
            payload = 10,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle25.getRightChildLocation(),
            payload = 30,
            color = TestColor.Yellow,
        )

        tree.attachVerified(
            location = handle75.getLeftChildLocation(),
            payload = 60,
            color = TestColor.Yellow,
        )

        tree.attachVerified(
            location = handle75.getRightChildLocation(),
            payload = 80,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle125.getLeftChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle125.getRightChildLocation(),
            payload = 130,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle175.getLeftChildLocation(),
            payload = 160,
            color = TestColor.Yellow,
        )

        tree.attachVerified(
            location = handle175.getRightChildLocation(),
            payload = 180,
            color = TestColor.Green,
        )

        tree.rotateVerified(
            pivotNodeHandle = handle150,
            direction = BinaryTree.RotationDirection.CounterClockwise,
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 50,
                    color = TestColor.Blue,
                    leftChild = NodeData(
                        payload = 25,
                        color = TestColor.Green,
                        leftChild = NodeData(
                            payload = 10,
                            color = TestColor.Green,
                        ),
                        rightChild = NodeData(
                            payload = 30,
                            color = TestColor.Yellow,
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 75,
                        color = TestColor.Yellow,
                        leftChild = NodeData(
                            payload = 60,
                            color = TestColor.Yellow,
                        ),
                        rightChild = NodeData(
                            payload = 80,
                            color = TestColor.Blue,
                        ),
                    ),
                ),
                rightChild = NodeData(
                    payload = 175,
                    color = TestColor.Blue,
                    leftChild = NodeData(
                        payload = 150,
                        color = TestColor.Yellow,
                        leftChild = NodeData(
                            payload = 125,
                            color = TestColor.Blue,
                            leftChild = NodeData(
                                payload = 110,
                                color = TestColor.Green,
                            ),
                            rightChild = NodeData(
                                payload = 130,
                                color = TestColor.Blue,
                            ),
                        ),
                        rightChild = NodeData(
                            payload = 160,
                            color = TestColor.Yellow,
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