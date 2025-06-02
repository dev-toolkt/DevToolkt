package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.future.Future
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamNextTests {
    @Test
    fun testNext() {
        val eventEmitter = EventEmitter<Int>()

        val nextFuture = eventEmitter.next()

        val changesVerifier = EventStreamVerifier(
            eventStream = nextFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = nextFuture.currentState,
        )

        eventEmitter.emit(10)

        assertEquals(
            expected = listOf(10),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = nextFuture.currentState,
        )

        eventEmitter.emit(20)

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = nextFuture.currentState,
        )
    }
}
