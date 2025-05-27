package dev.toolkt.reactive

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Test
import kotlin.test.assertEquals

class CellSwitchTests {
    @Test
    fun testMap() {
        val mutableCell1 = MutableCell(
            initialValue = 10,
        )

        val mutableCell2 = MutableCell(
            initialValue = -2,
        )

        val mutableNestedCell = MutableCell(
            initialValue = mutableCell1,
        )

        val switchedCell = Cell.switch(
            nestedCell = mutableNestedCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = switchedCell.changes,
        )

        assertEquals(
            expected = 10,
            actual = switchedCell.currentValue,
        )

        mutableCell1.set(9)

        mutableCell2.set(-3)

        mutableCell1.set(8)

        assertEquals(
            expected = 8,
            actual = switchedCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 10,
                    newValue = 9,
                ),
                Cell.Change(
                    oldValue = 9,
                    newValue = 8,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableNestedCell.set(mutableCell2)

        assertEquals(
            expected = -3,
            actual = switchedCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 8,
                    newValue = -3,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableCell1.set(11)

        mutableCell2.set(-4)

        mutableCell1.set(12)

        mutableCell2.set(-5)

        assertEquals(
            expected = -5,
            actual = switchedCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = -3,
                    newValue = -4,
                ),
                Cell.Change(
                    oldValue = -4,
                    newValue = -5,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
