package dev.toolkt.core.iterable

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class ClusterSimilarTests {
    @Test
    fun testClusterSimilarConsecutive_empty() {
        val actual = emptyList<Char>().clusterSimilarConsecutive { a, b -> a == b }

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilarConsecutive_simple() {
        val actual = listOf(
            'a', 'x', 'y', 'B', '!', '_', 'C', 'P', 'T', '=', '1', '2', '3',
        ).clusterSimilarConsecutive { a, b ->
            when {
                a.isDigit() && b.isDigit() -> true
                a.isLowerCase() && b.isLowerCase() -> true
                a.isUpperCase() && b.isUpperCase() -> true
                else -> false
            }
        }

        assertEquals(
            expected = listOf(
                listOf('a', 'x', 'y'),
                listOf('B'),
                listOf('!'),
                listOf('_'),
                listOf('C', 'P', 'T'),
                listOf('='),
                listOf('1', '2', '3'),
            ),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilarConsecutive_singleElement() {
        val actual = listOf('a').clusterSimilarConsecutive { a, b -> a == b }

        assertEquals(
            expected = listOf(listOf('a')),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilarConsecutive_allSimilar() {
        val actual = listOf(1, 1, 1, 1).clusterSimilarConsecutive { a, b -> a == b }

        assertEquals(
            expected = listOf(listOf(1, 1, 1, 1)),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilarConsecutive_noSimilar() {
        val actual = listOf(1, 2, 3, 4).clusterSimilarConsecutive { a, b -> false }

        assertEquals(
            expected = listOf(
                listOf(1),
                listOf(2),
                listOf(3),
                listOf(4),
            ),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilarConsecutive_complexCondition() {
        val actual = listOf(1, 2, 2, 3, 5, 8, 13, 21).clusterSimilarConsecutive { a, b ->
            (a + b) % 2 == 0
        }

        assertEquals(
            expected = listOf(
                listOf(1),
                listOf(2, 2),
                listOf(3, 5),
                listOf(8),
                listOf(13, 21),
            ),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilar_empty() {

    }

    @Test
    fun testClusterSimilar_simple() {
        val group1 = listOf(
            0.55,
            0.54,
            0.56,
            0.57,
        )

        val group2 = listOf(
            1.06,
            1.07,
            1.08,
            1.09,
        )

        val group3 = listOf(
            2.01,
            2.02,
            2.1,
        )

        val group4 = listOf(
            3.0,
            3.1,
            3.12,
        )

        val numbers = group1 + group2 + group3 + group4

        val actual = numbers.clusterSimilar { group, x ->
            abs(x - group.average()) < 0.2
        }

        assertEquals(
            expected = listOf(
                group1,
                group2,
                group3,
                group4,
            ),
            actual = actual,
        )
    }
}
