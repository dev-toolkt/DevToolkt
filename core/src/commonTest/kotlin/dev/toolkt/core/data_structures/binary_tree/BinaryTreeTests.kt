package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.getHandle
import dev.toolkt.core.data_structures.binary_tree.test_utils.load
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTreeTests {
    private enum class TestColor {
        Pink, Orange, Green,
    }

    @Test
    fun testGetSideMostDescendant_leaf() {
        val tree = MutableUnbalancedBinaryTree.load(
            rootData = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 50,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 25,
                        color = TestColor.Orange,
                    ),
                    rightChild = NodeData(
                        payload = 75,
                        color = TestColor.Orange,
                    ),
                ),
                rightChild = NodeData(
                    payload = 150,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 125,
                        color = TestColor.Pink,
                        leftChild = NodeData(
                            payload = 115,
                            color = TestColor.Pink,
                            leftChild = NodeData(
                                payload = 110,
                                color = TestColor.Pink,
                            ),
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 175,
                        color = TestColor.Orange,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 110)

        assertEquals(
            expected = nodeHandle,
            actual = tree.getSideMostDescendant(
                nodeHandle = nodeHandle,
                side = BinaryTree.Side.Left,
            ),
        )
    }

    @Test
    fun testGetSideMostDescendant_nonLeaf() {
        val tree = MutableUnbalancedBinaryTree.load(
            rootData = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 50,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 25,
                        color = TestColor.Orange,
                    ),
                    rightChild = NodeData(
                        payload = 75,
                        color = TestColor.Orange,
                    ),
                ),
                rightChild = NodeData(
                    payload = 150,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 125,
                        color = TestColor.Pink,
                        leftChild = NodeData(
                            payload = 115,
                            color = TestColor.Pink,
                            leftChild = NodeData(
                                payload = 110,
                                color = TestColor.Pink,
                            ),
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 175,
                        color = TestColor.Orange,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 150)
        val descendantHandle = tree.getHandle(payload = 110)

        val actual = tree.getSideMostDescendant(
            nodeHandle = nodeHandle,
            side = BinaryTree.Side.Left,
        )

        assertEquals(
            expected = descendantHandle,
            actual = actual,
        )
    }

    @Test
    fun testGetInOrderSuccessor() {
        val tree = MutableUnbalancedBinaryTree.load(
            rootData = NodeData(
                payload = 100,
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 50,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 25,
                        color = TestColor.Orange,
                    ),
                    rightChild = NodeData(
                        payload = 75,
                        color = TestColor.Orange,
                    ),
                ),
                rightChild = NodeData(
                    payload = 150,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 125,
                        color = TestColor.Pink,
                        leftChild = NodeData(
                            payload = 115,
                            color = TestColor.Pink,
                            leftChild = NodeData(
                                payload = 110,
                                color = TestColor.Pink,
                            ),
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 175,
                        color = TestColor.Orange,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.root!!
        val successorHandle = tree.getHandle(payload = 110)

        assertEquals(
            expected = successorHandle,
            actual = tree.getInOrderSuccessor(nodeHandle = nodeHandle),
        )
    }
}
