package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class MutableListUtilsTests {
    @Test
    fun testRemoveRange_empty() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.removeRange(2 until 2)

        assertEquals(
            expected = originalList,
            actual = mutableList,
        )
    }

    @Test
    fun testRemoveRange_emptyOutside() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        assertIs<IndexOutOfBoundsException>(
            assertFails {
                mutableList.removeRange(7 until 7)
            },
        )
    }

    @Test
    fun testRemoveRange_single() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.removeRange(2..2)

        assertEquals(
            expected = listOf(0, 1, 3, 4, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testRemoveRange_multiple() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.removeRange(2..4)

        assertEquals(
            expected = listOf(0, 1, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testRemoveRange_multiplePartiallyOutside() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        assertIs<IndexOutOfBoundsException>(
            assertFails {
                mutableList.removeRange(4 until 8)
            },
        )
    }

    @Test
    fun testRemoveRange_all() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.removeRange(0..5)

        assertEquals(
            expected = emptyList(),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_empty() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(2 until 2, emptyList())

        assertEquals(
            expected = originalList,
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_emptyOutside() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        assertIs<IndexOutOfBoundsException>(
            assertFails {
                mutableList.updateRange(7 until 7, emptyList())
            },
        )
    }

    @Test
    fun testUpdateRangeRange_insertSingle() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(2 until 2, listOf(21))
        assertEquals(
            expected = listOf(0, 1, 21, 2, 3, 4, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_insertMultiple() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(2 until 2, listOf(21, 22, 23))

        assertEquals(
            expected = listOf(0, 1, 21, 22, 23, 2, 3, 4, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_appendMultiple() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(6 until 6, listOf(60, 70))

        assertEquals(
            expected = listOf(0, 1, 2, 3, 4, 5, 60, 70),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_appendMultiple_outsideRange() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        assertIs<IndexOutOfBoundsException>(
            assertFails {
                mutableList.updateRange(6 until 10, listOf(21, 22, 23))
            },
        )
    }

    @Test
    fun testUpdateRangeRange_removeSingle() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(3..3, emptyList())

        assertEquals(
            expected = listOf(0, 1, 2, 4, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_removeMultiple() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(2..4, emptyList())

        assertEquals(
            expected = listOf(0, 1, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_changeSingle() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(3..3, listOf(30))

        assertEquals(
            expected = listOf(0, 1, 2, 30, 4, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_changeMultiple() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(2..4, listOf(20, 30, 40, 41))

        assertEquals(
            expected = listOf(0, 1, 20, 30, 40, 41, 5),
            actual = mutableList,
        )
    }

    @Test
    fun testUpdateRangeRange_changeAll() {
        val originalList = listOf(0, 1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        mutableList.updateRange(0..5, listOf(10, 20, 30))

        assertEquals(
            expected = listOf(10, 20, 30),
            actual = mutableList,
        )
    }
}
