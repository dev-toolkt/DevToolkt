package dev.toolkt.reactive

import dev.toolkt.reactive.event_stream.EventEmitter
import kotlin.test.Test
import kotlin.test.assertEquals

class EventEmitterTests {
    @Test
    fun testEventEmitter() {
        val eventEmitter = EventEmitter<String>()

        val changesVerifier = EventStreamVerifier(
            eventStream = eventEmitter,
        )

        eventEmitter.emit("Hello")
        eventEmitter.emit("World")

        assertEquals(
            expected = listOf(
                "Hello",
                "World",
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit("Bye")

        assertEquals(
            expected = listOf(
                "Bye",
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
