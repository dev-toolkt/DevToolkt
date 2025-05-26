package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class SeparateTests {
    @Test
    fun testSeparate2_emptyList() {
        assertIs<IndexOutOfBoundsException>(
            value = assertFails {
                emptyList<Int>().separateAt(
                    index = 0,
                )
            },
        )
    }

    @Test
    fun testSeparate2_singleElement() {
        val actual = listOf(
            10,
        ).separateAt(
            index = 0,
        )

        assertEquals(
            expected = Separation2(
                leadingElements = emptyList(),
                separator = 10,
                trailingElements = emptyList(),
            ),
            actual = actual,
        )
    }

    @Test
    fun testSeparate2_twoElements() {
        val actual = listOf(
            10,
            20,
        ).separateAt(
            index = 0,
        )

        assertEquals(
            expected = Separation2(
                leadingElements = emptyList(),
                separator = 10,
                trailingElements = listOf(20),
            ),
            actual = actual,
        )
    }

    @Test
    fun testSeparate2_threeElements() {
        val actual = listOf(
            10,
            20,
            30,
        ).separateAt(
            index = 1,
        )

        assertEquals(
            expected = Separation2(
                leadingElements = listOf(10),
                separator = 20,
                trailingElements = listOf(30),
            ),
            actual = actual,
        )
    }

    @Test
    fun testSeparate2_standardCase() {
        val actual = listOf(
            0,
            10,
            20,
            30,
            40,
            50,
            60,
            70,
        ).separateAt(
            index = 3,
        )

        assertEquals(
            expected = Separation2(
                leadingElements = listOf(0, 10, 20),
                separator = 30,
                trailingElements = listOf(40, 50, 60, 70),
            ),
            actual = actual,
        )
    }

    @Test
    fun testSeparate3_emptyList() {
        assertIs<IndexOutOfBoundsException>(
            value = assertFails {
                emptyList<Int>().separateAt(
                    firstIndex = 0,
                    secondIndex = 0,
                )
            },
        )
    }

    @Test
    fun testSeparate3_singleElement() {
        assertIs<IllegalArgumentException>(
            value = assertFails {
                listOf(10).separateAt(
                    firstIndex = 0,
                    secondIndex = 0,
                )
            },
        )
    }

    @Test
    fun testSeparate3_twoElements_sameIndex() {
        assertIs<IllegalArgumentException>(
            value = assertFails {
                listOf(
                    10,
                    20,
                ).separateAt(
                    firstIndex = 0,
                    secondIndex = 0,
                )
            },
        )
    }

    @Test
    fun testSeparate3_twoElements() {
        val actual = listOf(
            10,
            20,
        ).separateAt(
            firstIndex = 0,
            secondIndex = 1,
        )

        assertEquals(
            expected = Separation3(
                leadingElements = emptyList(),
                firstSeparator = 10,
                innerElements = emptyList(),
                secondSeparator = 20,
                trailingElements = emptyList(),
            ),
            actual = actual,
        )
    }

    @Test
    fun testSeparate3_threeElements() {
        val actual = listOf(
            10,
            20,
            30,
        ).separateAt(
            firstIndex = 0,
            secondIndex = 1,
        )

        assertEquals(
            expected = Separation3(
                leadingElements = emptyList(),
                firstSeparator = 10,
                innerElements = listOf(),
                secondSeparator = 20,
                trailingElements = listOf(30),
            ),
            actual = actual,
        )
    }

    @Test
    fun testSeparate3_standardCase() {
        val actual = listOf(
            0,
            10,
            20,
            30,
            40,
            50,
            60,
            70,
            80,
        ).separateAt(
            firstIndex = 2,
            secondIndex = 5,
        )

        assertEquals(
            expected = Separation3(
                leadingElements = listOf(0, 10),
                firstSeparator = 20,
                innerElements = listOf(30, 40),
                secondSeparator = 50,
                trailingElements = listOf(60, 70, 80),
            ),
            actual = actual,
        )
    }
}
