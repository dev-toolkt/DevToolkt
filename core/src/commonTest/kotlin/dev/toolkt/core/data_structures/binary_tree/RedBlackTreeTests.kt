package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.RedBlackTree.Color
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.buildBalance
import dev.toolkt.core.data_structures.binary_tree.test_utils.dump
import dev.toolkt.core.data_structures.binary_tree.test_utils.getHandle
import dev.toolkt.core.data_structures.binary_tree.test_utils.insertVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.loadVerified
import dev.toolkt.core.data_structures.binary_tree.test_utils.removeVerified
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
            expected = NodeData(
                payload = 100,
                color = Color.Black,
            ),
            actual = tree.dump(),
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
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 10,
                color = Color.Black,
            ),
        )

        val parentHandle = tree.root!!

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #1: Parent is black
     *
     * No fixup required
     */
    @Test
    fun testInsert_ordinaryBlackParent() {
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 100,
                color = Color.Red,
                leftChild = NodeData(
                    payload = 50,
                    color = Color.Black,
                ),
                rightChild = NodeData(
                    payload = 150,
                    color = Color.Black,
                ),
            ),
        )

        val parentHandle = tree.getHandle(50)

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1000,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, grandparent's parent is black
     *
     * Leads to Case #1
     */
    @Test
    fun testInsert_redParentRedUncle_blackGreatGrandparent() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent's grandparent
            rootData = NodeData(
                payload = 100.0,
                color = Color.Red,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 3,
                    payloadRange = 0.0..100.0,
                ),
                // Grandparent's parent
                rightChild = NodeData(
                    payload = 500.0,
                    color = Color.Black,
                    // Grandparent
                    leftChild = NodeData(
                        payload = 250.0,
                        color = Color.Black,
                        // Parent
                        leftChild = NodeData(
                            payload = 150.0,
                            color = Color.Red,
                        ),
                        // Uncle
                        rightChild = NodeData(
                            payload = 400.0,
                            color = Color.Red,
                        ),
                    ),
                    rightChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 500.0..1000.0,
                    ),
                ),
            )
        )

        val parentHandle = tree.getHandle(payload = 150.0)

        tree.insertVerified(
            location = parentHandle.getLeftChildLocation(),
            payload = 125.0,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, grandparent's parent and grandparent's
     * uncle are also red
     *
     * Leads to another Case #2
     *
     * First Case #2 from the left side, second Case #2 from the right side.
     *
     */
    @Test
    fun testInsert_redParentRedUncle_redGreatGrandparentRedGreatUncle() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent's grandparent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                // Grandparent's parent
                leftChild = NodeData(
                    payload = 500.0,
                    color = Color.Red,
                    leftChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 0.0..500.0,
                    ),
                    // Grandparent
                    rightChild = NodeData(
                        payload = 750.0,
                        color = Color.Black,
                        // Parent
                        leftChild = NodeData(
                            payload = 600.0,
                            color = Color.Red,
                        ),
                        // Uncle
                        rightChild = NodeData(
                            payload = 850.0,
                            color = Color.Red,
                        ),
                    ),
                ),
                // Grandparent's uncle
                rightChild = NodeData(
                    payload = 1500.0,
                    color = Color.Red,
                    leftChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 1000.0..1500.0,
                    ),
                    rightChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 1500.0..2000.0,
                    ),
                ),
            ),
        )

        val parentHandle = tree.getHandle(payload = 600.0)

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 650.0,
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
        // Black height: 2
        val tree = RedBlackTree.loadVerified(
            // Grandparent
            rootData = NodeData(
                payload = 750.0,
                color = Color.Black,
                // Uncle
                leftChild = NodeData(
                    payload = 600.0,
                    color = Color.Red,
                ),
                // Parent
                rightChild = NodeData(
                    payload = 850.0,
                    color = Color.Red,
                ),
            ),
        )

        val parentHandle = tree.getHandle(payload = 850.0)

        tree.insertVerified(
            location = parentHandle.getLeftChildLocation(),
            payload = 800.0,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, the grandparent's parent is root and is red
     *
     * Leads to Case #4
     *
     * Case #2 from the right side,
     */
    @Test
    fun testInsert_redParentRedUncle_redRootGreatGrandparent() {
        // Black height: 2
        val tree = RedBlackTree.loadVerified(
            // Grandparent's parent
            rootData = NodeData(
                payload = 500.0,
                color = Color.Red,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 2,
                    payloadRange = 0.0..500.0,
                ),
                // Grandparent
                rightChild = NodeData(
                    payload = 1000.0,
                    color = Color.Black,
                    // Uncle
                    leftChild = NodeData(
                        payload = 750.0,
                        color = Color.Red,
                    ),
                    // Parent
                    rightChild = NodeData(
                        payload = 1500.0,
                        color = Color.Red,
                    ),
                ),
            ),
        )

        val parentHandle = tree.getHandle(payload = 1500.0)

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 2000.0,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, the grandparent's parent is red and the
     * grandparent's uncle is black, the grandparent is the inner grandchild of
     * its own grandparent.
     *
     * Leads to Case #5
     */
    @Test
    fun testInsert_redParentRedUncle_redGreatGrandparentBlackGreatUncle_inner() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent's grandparent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                // Grandparent's uncle
                leftChild = NodeData(
                    payload = 500.0,
                    color = Color.Black,
                ),
                // Grandparent's parent
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Red,
                    // Grandparent
                    leftChild = NodeData(
                        payload = 1500.0,
                        color = Color.Black,
                        // Parent
                        leftChild = NodeData(
                            payload = 1250.0,
                            color = Color.Red,
                        ),
                        // Uncle
                        rightChild = NodeData(
                            payload = 1750.0,
                            color = Color.Red,
                        ),
                    ),
                    rightChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 2000.0..3000.0,
                    ),
                ),
            )
        )

        val parentHandle = tree.getHandle(payload = 1250.0)

        tree.insertVerified(
            location = parentHandle.getRightChildLocation(),
            payload = 1300.0,
        )
    }

    /**
     * Insertion
     * Case #2: Parent and uncle are red, the grandparent's parent is red and the
     * grandparent's uncle is black, the grandparent is the outer grandchild of its own grandparent
     *
     * Leads to Case #6
     */
    @Test
    fun testInsert_redParentRedUncle_redGreatGrandparentBlackGreatUncle_outer() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent's grandparent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                // Grandparent's uncle
                leftChild = NodeData(
                    payload = 500.0,
                    color = Color.Black,
                ),
                // Grandparent's parent
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Red,
                    leftChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 1000.0..2000.0,
                    ),
                    // Grandparent
                    rightChild = NodeData(
                        payload = 3000.0,
                        color = Color.Black,
                        // Parent
                        leftChild = NodeData(
                            payload = 2500.0,
                            color = Color.Red,
                        ),
                        // Uncle
                        rightChild = NodeData(
                            payload = 4000.0,
                            color = Color.Red,
                        ),
                    ),
                ),
            )
        )

        val parentHandle = tree.getHandle(payload = 2500.0)

        tree.insertVerified(
            location = parentHandle.getLeftChildLocation(),
            payload = 2250.0,
        )
    }

    /**
     * Insertion
     * Case #4: Parent is a root and is red
     *
     * Immediate fixup
     */
    @Test
    fun testInsert_redRootParent() {
        // Black height: 1
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Red,
            ),
        )

        val parentHandle = tree.root!!

        tree.insertVerified(
            location = parentHandle.getLeftChildLocation(),
            payload = 500.0,
        )
    }

    /**
     * Insertion
     * Case #5: Parent is red and uncle is black, node is the inner grandchild of
     * its grandparent
     *
     * Goes through Case #6 internally
     */
    @Test
    fun testInsert_redParentBlackUncle_inner() {
        // Black height: 2
        val tree = RedBlackTree.loadVerified(
            // Grandparent's parent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Red,
                // Grandparent
                leftChild = NodeData(
                    payload = 500.0,
                    color = Color.Black,
                    // Uncle: nil
                    // Parent
                    rightChild = NodeData(
                        payload = 750.0,
                        color = Color.Red,
                    ),
                ),
                rightChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 2,
                    payloadRange = 1000.0..2000.0,
                ),
            ),
        )


        val parentHandle = tree.getHandle(payload = 750.0)

        tree.insertVerified(
            location = parentHandle.getLeftChildLocation(),
            payload = 700.0,
        )
    }

    /**
     * Insertion
     * Case #6: Parent is red and uncle is nil (effectively black), node is the outer grandchild of
     * its grandparent
     *
     * Immediate fixup
     */
    @Test
    fun testInsert_redParentBlackUncle_outer() {
        // Black height: 2
        val tree = RedBlackTree.loadVerified(
            // Grandparent's parent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Red,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 2,
                    payloadRange = 0.0..1000.0,
                ),
                // Grandparent
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Black,
                    // Parent
                    leftChild = NodeData(
                        payload = 1500.0,
                        color = Color.Red,
                    ),
                    // Uncle: nil
                ),
            ),
        )

        val parentHandle = tree.getHandle(payload = 1500.0)

        tree.insertVerified(
            location = parentHandle.getLeftChildLocation(),
            payload = 1250.0,
        )
    }

    /**
     * Simple removal (two children, successor: leaf)
     */
    @Test
    fun testRemove_twoChildren_leaf() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 4,
                    payloadRange = 0.0..1000.0,
                ),
                // Removed node
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Red,
                    leftChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 4,
                        payloadRange = 1000.0..2000.0,
                    ),
                    rightChild = NodeData(
                        payload = 3000.0,
                        color = Color.Black,
                        leftChild = NodeData(
                            payload = 2800.0,
                            color = Color.Black,
                            // Successor
                            leftChild = NodeData(
                                payload = 2500.0,
                                color = Color.Black,
                            ),
                            rightChild = RedBlackTree.buildBalance(
                                requiredBlackDepth = 2,
                                payloadRange = 2800.0..3000.0,
                            ),
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 3,
                            payloadRange = 3000.0..4000.0,
                        ),
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 2000.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (two children, successor: one child)
     */
    @Test
    fun testRemove_twoChildren_oneChild() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 3,
                    payloadRange = 0.0..1000.0,
                ),
                // Removed node
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Red,
                    leftChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 3,
                        payloadRange = 1000.0..2000.0,
                    ),
                    rightChild = NodeData(
                        payload = 3000.0,
                        color = Color.Black,
                        // Successor
                        leftChild = NodeData(
                            payload = 2800.0,
                            color = Color.Black,
                            rightChild = NodeData(
                                payload = 2900.0,
                                color = Color.Red,
                            ),
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 3000.0..4000.0,
                        ),
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 2800.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (one child)
     */
    @Test
    fun testRemove_oneChild() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Red,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 3,
                    payloadRange = 0.0..1000.0,
                ),
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Black,
                    // Removed node
                    leftChild = NodeData(
                        payload = 1500.0,
                        color = Color.Black,
                        leftChild = NodeData(
                            payload = 1250.0,
                            color = Color.Red,
                        ),
                    ),
                    rightChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 2000.0..4000.0,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 1500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (leaf, red root)
     */
    @Test
    fun testRemove_leaf_redRoot() {
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Red,
            ),
        )

        val nodeHandle = tree.root!!

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (leaf, red root)
     */
    @Test
    fun testRemove_leaf_blackRoot() {
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Black,
            ),
        )

        val nodeHandle = tree.root!!

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Simple removal (leaf, red)
     */
    @Test
    fun testRemove_leaf_red() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Red,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 3,
                    payloadRange = 0.0..1000.0,
                ),
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Black,
                    leftChild = NodeData(
                        payload = 1500.0,
                        color = Color.Black,
                        // Removed node
                        leftChild = NodeData(
                            payload = 1200.0,
                            color = Color.Red,
                        ),
                    ),
                    rightChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 2000.0..4000.0,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 1200.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #1: parent is root
     *
     * No fixup required
     */
    @Test
    fun testRemove_blackLeaf_rootParent() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Red,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 2,
                    payloadRange = 0.0..1000.0,
                ),
                rightChild = NodeData(
                    payload = 2000.0,
                    color = Color.Black,
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 2000.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #2: parent and sibling are black, nephews are nil (effectively black)
     *
     * Leads to Case #1
     */
    @Test
    fun testRemove_blackLeaf_blackCloseFamily_to_rootParent() {
        // Black height: ?
        val tree = RedBlackTree.loadVerified(
            // Parent
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Black,
                // Removed node
                leftChild = NodeData(
                    payload = 1500.0,
                    color = Color.Black,
                ),
                // Sibling
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Black,
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 1500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #2: parent and sibling are black, nephews are nil (effectively black)
     *
     * Leads to another Case #2, parent and sibling are black, nephews are now
     * proper black nodes
     */
    @Test
    fun testRemove_blackLeaf_blackCloseFamily_to_blackCloseFamily() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            // Parent's parent (grandparent)
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                // Parent's sibling (uncle)
                leftChild = NodeData(
                    payload = 500.0,
                    color = Color.Black,
                    // Parent's distant nephew
                    leftChild = NodeData(
                        payload = 250.0,
                        color = Color.Black,
                    ),
                    // Parent's close nephew
                    rightChild = NodeData(
                        payload = 750.0,
                        color = Color.Black,
                    ),
                ),
                // Parent
                rightChild = NodeData(
                    payload = 1500.0,
                    color = Color.Black,
                    // Removed node
                    leftChild = NodeData(
                        payload = 1250.0,
                        color = Color.Black,
                    ),
                    // Sibling
                    rightChild = NodeData(
                        payload = 2000.0,
                        color = Color.Black,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 1250.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #2: parent and sibling are black, nephews are nil (effectively black)
     *
     * Leads to Case #3, then Cases #4/#5/#6, then done
     */
    @Test
    fun testRemove_blackLeaf_blackCloseFamily_to_redSibling() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            // Parent's grandparent
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Red,
                // Parent's parent (grandparent)
                leftChild = NodeData(
                    payload = 1000.0,
                    color = Color.Black,
                    // Parent
                    leftChild = NodeData(
                        payload = 500.0,
                        color = Color.Black,
                        // Sibling
                        leftChild = NodeData(
                            payload = 250.0,
                            color = Color.Black,
                        ),
                        // Removed node
                        rightChild = NodeData(
                            payload = 750.0,
                            color = Color.Black,
                        ),
                    ),
                    // Parent's sibling (uncle)
                    rightChild = NodeData(
                        payload = 1500.0,
                        color = Color.Red,
                        // Parent's close nephew
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 3,
                            payloadRange = 1000.0..1500.0,
                        ),
                        // Parent's distant nephew
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 3,
                            payloadRange = 1500.0..2000.0,
                        ),
                    ),
                ),
                rightChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 4,
                    payloadRange = 2000.0..4000.0,
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 750.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #2: parent and sibling are black, nephews are nil (effectively black)
     *
     * Leads to Case #4, then done
     */
    @Test
    fun testRemove_blackLeaf_blackCloseFamily_to_blackParentBlackNephews() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Parent's parent (grandparent)
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Red,
                // Parent's sibling (uncle)
                leftChild = NodeData(
                    payload = 1000.0,
                    color = Color.Black,
                    // Parent's distant nephew
                    leftChild = NodeData(
                        payload = 500.0,
                        color = Color.Black,
                    ),
                    // Parent's close nephew
                    rightChild = NodeData(
                        payload = 1500.0,
                        color = Color.Black,
                    ),
                ),
                // Parent
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Black,
                    // Removed node
                    leftChild = NodeData(
                        payload = 2500.0,
                        color = Color.Black,
                    ),
                    // Sibling
                    rightChild = NodeData(
                        payload = 3500.0,
                        color = Color.Black,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 2500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #2: parent and sibling are black, nephews are nil (effectively black)
     *
     * Leads to Case #5, then (internally) Case 6, then done
     */
    @Test
    fun testRemove_blackLeaf_blackCloseFamily_to_redCloseNephewBlackDistantNephew() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Parent's parent (grandparent)
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Red,
                // Parent's sibling (uncle)
                leftChild = NodeData(
                    payload = 1000.0,
                    color = Color.Black,
                    // Parent's distant nephew
                    leftChild = NodeData(
                        payload = 500.0,
                        color = Color.Black,
                    ),
                    // Parent's close nephew
                    rightChild = NodeData(
                        payload = 1500.0,
                        color = Color.Red,
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 1000.0..1500.0,
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 1500.0..2000.0,
                        ),
                    ),
                ),
                // Parent
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Black,
                    // Sibling
                    leftChild = NodeData(
                        payload = 2500.0,
                        color = Color.Black,
                    ),
                    // Removed node
                    rightChild = NodeData(
                        payload = 3500.0,
                        color = Color.Black,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 3500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #2: parent and sibling are black, nephews are nil (effectively black)
     *
     * Leads to Case #6, then done
     */
    @Test
    fun testRemove_blackLeaf_blackCloseFamily_to_redDistantNephew() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            // Parent's parent (grandparent)
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Black,
                // Parent
                leftChild = NodeData(
                    payload = 1500.0,
                    color = Color.Black,
                    // Removed node
                    leftChild = NodeData(
                        payload = 1000.0,
                        color = Color.Black,
                    ),
                    // Sibling
                    rightChild = NodeData(
                        payload = 1750.0,
                        color = Color.Black,
                    ),
                ),
                // Parent's sibling (uncle)
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Black,
                    // Parent's close nephew
                    leftChild = NodeData(
                        payload = 2500.0,
                        color = Color.Red,
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 2000.0..2500.0,
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 2500.0..3000.0,
                        ),
                    ),
                    // Parent's distant nephew
                    rightChild = NodeData(
                        payload = 3500.0,
                        color = Color.Red,
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 3000.0..3500.0,
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 3500.0..5000.0,
                        ),
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 1000.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #3: The sibling is red (parent and nephews are black)
     *
     * Leads to Case #4, then done
     */
    @Test
    fun testRemove_blackLeaf_redSibling_to_blackParentBlackNephews() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Red,
                // Parent
                leftChild = NodeData(
                    payload = 1000.0,
                    color = Color.Black,
                    // Sibling
                    leftChild = NodeData(
                        payload = 500.0,
                        color = Color.Red,
                        // Distant nephew
                        leftChild = NodeData(
                            payload = 250.0,
                            color = Color.Black,
                        ),
                        // Close nephew
                        rightChild = NodeData(
                            payload = 750.0,
                            color = Color.Black,
                        ),
                    ),
                    // Removed node
                    rightChild = NodeData(
                        payload = 1500.0,
                        color = Color.Black,
                    ),
                ),
                // Parent's sibling (uncle)
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Black,
                    // Parent's close nephew
                    leftChild = NodeData(
                        payload = 2500.0,
                        color = Color.Black,
                    ),
                    // Parent's distant nephew
                    rightChild = NodeData(
                        payload = 4000.0,
                        color = Color.Black,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 1500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #3: The sibling is red (parent and nephews are black)
     *
     * Leads to Case #5, then Case #6, then done
     */
    @Test
    fun testRemove_blackLeaf_redSibling_to_redCloseNephewBlackDistantNephew() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            // Grandparent
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Black,
                // Parent's sibling (uncle)
                leftChild = NodeData(
                    payload = 1000.0,
                    color = Color.Black,
                    // Parent's distant nephew
                    leftChild = NodeData(
                        payload = 500.0,
                        color = Color.Black,
                    ),
                    // Parent's close nephew
                    rightChild = NodeData(
                        payload = 1500.0,
                        color = Color.Red,
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 1000.0..1500.0,
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 1500.0..2000.0,
                        ),
                    ),
                ),
                // Parent
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Black,
                    // Removed node
                    leftChild = NodeData(
                        payload = 2500.0,
                        color = Color.Black,
                    ),
                    // Sibling
                    rightChild = NodeData(
                        payload = 4000.0,
                        color = Color.Red,
                        // Close nephew
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 3000.0..4000.0,
                        ),
                        // Distant nephew
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 4000.0..5000.0,
                        ),
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 2500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #3: The sibling is red (parent and nephews are black)
     *
     * Leads to Case #6, then done
     */
    @Test
    fun testRemove_blackLeaf_redSibling_to_redDistantNephew() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent
            rootData = NodeData(
                payload = 2000.0,
                color = Color.Red,
                // Parent
                leftChild = NodeData(
                    payload = 1000.0,
                    color = Color.Black,
                    // Sibling
                    leftChild = NodeData(
                        payload = 500.0,
                        color = Color.Red,
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 0.0..500.0,
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 500.0..1000.0,
                        ),
                    ),
                    // Removed node
                    rightChild = NodeData(
                        payload = 1500.0,
                        color = Color.Black,
                    ),
                ),
                // Parent's sibling
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Black,
                    // Parent's close nephew
                    leftChild = RedBlackTree.buildBalance(
                        requiredBlackDepth = 2,
                        payloadRange = 2000.0..3000.0,
                    ),
                    // Parent's distant nephew
                    rightChild = NodeData(
                        payload = 4000.0,
                        color = Color.Red,
                        leftChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 3000.0..4000.0,
                        ),
                        rightChild = RedBlackTree.buildBalance(
                            requiredBlackDepth = 2,
                            payloadRange = 4000.0..5000.0,
                        ),
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 1500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #4: Parent is red, nephews are nil (effectively black) (sibling is black)
     */
    @Test
    fun testRemove_blackLeaf_blackParentBlackNephews() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                // Parent
                leftChild = NodeData(
                    payload = 500.0,
                    color = Color.Red,
                    // Removed node
                    leftChild = NodeData(
                        payload = 250.0,
                        color = Color.Black,
                    ),
                    // Sibling
                    rightChild = NodeData(
                        payload = 750.0,
                        color = Color.Black,
                        // Nephews: nil
                    ),
                ),
                rightChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 2,
                    payloadRange = 1000.0..2000.0,
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 250.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #5: Close nephew is red (sibling is black), distant nephew is nil (effectively black)
     */
    @Test
    fun testRemove_blackLeaf_redCloseNephewBlackDistantNephew() {
        // Black height: 3
        val tree = RedBlackTree.loadVerified(
            // Grandparent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                leftChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 2,
                    payloadRange = 0.0..1000.0,
                ),
                // Parent
                rightChild = NodeData(
                    payload = 3000.0,
                    color = Color.Red,
                    // Sibling
                    leftChild = NodeData(
                        payload = 2000.0,
                        color = Color.Black,
                        // Distant nephew: nil
                        // Close nephew
                        rightChild = NodeData(
                            payload = 2500.0,
                            color = Color.Red,
                        ),
                    ),
                    // Removed node
                    rightChild = NodeData(
                        payload = 3500.0,
                        color = Color.Black,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 3500.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    // TODO: Create a test for Case #2 -> Case #6 with red close nephew (or prove that it's impossible!)
    /**
     * Complex removal (black leaf)
     * Case #6: Distant nephew is red (sibling is black) [parent: black]
     */
    @Test
    fun testRemove_blackLeaf_redDistantNephew_blackParent() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            // Grandparent
            rootData = NodeData(
                payload = 1000.0,
                color = Color.Black,
                // parent
                leftChild = NodeData(
                    payload = 500.0,
                    color = Color.Black,
                    // Removed node
                    leftChild = NodeData(
                        payload = 250.0,
                        color = Color.Black,
                    ),
                    // Sibling
                    rightChild = NodeData(
                        payload = 750.0,
                        color = Color.Black,
                        // Close nephew: nil
                        // Distant nephew
                        rightChild = NodeData(
                            payload = 800.0,
                            color = Color.Red,
                        ),
                    ),
                ),
                rightChild = RedBlackTree.buildBalance(
                    requiredBlackDepth = 3,
                    payloadRange = 1000.0..2000.0,
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 250.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }

    /**
     * Complex removal (black leaf)
     * Case #6: Distant nephew is red (sibling is black) [parent: red]
     */
    @Test
    fun testRemove_blackLeaf_redDistantNephew_redParent() {
        // Black height: 4
        val tree = RedBlackTree.loadVerified(
            // parent
            rootData = NodeData(
                payload = 500.0,
                color = Color.Red,
                // Removed node
                leftChild = NodeData(
                    payload = 250.0,
                    color = Color.Black,
                ),
                // Sibling
                rightChild = NodeData(
                    payload = 750.0,
                    color = Color.Black,
                    // Close nephew: nil
                    // Distant nephew
                    rightChild = NodeData(
                        payload = 800.0,
                        color = Color.Red,
                    ),
                ),
            ),
        )

        val nodeHandle = tree.getHandle(payload = 250.0)

        tree.removeVerified(
            nodeHandle = nodeHandle,
        )
    }
}
