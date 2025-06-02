package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMapTests {
    @Test
    fun testMap() {
        val eventEmitter = EventEmitter<Int>()

        val mappedStream = eventEmitter.map { "$it" }

        val streamVerifier = EventStreamVerifier(
            eventStream = mappedStream,
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = listOf("1"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = listOf("2"),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
