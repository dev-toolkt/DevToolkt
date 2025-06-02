package dev.toolkt.reactive.future

import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import kotlin.test.Test
import kotlin.test.assertEquals

class FutureDivertHoldTests {
    @Test
    fun testDivertHold() {
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2 = EventEmitter<Int>()

        val futureCompleter = FutureCompleter<EventStream<Int>>()

        val divertHoldStream = futureCompleter.divertHold(
            initialEventStream = eventEmitter1,
        )

        val streamVerifier = EventStreamVerifier(
            eventStream = divertHoldStream,
        )

        eventEmitter1.emit(-11)
        eventEmitter2.emit(11)

        assertEquals(
            expected = listOf(-11),
            actual = streamVerifier.removeReceivedEvents(),
        )

        futureCompleter.complete(eventEmitter2)

        eventEmitter1.emit(-12)
        eventEmitter2.emit(12)

        assertEquals(
            expected = listOf(12),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
