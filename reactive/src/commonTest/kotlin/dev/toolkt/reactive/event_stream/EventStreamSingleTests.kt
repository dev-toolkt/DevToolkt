package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamSingleTests {
    @Test
    fun testSingle() {
        val eventEmitter = EventEmitter<Int>()

        val nextStream = eventEmitter.single()

        val streamVerifier = EventStreamVerifier(
            eventStream = nextStream,
        )

        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(20)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(30)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
