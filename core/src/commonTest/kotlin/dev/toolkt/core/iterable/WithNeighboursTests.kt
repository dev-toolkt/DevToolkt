package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class WithNeighboursTests {
    @Test
    fun testWithNeighbours_standardCase() {
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
        ).withNeighbours(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        ).toList()

        assertEquals(
            expected = listOf(
                WithNeighbours(
                    prevElement = BlueHalfDominoBlock(
                        blueNumber = 1,
                    ),
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithNeighbours(
                    prevElement = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
                WithNeighbours(
                    prevElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
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
    fun testWithNeighbours_singleElement() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
        ).withNeighbours(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        ).toList()

        assertEquals(
            expected = listOf(
                WithNeighbours(
                    prevElement = BlueHalfDominoBlock(
                        blueNumber = 1,
                    ),
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
    fun testWithNeighbours_emptyList() {
        val actual = emptyList<FullDominoBlock>().withNeighbours(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        ).toList()

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }
}
