package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class WithNextTests {
    @Test
    fun testWithNext_standardCase() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
            FullDominoBlock(
                redNumber = 6,
                blueNumber = 2,
            ),
            FullDominoBlock(
                redNumber = 2,
                blueNumber = 5,
            ),
        ).withNext(
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = listOf(
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                    nextElement = RedHalfDominoBlock(
                        redNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNext_singleElement() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
        ).withNext(
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = listOf(
                WithNext<FullDominoBlock, DominoBlockRed>(
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = RedHalfDominoBlock(
                        redNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNext_emptyList() {
        val actual = emptyList<FullDominoBlock>().withNext(
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }

    @Test
    fun testWithNextBy_standardCase() {
        val actual = listOf(
            "@foo",
            "?bar",
            "!baz",
            "*xyz",
        ).withNextBy(
            outerRight = '&',
            selector = String::first,
        )

        assertEquals(
            expected = listOf(
                WithNext(
                    element = "@foo",
                    nextElement = '?',
                ),
                WithNext(
                    element = "?bar",
                    nextElement = '!',
                ),
                WithNext(
                    element = "!baz",
                    nextElement = '*',
                ),
                WithNext(
                    element = "*xyz",
                    nextElement = '&',
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNextBy_singleElement() {
        val actual = listOf(
            "@foo",
        ).withNextBy(
            outerRight = '&',
            selector = String::first,
        )

        assertEquals(
            expected = listOf(
                WithNext(
                    element = "@foo",
                    nextElement = '&'
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNextBy_emptyList() {
        val actual = emptyList<String>().withNextBy(
            outerRight = '&',
            selector = String::first,
        )

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }

    @Test
    fun testWithNextCyclic_standardCase() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
            FullDominoBlock(
                redNumber = 6,
                blueNumber = 2,
            ),
            FullDominoBlock(
                redNumber = 2,
                blueNumber = 5,
            ),
        ).withNextCyclic()

        assertEquals(
            expected = listOf(
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                ),
            ),
            actual = actual,
        )
    }
}
