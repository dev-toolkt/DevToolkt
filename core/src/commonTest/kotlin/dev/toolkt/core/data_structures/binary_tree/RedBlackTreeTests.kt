package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.assertHolds
import dev.toolkt.core.data_structures.binary_tree.RedBlackTree.Color
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeMatcher
import dev.toolkt.core.data_structures.binary_tree.test_utils.dump
import dev.toolkt.core.data_structures.binary_tree.test_utils.insertVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.removeVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.verifyFamily
import dev.toolkt.core.todo
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Ignore
class RedBlackTreeTests {
    @Test
    fun testInitial() {
        val tree = RedBlackTree<Int>()

        assertNull(
            actual = tree.dump(),
        )
    }

    @Test
    fun testInsert_root() {
        val tree = RedBlackTree<Int>()

        val handle100 = tree.insertVerified(
            location = BinaryTree.RootLocation,
            payload = 100,
        )

        assertEquals(
            expected = tree.dump(),
            actual = NodeData(
                payload = 100,
                color = Color.Red,
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

    /**
     * Insertion
     * Case #1: Parent is black (and is root)
     */
    @Test
    fun testInsert_blackRootParent() {
        val tree = RedBlackTree<Int>()

        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        NodeMatcher.Proper(
            parentMatcher = NodeMatcher.Null(),
            expectedPayload = 10,
            expectedColor = Color.Black,
        ).assertMatches(
            tree = tree,
            nodeHandle = parentHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Black,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #1: Parent is black
     */
    @Test
    fun testInsert_ordinaryBlackParent() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val grandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        NodeMatcher.Proper(
            expectedPayload = 10,
            expectedColor = Color.Black,
            leftChildMatcher = NodeMatcher.Proper(
                expectedHandle = parentHandle,
                expectedPayload = 10,
                expectedColor = Color.Black,
            ),
        ).assertMatches(
            tree = tree,
            nodeHandle = grandparentHandle,
        )

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Black,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, great-grandparent is black
     * (leads to Case #1)
     */
    @Test
    fun testInsert_redParentRedUncle_blackGreatGrandparent() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val grandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        // Great-grandparent
        NodeMatcher.Proper(
            expectedPayload = 10,
            expectedColor = Color.Black,
            // Grandparent
            rightChildMatcher = NodeMatcher.Proper(
                expectedHandle = parentHandle,
                expectedPayload = 10,
                expectedColor = Color.Black,
                // Parent
                leftChildMatcher = NodeMatcher.Proper(
                    expectedHandle = parentHandle,
                    expectedPayload = 10,
                    expectedColor = Color.Black,
                ),
                // Uncle
                rightChildMatcher = NodeMatcher.Proper(
                    expectedHandle = parentHandle,
                    expectedPayload = 10,
                    expectedColor = Color.Black,
                ),
            ),
        ).assertMatches(
            tree = tree,
            nodeHandle = greatGrandparentHandle,
        )

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedGrandparentHandle = grandparentHandle,
            expectedUncleHandle = uncleHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = grandparentHandle) == Color.Black,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, grandparent's parent (great-grandparent)
     * and grandparent's uncle are also red
     *
     * Leads to another Case #2
     *
     * First Case #2 from the left side, second Case #2 from the right side.
     *
     */
    @Test
    fun testInsert_redParentRedUncle_redGreatGrandparentRedGreatUncle() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatUncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGreatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        // Grandparent's grandparent
        NodeMatcher.Proper(
            expectedPayload = 10,
            expectedColor = Color.Black,
            // Grandparent's parent (great-grandparent)
            leftChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Red,
                // Grandparent
                leftChildMatcher = NodeMatcher.Proper(
                    expectedPayload = 10,
                    expectedColor = Color.Black,
                    // Uncle
                    leftChildMatcher = NodeMatcher.Proper(
                        expectedPayload = 10,
                        expectedColor = Color.Red,
                    ),
                    // Parent
                    rightChildMatcher = NodeMatcher.Proper(
                        expectedHandle = parentHandle,
                        expectedPayload = 10,
                        expectedColor = Color.Red,
                    ),
                ),
            ),
            // Grandparent's uncle
            rightChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Red,
            ),
        ).assertMatches(
            tree = tree,
            nodeHandle = greatGreatGrandparentHandle,
        )

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedUncleHandle = uncleHandle,
        )

        tree.verifyFamily(
            nodeHandle = parentHandle,
            expectedGrandparentHandle = greatGrandparentHandle,
            expectedUncleHandle = greatUncleHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = greatGrandparentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = greatUncleHandle) == Color.Red,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, the grandparent is a root
     *
     * Leads to Case #3, a red root
     *
     * Case #2 from the left side
     */
    @Test
    fun testInsert_redParentRedUncle_rootGrandparent() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val grandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        // Grandparent
        NodeMatcher.Proper(
            parentMatcher = NodeMatcher.Null(),
            expectedPayload = 10,
            expectedColor = Color.Black,
            // Uncle
            leftChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Red,
            ),
            // Parent
            rightChildMatcher = NodeMatcher.Proper(
                expectedHandle = parentHandle,
                expectedPayload = 10,
                expectedColor = Color.Red,
            ),
        ).assertMatches(
            tree = tree,
            nodeHandle = grandparentHandle,
        )

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedGrandparentHandle = grandparentHandle,
            expectedUncleHandle = uncleHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getParent(nodeHandle = grandparentHandle) == null,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, the great-grandparent is root and is red
     *
     * Leads to Case #4
     *
     * Case #2 from the right side,
     */
    @Test
    fun testInsert_redParentRedUncle_redRootGreatGrandparent() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        // Grandparent's parent (great-grandparent)
        NodeMatcher.Proper(
            expectedPayload = 10,
            expectedColor = Color.Red,
            // Grandparent
            leftChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Black,
                // Uncle
                leftChildMatcher = NodeMatcher.Proper(
                    expectedPayload = 10,
                    expectedColor = Color.Red,
                ),
                // Parent
                rightChildMatcher = NodeMatcher.Proper(
                    expectedHandle = parentHandle,
                    expectedPayload = 10,
                    expectedColor = Color.Red,
                ),
            ),
        ).assertMatches(
            tree = tree,
            nodeHandle = greatGrandparentHandle,
        )

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedUncleHandle = uncleHandle,
        )

        tree.verifyFamily(
            nodeHandle = parentHandle,
            expectedGrandparentHandle = greatGrandparentHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = greatGrandparentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getParent(nodeHandle = greatGrandparentHandle) == null,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, the grandparent's parent (great-grandparent)
     * is red and the grandparent's uncle is black, the grandparent is the inner
     * grandchild of its own grandparent.
     *
     * Leads to Case #5
     */
    @Test
    fun testInsert_redParentRedUncle_redGreatGrandparentBlackGreatUncle_inner() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val grandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatUncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGreatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        // Grandparent's grandparent
        NodeMatcher.Proper(
            expectedPayload = 10,
            expectedColor = Color.Black,
            // Grandparent's parent (great-grandparent)
            leftChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Red,
                // Grandparent
                leftChildMatcher = NodeMatcher.Proper(
                    expectedPayload = 10,
                    expectedColor = Color.Black,
                    // Uncle
                    leftChildMatcher = NodeMatcher.Proper(
                        expectedPayload = 10,
                        expectedColor = Color.Red,
                    ),
                    // Parent
                    rightChildMatcher = NodeMatcher.Proper(
                        expectedHandle = parentHandle,
                        expectedPayload = 10,
                        expectedColor = Color.Red,
                    ),
                ),
            ),
            // Grandparent's uncle
            rightChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Black,
            ),
        ).assertMatches(
            tree = tree,
            nodeHandle = greatGreatGrandparentHandle,
        )

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedUncleHandle = uncleHandle,
        )

        tree.verifyFamily(
            nodeHandle = parentHandle,
            expectedGrandparentHandle = greatGrandparentHandle,
            expectedUncleHandle = greatUncleHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Red,
        )

        val grandparentRelativeLocation = tree.locateRelatively(
            nodeHandle = grandparentHandle,
        ) ?: throw AssertionError("Grandparent isn't expected to be a root")

        val greatGrandparentRelativeLocation = tree.locateRelatively(
            nodeHandle = greatGrandparentHandle,
        ) ?: throw AssertionError("Great-grandparent isn't expected to be a root")

        assertHolds(
            precondition = grandparentRelativeLocation.side == greatGrandparentRelativeLocation.side.opposite,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = greatGrandparentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = greatUncleHandle) == Color.Black,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, the grandparent's parent (great-grandparent)
     * is red and the grandparent's uncle is black, the grandparent is the outer
     * grandchild of its own grandparent
     *
     * Leads to Case #6
     */
    @Test
    fun testInsert_redParentRedUncle_redGreatGrandparentBlackGreatUncle_outer() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val grandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatUncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val greatGreatGrandparentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        // Grandparent's grandparent
        NodeMatcher.Proper(
            expectedPayload = 10,
            expectedColor = Color.Black,
            // Grandparent's parent (great-grandparent)
            leftChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Red,
                // Grandparent
                leftChildMatcher = NodeMatcher.Proper(
                    expectedPayload = 10,
                    expectedColor = Color.Black,
                    // Uncle
                    leftChildMatcher = NodeMatcher.Proper(
                        expectedPayload = 10,
                        expectedColor = Color.Red,
                    ),
                    // Parent
                    rightChildMatcher = NodeMatcher.Proper(
                        expectedHandle = parentHandle,
                        expectedPayload = 10,
                        expectedColor = Color.Red,
                    ),
                ),
            ),
            // Grandparent's uncle
            rightChildMatcher = NodeMatcher.Proper(
                expectedPayload = 10,
                expectedColor = Color.Black,
            ),
        ).assertMatches(
            tree = tree,
            nodeHandle = greatGreatGrandparentHandle,
        )

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedUncleHandle = uncleHandle,
        )

        tree.verifyFamily(
            nodeHandle = parentHandle,
            expectedGrandparentHandle = greatGrandparentHandle,
            expectedUncleHandle = greatUncleHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Red,
        )

        val grandparentRelativeLocation = tree.locateRelatively(
            nodeHandle = grandparentHandle,
        ) ?: throw AssertionError("Grandparent isn't expected to be a root")

        val greatGrandparentRelativeLocation = tree.locateRelatively(
            nodeHandle = greatGrandparentHandle,
        ) ?: throw AssertionError("Great-grandparent isn't expected to be a root")

        assertHolds(
            precondition = grandparentRelativeLocation.side == greatGrandparentRelativeLocation.side,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = greatGrandparentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = greatUncleHandle) == Color.Black,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #4: Parent is a root and is red
     */
    @Test
    fun testInsert_redRootParent() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedGrandparentHandle = null,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #5: Parent is red and uncle is black, node is the inner grandchild of
     * its grandparent
     */
    @Test
    fun testInsert_redParentBlackUncle_inner() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedUncleHandle = uncleHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Black,
        )

        val nodeRelativeLocation = tree.locateRelatively(
            nodeHandle = nodeHandle,
        ) ?: throw AssertionError("Node isn't expected to be a root")

        val parentRelativeLocation = tree.locateRelatively(
            nodeHandle = parentHandle,
        ) ?: throw AssertionError("Parent isn't expected to be a root")

        assertHolds(
            precondition = nodeRelativeLocation.side == parentRelativeLocation.side.opposite,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #6: Parent is red and uncle is black, node is the outer grandchild of
     * its grandparent
     */
    @Test
    fun testInsert_redParentBlackUncle_outer() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val uncleHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedUncleHandle = uncleHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = uncleHandle) == Color.Black,
        )

        val nodeRelativeLocation = tree.locateRelatively(
            nodeHandle = nodeHandle,
        ) ?: throw AssertionError("Node isn't expected to be a root")

        val parentRelativeLocation = tree.locateRelatively(
            nodeHandle = parentHandle,
        ) ?: throw AssertionError("Parent isn't expected to be a root")

        assertHolds(
            precondition = nodeRelativeLocation.side == parentRelativeLocation.side,
        )

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Simple removal (two children, successor: leaf)
     */
    @Test
    fun testRemove_twoChildren_leaf() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val successorHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        val leftChildHandle = tree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = tree.getRightChild(nodeHandle = nodeHandle)

        assertHolds(
            precondition = leftChildHandle != null && rightChildHandle != null,
        )

        val successorLeftChildHandle = tree.getLeftChild(nodeHandle = successorHandle)
        val successorRightChildHandle = tree.getRightChild(nodeHandle = successorHandle)

        assertHolds(
            precondition = successorLeftChildHandle == null && successorRightChildHandle == null,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (two children, successor: one child)
     */
    @Test
    fun testRemove_twoChildren_oneChild() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val successorHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        val leftChildHandle = tree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = tree.getRightChild(nodeHandle = nodeHandle)

        assertHolds(
            precondition = leftChildHandle != null && rightChildHandle != null,
        )

        val successorLeftChildHandle = tree.getLeftChild(nodeHandle = successorHandle)
        val successorRightChildHandle = tree.getRightChild(nodeHandle = successorHandle)

        // Has at least one child...
        assertHolds(
            precondition = successorLeftChildHandle != null || successorRightChildHandle != null,
        )

        // ...but not two
        assertHolds(
            precondition = !(successorLeftChildHandle != null && successorRightChildHandle != null),
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (one child)
     */
    @Test
    fun testRemove_oneChild() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        val leftChildHandle = tree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = tree.getRightChild(nodeHandle = nodeHandle)

        // Has at least one child...
        assertHolds(
            precondition = leftChildHandle != null || rightChildHandle != null,
        )

        // ...but not two
        assertHolds(
            precondition = !(leftChildHandle != null && rightChildHandle != null),
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (leaf, root)
     */
    @Test
    fun testRemove_leaf_root() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        val leftChildHandle = tree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = tree.getRightChild(nodeHandle = nodeHandle)

        assertHolds(
            precondition = leftChildHandle == null && rightChildHandle == null,
        )

        assertHolds(
            precondition = tree.getParent(nodeHandle = nodeHandle) == null,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (leaf, red)
     */
    @Test
    fun testRemove_leaf_red() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        val leftChildHandle = tree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = tree.getRightChild(nodeHandle = nodeHandle)

        assertHolds(
            precondition = leftChildHandle == null && rightChildHandle == null,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = nodeHandle) == Color.Red,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #1: parent is root
     */
    @Test
    fun testRemove_blackLeaf_rootParent() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedGrandparentHandle = null,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #2: parent, sibling and nephews are all black
     */
    @Test
    fun testRemove_blackLeaf_blackCloseFamily() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val siblingHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val closeNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val distantNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedSiblingHandle = siblingHandle,
            expectedCloseNephewHandle = closeNephewHandle,
            expectedDistantNephewHandle = distantNephewHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = parentHandle) == Color.Black,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = siblingHandle) == Color.Black,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = closeNephewHandle) == Color.Black,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = distantNephewHandle) == Color.Black,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #3: The sibling is red (parent and nephews are black)
     */
    @Test
    fun testRemove_blackLeaf_redSibling() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val siblingHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val closeNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val distantNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedSiblingHandle = siblingHandle,
            expectedCloseNephewHandle = closeNephewHandle,
            expectedDistantNephewHandle = distantNephewHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = siblingHandle) == Color.Red,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #4: Parent is red, nephews are black (sibling is black)
     */
    @Test
    fun testRemove_blackLeaf_blackParentBlackNephews() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val siblingHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val closeNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val distantNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedSiblingHandle = siblingHandle,
            expectedCloseNephewHandle = closeNephewHandle,
            expectedDistantNephewHandle = distantNephewHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = siblingHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = closeNephewHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = distantNephewHandle) == Color.Red,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #5: Close nephew is red (sibling is black), distant nephew is black
     */
    @Test
    fun testRemove_blackLeaf_redCloseNephewBlackDistantNephew() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val siblingHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val closeNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val distantNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedSiblingHandle = siblingHandle,
            expectedCloseNephewHandle = closeNephewHandle,
            expectedDistantNephewHandle = distantNephewHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = closeNephewHandle) == Color.Red,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = distantNephewHandle) == Color.Black,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #6: Distant nephew is red (sibling is black)
     */
    @Test
    fun testRemove_blackLeaf_redDistantNephew() {
        val tree = RedBlackTree<Int>()

        val nodeHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val parentHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val siblingHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val closeNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()
        val distantNephewHandle: BinaryTree.NodeHandle<Int, Color> = todo()

        tree.verifyFamily(
            nodeHandle = nodeHandle,
            expectedParentHandle = parentHandle,
            expectedSiblingHandle = siblingHandle,
            expectedCloseNephewHandle = closeNephewHandle,
            expectedDistantNephewHandle = distantNephewHandle,
        )

        assertHolds(
            precondition = tree.getColor(nodeHandle = distantNephewHandle) == Color.Red,
        )

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }
}
