package dev.toolkt.reactive.future

import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class FutureHoldTests {
    @Test
    fun testHold() {
        val futureCompleter = FutureCompleter<Int>()

        val holdCell = futureCompleter.hold(-1)

        val newValuesVerifier = EventStreamVerifier(
            eventStream = holdCell.newValues,
        )

        assertEquals(
            expected = -1,
            actual = holdCell.currentValue,
        )

        futureCompleter.complete(1)

        assertEquals(
            expected = listOf(1),
            actual = newValuesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = 1,
            actual = holdCell.currentValue,
        )
    }
}
