package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class CrackTests {
    @Test
    fun testCrack2_emptyList() {
        assertIs<IndexOutOfBoundsException>(
            value = assertFails {
                emptyList<String>().crackAt(
                    index = 0,
                    crack = { it.splitBefore(index = 1).pair },
                )
            },
        )
    }

    @Test
    fun testCrack2_singleElement() {
        val actual = listOf("AB").crackAt(
            index = 0,
            crack = { it.splitBefore(index = 1).pair },
        )

        assertEquals(
            expected = Split2(
                leadingElements = listOf("A"),
                trailingElements = listOf("B"),
            ),
            actual = actual,
        )
    }

    @Test
    fun testCrack2_twoElements() {
        val actual = listOf("AB", "CD").crackAt(
            index = 0,
            crack = { it.splitBefore(index = 1).pair },
        )

        assertEquals(
            expected = Split2(
                leadingElements = listOf("A"),
                trailingElements = listOf("B", "CD"),
            ),
            actual = actual,
        )
    }

    @Test
    fun testCrack2_threeElements() {
        val actual = listOf("AB", "CD", "EF").crackAt(
            index = 1,
            crack = { it.splitBefore(index = 1).pair },
        )

        assertEquals(
            expected = Split2(
                leadingElements = listOf("AB", "C"),
                trailingElements = listOf("D", "EF"),
            ),
            actual = actual,
        )
    }

    @Test
    fun testCrack2_standardCase() {
        val actual = listOf(
            "ABC",
            "DEFG",
            "HIJ",
            "KLMN",
        ).crackAt(
            index = 1,
            crack = { it.splitBefore(index = 2).pair },
        )

        assertEquals(
            expected = Split2(
                leadingElements = listOf(
                    "ABC",
                    "DE",
                ),
                trailingElements = listOf(
                    "FG",
                    "HIJ",
                    "KLMN",
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testCrack3_emptyList() {
        assertIs<IndexOutOfBoundsException>(
            value = assertFails {
                emptyList<String>().crackAt(
                    firstIndex = 0,
                    crackFirst = { it.splitBefore(index = 1).pair },
                    secondIndex = 1,
                    crackSecond = { it.splitBefore(index = 1).pair },
                )
            },
        )
    }

    @Test
    fun testCrack3_singleElement() {
        assertIs<IndexOutOfBoundsException>(
            value = assertFails {
                listOf("AB").crackAt(
                    firstIndex = 0,
                    crackFirst = { it.splitBefore(index = 1).pair },
                    secondIndex = 1,
                    crackSecond = { it.splitBefore(index = 1).pair },
                )
            },
        )
    }

    @Test
    fun testCrack3_twoElements_sameIndex() {
        assertIs<IllegalArgumentException>(
            value = assertFails {
                listOf("AB", "CD").crackAt(
                    firstIndex = 0,
                    crackFirst = { it.splitBefore(index = 1).pair },
                    secondIndex = 0,
                    crackSecond = { it.splitBefore(index = 1).pair },
                )
            },
        )
    }

    @Test
    fun testCrack3_twoElements() {
        val actual = listOf("AB", "CD").crackAt(
            firstIndex = 0,
            crackFirst = { it.splitBefore(index = 1).pair },
            secondIndex = 1,
            crackSecond = { it.splitBefore(index = 1).pair },
        )

        assertEquals(
            expected = Split3(
                leadingElements = listOf("A"),
                innerElements = listOf("B", "C"),
                trailingElements = listOf("D"),
            ),
            actual = actual,
        )
    }

    @Test
    fun testCrack3_threeElements() {
        val actual = listOf("AB", "CD", "EF").crackAt(
            firstIndex = 0,
            crackFirst = { it.splitBefore(index = 1).pair },
            secondIndex = 2,
            crackSecond = { it.splitBefore(index = 1).pair },
        )

        assertEquals(
            expected = Split3(
                leadingElements = listOf("A"),
                innerElements = listOf("B", "CD", "E"),
                trailingElements = listOf("F"),
            ),
            actual = actual,
        )
    }

    @Test
    fun testCrack3_standardCase() {
        val actual = listOf(
            "ABC",
            "DEFG",
            "HIJ",
            "KLMN",
            "OPQR",
        ).crackAt(
            firstIndex = 1,
            crackFirst = { it.splitBefore(index = 2).pair },
            secondIndex = 3,
            crackSecond = { it.splitBefore(index = 2).pair },
        )

        assertEquals(
            expected = Split3(
                leadingElements = listOf(
                    "ABC",
                    "DE",
                ),
                innerElements = listOf(
                    "FG",
                    "HIJ",
                    "KL",
                ),
                trailingElements = listOf(
                    "MN",
                    "OPQR",
                ),
            ),
            actual = actual,
        )
    }


    @Test
    fun testCrack3Cyclic_standardCase() {
        val actual = listOf(
            "ABC",
            "DEFG",
            "HIJ",
            "KLMN",
            "OPQR",
            "STU",
            "VWXYZ",
        ).crackAtCyclic(
            firstIndex = 1,
            crackFirst = { it.splitBefore(index = 2).pair },
            secondIndex = 4,
            crackSecond = { it.splitBefore(index = 2).pair },
        )

        assertEquals(
            expected = Split2(
                leadingElements = listOf(
                    "FG",
                    "HIJ",
                    "KLMN",
                    "OP",
                ),
                trailingElements = listOf(
                    "QR",
                    "STU",
                    "VWXYZ",
                    "ABC",
                    "DE",
                ),
            ),
            actual = actual,
        )
    }
}
