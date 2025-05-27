package dev.toolkt.reactive

import dev.toolkt.reactive.event_stream.EventEmitter
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamFilterTests {
    @Test
    fun testFilter() {
        val eventEmitter = EventEmitter<Int>()

        val mappedStream = eventEmitter.filter { it % 2 == 0 }

        val changesVerifier = EventStreamVerifier(
            eventStream = mappedStream,
        )

        eventEmitter.emit(2)

        eventEmitter.emit(3)

        eventEmitter.emit(5)

        eventEmitter.emit(4)

        assertEquals(
            expected = listOf(2, 4),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(3)


        eventEmitter.emit(5)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
