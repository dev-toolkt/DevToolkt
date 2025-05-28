package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.Cell
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamHoldTests {
    @Test
    fun testHold() {
        val eventEmitter = EventEmitter<Int>()

        val heldCell = eventEmitter.hold(0)

        val changesVerifier = EventStreamVerifier(
            eventStream = heldCell.changes,
        )

        assertEquals(
            expected = 0,
            actual = heldCell.currentValue,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        eventEmitter.emit(3)

        assertEquals(
            expected = 3,
            actual = heldCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = 0,
                    newValue = 2,
                ),
                Cell.Change(
                    oldValue = 2,
                    newValue = 3,
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}