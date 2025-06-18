package dev.toolkt.core.collections

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Ignore
class RedBlackTreeSetTests {
    @Test
    fun testInitial() {
        val set = RedBlackTreeSet<Int>()

        set.verifyContent(
            elements = emptyList(),
            controlElements = listOf(10, 20, 30),
        )
    }

    @Test
    fun testAdd_empty() {
        val set = RedBlackTreeSet<Int>()

        assertTrue(
            actual = set.add(10),
        )

        set.verifyContent(
            elements = listOf(10),
            controlElements = listOf(20, 30),
        )
    }

    @Test
    fun testAdd_nonEmpty() {
        val set = RedBlackTreeSet<Int>()

        set.addAll(
            listOf(
                10,
                20,
                30,
            ),
        )

        assertTrue(
            actual = set.add(15),
        )

        set.verifyContent(
            elements = listOf(10, 15, 20, 30),
            controlElements = listOf(-10, 40, 50),
        )
    }

    @Test
    fun testRemove() {
        val set = RedBlackTreeSet<Int>()

        set.addAll(
            listOf(
                10,
                20,
                30,
            ),
        )

        assertTrue(
            actual = set.remove(20),
        )

        set.verifyContent(
            elements = listOf(10, 30),
            controlElements = listOf(20, 40, 50),
        )

        assertTrue(
            actual = set.remove(10),
        )

        set.verifyContent(
            elements = listOf(30),
            controlElements = listOf(10, 20, 40, 50),
        )

        assertTrue(
            actual = set.remove(30),
        )

        set.verifyContent(
            elements = emptyList(),
            controlElements = listOf(10, 20, 30, 40, 50),
        )
    }

    @Test
    fun testRemove_notContained() {
        val set = RedBlackTreeSet<Int>()

        set.addAll(
            listOf(
                10,
                20,
                30,
            ),
        )

        assertFalse(
            actual = set.remove(40),
        )

        set.verifyContent(
            elements = listOf(10, 20, 30),
            controlElements = listOf(40, 50),
        )
    }
}
