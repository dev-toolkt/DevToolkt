package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMergeTests {
    @Test
    fun testMerge_doubleNever() {
        val mergeStream = EventStream.merge(
            source1 = NeverEventStream,
            source2 = NeverEventStream,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mergeStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMerge_singleNever1() {
        val eventEmitter = EventEmitter<Int>()

        val mergeStream = EventStream.merge(
            source1 = eventEmitter,
            source2 = NeverEventStream,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mergeStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = listOf(1),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = listOf(2),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMerge_singleNever2() {
        val eventEmitter = EventEmitter<Int>()

        val mergeStream = EventStream.merge(
            source1 = NeverEventStream,
            source2 = eventEmitter,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mergeStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = listOf(1),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = listOf(2),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMerge_same() {
        val eventEmitter = EventEmitter<Int>()

        val mergeStream = EventStream.merge(
            source1 = eventEmitter,
            source2 = eventEmitter,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mergeStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = listOf(1, 1),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = listOf(2, 2),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMerge_sameMapped() {
        val eventEmitter = EventEmitter<Int>()

        val mergeStream = EventStream.merge(
            source1 = eventEmitter.map { it * 10 },
            source2 = eventEmitter.map { it * -10 },
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mergeStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = listOf(10, -10),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = listOf(20, -20),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMerge_different() {
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2 = EventEmitter<Int>()

        val mergeStream = EventStream.merge(
            source1 = eventEmitter1,
            source2 = eventEmitter2,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mergeStream,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter1.emit(1)

        assertEquals(
            expected = listOf(1),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter1.emit(2)
        eventEmitter2.emit(-2)

        assertEquals(
            expected = listOf(2, -2),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter2.emit(-3)

        assertEquals(
            expected = listOf(-3),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
