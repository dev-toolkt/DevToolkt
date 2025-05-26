package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class AllPairsTests {
    @Test
    fun testAllUniquePairs_empty() {
        assertEquals(
            expected = emptySet(),
            actual = emptySet<Int>().allUniquePairs().toSet(),
        )
    }

    @Test
    fun testAllUniquePairs_single() {
        assertEquals(
            expected = emptySet(),
            actual = setOf(
                1,
            ).allUniquePairs().toSet(),
        )
    }

    @Test
    fun testAllUniquePairs_two() {
        assertEquals(
            expected = setOf(
                Pair(1, 9),
            ),
            actual = setOf(
                1,
                9,
            ).allUniquePairs().toSet(),
        )
    }

    @Test
    fun testAllUniquePairs_three() {
        assertEquals(
            expected = setOf(
                Pair(1, 9),
                Pair(8, 1),
                Pair(8, 9),
            ),
            actual = setOf(
                8,
                1,
                9,
            ).allUniquePairs().toSet(),
        )
    }

    @Test
    fun testAllUniquePairs_multiple() {
        assertEquals(
            expected = setOf(
                Pair(1, 3),
                Pair(1, 7),
                Pair(1, 2),
                Pair(3, 7),
                Pair(3, 2),
                Pair(7, 2),
            ),
            actual = setOf(
                1,
                3,
                7,
                2,
            ).allUniquePairs().toSet(),
        )
    }
}
