package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCollectionUtilsTests {
    @Test
    fun testForEachRemoving_removingNone() {
        val originalList = listOf(1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        val visitedElements = mutableListOf<Int>()

        mutableList.forEachRemoving {
            visitedElements.add(it)

            false
        }

        assertEquals(
            expected = originalList,
            actual = visitedElements,
        )

        assertEquals(
            expected = originalList,
            actual = mutableList,
        )
    }

    @Test
    fun testForEachRemoving_removingSome() {
        val originalList = listOf(1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        val visitedElements = mutableListOf<Int>()

        mutableList.forEachRemoving {
            visitedElements.add(it)

            it % 2 == 0
        }

        assertEquals(
            expected = originalList,
            actual = visitedElements,
        )

        assertEquals(
            expected = listOf(1, 3, 5),
            actual = mutableList,
        )
    }


    @Test
    fun testForEachRemoving_removingAll() {
        val originalList = listOf(1, 2, 3, 4, 5)

        val mutableList = originalList.toMutableList()

        val visitedElements = mutableListOf<Int>()

        mutableList.forEachRemoving {
            visitedElements.add(it)

            true
        }

        assertEquals(
            expected = originalList,
            actual = visitedElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = mutableList,
        )
    }
}
