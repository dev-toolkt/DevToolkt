package dev.toolkt.reactive

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamDivertTests {
    @Test
    fun testDivert() {
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2 = EventEmitter<Int>()

        val mutableStreamCell = MutableCell<EventStream<Int>>(
            initialValue = eventEmitter1,
        )

        val divertedStream = EventStream.divert(
            nestedEventStream = mutableStreamCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = divertedStream,
        )

        eventEmitter1.emit(2)

        eventEmitter2.emit(-1)

        eventEmitter1.emit(5)

        eventEmitter2.emit(-7)

        assertEquals(
            expected = listOf(2, 5),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableStreamCell.set(eventEmitter2)

        eventEmitter1.emit(3)

        eventEmitter2.emit(-2)

        eventEmitter1.emit(8)

        eventEmitter2.emit(-9)

        assertEquals(
            expected = listOf(-2, -9),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableStreamCell.set(eventEmitter1)

        eventEmitter1.emit(4)

        eventEmitter2.emit(-12)

        eventEmitter1.emit(11)

        eventEmitter2.emit(-77)

        assertEquals(
            expected = listOf(4, 11),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
