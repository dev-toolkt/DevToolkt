package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.MutableCell
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

        val streamVerifier = EventStreamVerifier(
            eventStream = divertedStream,
        )

        eventEmitter1.emit(2)

        eventEmitter2.emit(-1)

        eventEmitter1.emit(5)

        eventEmitter2.emit(-7)

        assertEquals(
            expected = listOf(2, 5),
            actual = streamVerifier.removeReceivedEvents(),
        )

        mutableStreamCell.set(eventEmitter2)

        eventEmitter1.emit(3)

        eventEmitter2.emit(-2)

        eventEmitter1.emit(8)

        eventEmitter2.emit(-9)

        assertEquals(
            expected = listOf(-2, -9),
            actual = streamVerifier.removeReceivedEvents(),
        )

        mutableStreamCell.set(eventEmitter1)

        eventEmitter1.emit(4)

        eventEmitter2.emit(-12)

        eventEmitter1.emit(11)

        eventEmitter2.emit(-77)

        assertEquals(
            expected = listOf(4, 11),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
