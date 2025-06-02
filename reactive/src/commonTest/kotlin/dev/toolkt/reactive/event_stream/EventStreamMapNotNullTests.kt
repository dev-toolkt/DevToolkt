package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMapNotNullTests {
    @Test
    fun testMapNotNull() {
        val eventEmitter = EventEmitter<Int>()

        val mappedStream = eventEmitter.mapNotNull {
            when {
                it % 2 == 0 -> "$it"
                else -> null
            }
        }

        val streamVerifier = EventStreamVerifier(
            eventStream = mappedStream,
        )

        eventEmitter.emit(2)

        eventEmitter.emit(3)

        eventEmitter.emit(5)

        eventEmitter.emit(4)

        assertEquals(
            expected = listOf("2", "4"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(3)

        eventEmitter.emit(5)

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
