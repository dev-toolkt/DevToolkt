package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.BinaryTree.RotationDirection
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.attachVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.collapseVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.cutOffVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.dump
import dev.toolkt.core.data_structures.binary_tree.test_utils.rotateVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.swapVerified
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNull

class MutableUnbalancedBinaryTreeTests {
    private enum class TestColor {
        Green, Blue, Yellow, Pink,
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
    fun testCutOff_ordinaryLeaf_extremal() {
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
    fun testCutOff_ordinaryLeaf_nonExtremal() {
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

        val handle75 = tree.attachVerified(
            location = handle50.getRightChildLocation(),
            payload = 75,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 110,
            color = TestColor.Blue,
        )

        val emptiedLocation = tree.cutOffVerified(leafHandle = handle75)

        assertEquals(
            expected = tree.dump(),
            actual = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 50,
                    color = TestColor.Blue,
                ),
                rightChild = NodeData(
                    payload = 110,
                    color = TestColor.Blue,
                ),
            ),
        )

        assertEquals(
            expected = BinaryTree.RelativeLocation(
                parentHandle = handle50,
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

        val handle90 = tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle90.getRightChildLocation(),
            payload = 95,
            color = TestColor.Green,
        )

        assertEquals(
            expected = handle90,
            actual = tree.collapseVerified(nodeHandle = handle100),
        )

        assertEquals(
            expected = NodeData(
                payload = 90,
                color = TestColor.Blue,
                rightChild = NodeData(
                    payload = 95,
                    color = TestColor.Green,
                ),
            ),
            actual = tree.dump(),
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
            actual = tree.collapseVerified(nodeHandle = handle90),
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

    /**
     * - Collapsed non-root node having one child (on the right side)
     * - Collapsed node is not an extremal (minimal) value on the child's opposite side (left)
     */
    @Test
    fun testCollapse_ordinarySingleChild_nonExtremal() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Blue,
        )

        val handle200 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 200,
            color = TestColor.Green,
        )

        val handle300 = tree.attachVerified(
            location = handle200.getRightChildLocation(),
            payload = 300,
            color = TestColor.Yellow,
        )

