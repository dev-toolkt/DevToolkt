package dev.toolkt.reactive.future

import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class FutureMapTests {
    @Test
    fun testMap() {
        val futureCompleter = FutureCompleter<Int>()

        val mapFuture = futureCompleter.map { "$it" }

        val changesVerifier = EventStreamVerifier(
            eventStream = mapFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = mapFuture.currentState,
        )

        futureCompleter.complete(1)

        assertEquals(
            expected = listOf("1"),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = "1",
            ),
            actual = mapFuture.currentState,
        )
    }
}