        assertEquals(
            expected = handle300,
            actual = tree.collapseVerified(nodeHandle = handle200),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Blue,
                rightChild = NodeData(
                    payload = 300,
                    color = TestColor.Yellow,
                ),
            ),
            actual = tree.dump(),
        )
    }

    /**
     * - Collapsed non-root node having one child (on the left side)
     * - Collapsed node is an extremal (maximal) value on the child's opposite side (right)
     */
    @Test
    fun testCollapse_ordinarySingleChild_extremal() {
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

        val handle105 = tree.attachVerified(
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
    fun testCollapse_leaf() {
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
    fun testCollapse_twoChildren() {
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

        tree.attachVerified(
            location = handle110.getRightChildLocation(),
            payload = 115,
            color = TestColor.Green,
        )

        assertIs<IllegalArgumentException>(
            assertFails {
                tree.collapse(nodeHandle = handle110)
            },
        )
    }

    /**
     * - The swapped node is root
     * - Primary side: right (neighbour = successor)
     * - The swapped node doesn't have a child on the opposite side (left)
     *   - As it's root, it must be an extremal (minimal) value (link = null)
     * - The neighbour (successor) is separated by more than one node
     * - The neighbour (successor) doesn't have a child on the primary side (right)
     */
    @Test
    fun testSwap_root_noOppositeChild_successor_distant_neighbourNoOppositeChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        // The swapped node (root)
        val handle200 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 200,
            color = TestColor.Blue,
        )

        val handle300 = tree.attachVerified(
            location = handle200.getRightChildLocation(),
            payload = 300,
            color = TestColor.Green,
        )

        val handle260 = tree.attachVerified(
            location = handle300.getLeftChildLocation(),
            payload = 260,
            color = TestColor.Blue,
        )

        // The successor
        val handle240 = tree.attachVerified(
            location = handle260.getLeftChildLocation(),
            payload = 240,
            color = TestColor.Yellow,
        )

        tree.swapVerified(
            nodeHandle = handle200,
            side = BinaryTree.Side.Right,
        )

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 200,
            actual = tree.getPayload(
                nodeHandle = handle200,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle200,
            ),
        )

        assertEquals(
            expected = 240,
            actual = tree.getPayload(
                nodeHandle = handle240,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle240,
            ),
        )

        assertEquals(
            expected = NodeData(
                payload = 240,
                color = TestColor.Blue,
                rightChild = NodeData(
                    payload = 300,
                    color = TestColor.Green,
                    leftChild = NodeData(
                        payload = 260,
                        color = TestColor.Blue,
                        leftChild = NodeData(
                            payload = 200,
                            color = TestColor.Yellow,
                        ),
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }

    /**
     * - The swapped node is non-root
     * - Primary side: left (neighbour = predecessor)
     * - The swapped node doesn't have a child on the opposite side (right)
     *   - It is an extremal (maximal) value (link = null)
     * - The neighbour (predecessor) is separated by one node
     * - The neighbour (predecessor) has a child on the primary side (left)
     */
    @Test
    fun testSwap_nonRoot_noOppositeChild_extremal_predecessor_separated_neighbourHasOppositeChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle100.getLeftChildLocation(),
            payload = 90,
            color = TestColor.Pink,
        )

        // The swapped node
        val handle200 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 200,
            color = TestColor.Yellow,
        )

        val handle150 = tree.attachVerified(
            location = handle200.getLeftChildLocation(),
            payload = 150,
            color = TestColor.Green,
        )

        // The predecessor
        val handle160 = tree.attachVerified(
            location = handle150.getRightChildLocation(),
            payload = 160,
            color = TestColor.Blue,
        )

        // The predecessor's child on the primary side
        tree.attachVerified(
            location = handle160.getLeftChildLocation(),
            payload = 155,
            color = TestColor.Pink,
        )

        tree.swapVerified(
            nodeHandle = handle200,
            side = BinaryTree.Side.Left,
        )

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 200,
            actual = tree.getPayload(
                nodeHandle = handle200,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle200,
            ),
        )

        assertEquals(
            expected = 160,
            actual = tree.getPayload(
                nodeHandle = handle160,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle160,
            ),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 90,
                    color = TestColor.Pink,
                ),
                rightChild = NodeData(
                    payload = 160,
                    color = TestColor.Yellow,
                    leftChild = NodeData(
                        payload = 150,
                        color = TestColor.Green,
                        rightChild = NodeData(
                            payload = 200,
                            color = TestColor.Blue,
                            leftChild = NodeData(
                                payload = 155,
                                color = TestColor.Pink,
                            ),
                        ),
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }

    /**
     * - The swapped node is non-root
     * - Primary side: right (neighbour = successor)
     * - The swapped node doesn't have a child on the opposite side (left)
     *   - It is not an extremal (minimal) value (link = predecessor)
     * - The neighbour (successor) is separated by one node
     * - The neighbour (successor) doesn't have a child on the primary side (right)
     */
    @Test
    fun testSwap_nonRoot_noOppositeChild_nonExtremal_successor_separated_neighbourNoOppositeChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle50 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 50,
            color = TestColor.Green,
        )

        tree.attachVerified(
            location = handle50.getLeftChildLocation(),
            payload = 25,
            color = TestColor.Blue,
        )

        // The swapped node
        val handle100 = tree.attachVerified(
            location = handle50.getRightChildLocation(),
            payload = 100,
            color = TestColor.Yellow,
        )

        val handle200 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 200,
            color = TestColor.Green,
        )

        // The successor
        val handle150 = tree.attachVerified(
            location = handle200.getLeftChildLocation(),
            payload = 150,
            color = TestColor.Pink,
        )

        tree.swapVerified(
            nodeHandle = handle100,
            side = BinaryTree.Side.Right,
        )

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 100,
            actual = tree.getPayload(
                nodeHandle = handle100,
            ),
        )

        assertEquals(
            expected = TestColor.Pink,
            actual = tree.getColor(
                nodeHandle = handle100,
            ),
        )

        assertEquals(
            expected = 150,
            actual = tree.getPayload(
                nodeHandle = handle150,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle150,
            ),
        )

        assertEquals(
            expected = NodeData(
                payload = 50,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 25,
                    color = TestColor.Blue,
                ),
                rightChild = NodeData(
                    payload = 150,
                    color = TestColor.Yellow,
                    rightChild = NodeData(
                        payload = 200,
                        color = TestColor.Green,
                        leftChild = NodeData(
                            payload = 100,
                            color = TestColor.Pink,
                        ),
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }

    /**
     * - The swapped node is non-root
     * - Primary side: right (neighbour = successor)
     * - The swapped node has a child on the opposite side (left) (link = child)
     * - The neighbour (successor) is the direct child
     * - The neighbour (successor) has a child on the primary side (right)
     */
    @Test
    fun testSwap_nonRoot_hasOppositeChild_successor_directChild_neighbourHasOppositeChild() {
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

        // The swapped node
        val handle200 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 200,
            color = TestColor.Yellow,
        )

        tree.attachVerified(
            location = handle200.getLeftChildLocation(),
            payload = 150,
            color = TestColor.Green,
        )

        // The successor
        val handle300 = tree.attachVerified(
            location = handle200.getRightChildLocation(),
            payload = 300,
            color = TestColor.Blue,
        )

        // The successor's child on the primary side
        tree.attachVerified(
            location = handle300.getRightChildLocation(),
            payload = 400,
            color = TestColor.Yellow,
        )

        tree.swapVerified(
            nodeHandle = handle200,
            side = BinaryTree.Side.Right,
        )

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 200,
            actual = tree.getPayload(
                nodeHandle = handle200,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle200,
            ),
        )

        assertEquals(
            expected = 300,
            actual = tree.getPayload(
                nodeHandle = handle300,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle300,
            ),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 90,
                    color = TestColor.Blue,
                ),
                rightChild = NodeData(
                    payload = 300,
                    color = TestColor.Yellow,
                    leftChild = NodeData(
                        payload = 150,
                        color = TestColor.Green,
                    ),
                    rightChild = NodeData(
                        payload = 200,
                        color = TestColor.Blue,
                        rightChild = NodeData(
                            payload = 400,
                            color = TestColor.Yellow,
                        ),
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }

    /**
     * - The swapped node is non-root
     * - Primary side: right (neighbour = successor)
     * - The swapped node doesn't have a child on the opposite side (left) (link = predecessor)
     * - The neighbour (successor) is separated by one node
     * - The neighbour (successor) has a child on the primary side (right)
     */
    @Test
    fun testSwap_nonRoot_hasOppositeChild_successor_separated_neighbourHasOppositeChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        // The swapped node
        val handle150 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 150,
            color = TestColor.Yellow,
        )

        val handle180 = tree.attachVerified(
            location = handle150.getRightChildLocation(),
            payload = 180,
            color = TestColor.Green,
        )

        // The successor
        val handle170 = tree.attachVerified(
            location = handle180.getLeftChildLocation(),
            payload = 170,
            color = TestColor.Blue,
        )

        // The successor's child on the primary side
        val handle172 = tree.attachVerified(
            location = handle170.getRightChildLocation(),
            payload = 172,
            color = TestColor.Yellow,
        )

        // The successor's own successor
        tree.attachVerified(
            location = handle172.getLeftChildLocation(),
            payload = 171,
            color = TestColor.Pink,
        )

        tree.swapVerified(
            nodeHandle = handle150,
            side = BinaryTree.Side.Right,
        )

        // Assert that swapped nodes preserved their payloads but inherited the
        // other node's color

        assertEquals(
            expected = 150,
            actual = tree.getPayload(
                nodeHandle = handle150,
            ),
        )

        assertEquals(
            expected = TestColor.Blue,
            actual = tree.getColor(
                nodeHandle = handle150,
            ),
        )

        assertEquals(
            expected = 170,
            actual = tree.getPayload(
                nodeHandle = handle170,
            ),
        )

        assertEquals(
            expected = TestColor.Yellow,
            actual = tree.getColor(
                nodeHandle = handle170,
            ),
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
                rightChild = NodeData(
                    payload = 170,
                    color = TestColor.Yellow,
                    rightChild = NodeData(
                        payload = 180,
                        color = TestColor.Green,
                        leftChild = NodeData(
                            payload = 150,
                            color = TestColor.Blue,
                            rightChild = NodeData(
                                payload = 172,
                                color = TestColor.Yellow,
                                leftChild = NodeData(
                                    payload = 171,
                                    color = TestColor.Pink,
                                ),
                            ),
                        ),
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }

    /**
     * - Pivot is non-root
     * - Rotation direction: counter-clockwise
     * - The ascending node has a child on the rotation direction's end side (left)
     *
     * Before rotation:
     *
     *  100
     *            150
     *       125        175
     *    110  130   160    180
     *
     *
     * After rotation:
     *
     *  100
     *               175
     *          150        180
     *       125  160
     *    110  130
     */
    @Test
    fun testRotate_ccw_ascendingHasEndChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle100 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
            color = TestColor.Green,
        )

        val handle150 = tree.attachVerified(
            location = handle100.getRightChildLocation(),
            payload = 150,
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
            color = TestColor.Pink,
        )

        tree.attachVerified(
            location = handle175.getRightChildLocation(),
            payload = 180,
            color = TestColor.Green,
        )

        tree.rotateVerified(
            pivotNodeHandle = handle150,
            direction = RotationDirection.CounterClockwise,
        )

        assertEquals(
            expected = NodeData(
                payload = 100,
                color = TestColor.Green,
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
                            color = TestColor.Pink,
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

    /**
     * - Pivot is root
     * - Rotation direction: clockwise
     * - The ascending node doesn't have a child on the rotation direction's end side (right)
     *
     * Before rotation:
     *
     *            150
     *       125        175
     *    110
     *
     *
     * After rotation:
     *
     *       125
     *    110    150
     *               175
     */
    @Test
    fun testRotate_cw_ascendingNoEndChild() {
        val tree = MutableUnbalancedBinaryTree.create<Int, TestColor>()

        val handle150 = tree.attachVerified(
            location = BinaryTree.RootLocation,
            payload = 150,
            color = TestColor.Yellow,
        )

        val handle125 = tree.attachVerified(
            location = handle150.getLeftChildLocation(),
            payload = 125,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle150.getRightChildLocation(),
            payload = 175,
            color = TestColor.Blue,
        )

        tree.attachVerified(
            location = handle125.getLeftChildLocation(),
            payload = 110,
            color = TestColor.Green,
        )

        tree.rotateVerified(
            pivotNodeHandle = handle150,
            direction = RotationDirection.Clockwise,
        )

        assertEquals(
            expected = NodeData(
                payload = 125,
                color = TestColor.Blue,
                leftChild = NodeData(
                    payload = 110,
                    color = TestColor.Green,
                ),
                rightChild = NodeData(
                    payload = 150,
                    color = TestColor.Yellow,
                    rightChild = NodeData(
                        payload = 175,
                        color = TestColor.Blue,
                    ),
                ),
            ),
            actual = tree.dump(),
        )
    }
}
