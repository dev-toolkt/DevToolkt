package dev.toolkt.reactive.event_stream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

/**
 * Tests of the fundamental [EventStream] operations
 */
class EventStreamTests {
    @Test
    fun testListen() {
        val eventEmitter = EventEmitter<Int>()

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        val buffer = mutableListOf<String>()

        fun add(element: String) {
            buffer.add(element)
        }

        val subscription = dependentStream.listen(::add)

        assertEquals(
            expected = emptyList(),
            actual = buffer,
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = listOf("1"),
            actual = buffer,
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer,
        )

        subscription.cancel()

        eventEmitter.emit(3)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer,
        )
    }

    @Test
    fun testListenTwice() {
        val eventEmitter = EventEmitter<Int>()

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        @Suppress("unused")
        fun handle(element: String) {
        }

        dependentStream.listen(::handle)

        assertIs<IllegalStateException>(
            assertFails {
                dependentStream.listen(::handle)
            },
        )
    }

    private class Buffer {
        val list = mutableListOf<String>()
    }

    @Test
    fun testListenWeak() {
        val eventEmitter = EventEmitter<Int>()

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        val buffer = Buffer()

        fun add(
            buffer: Buffer,
            element: String,
        ) {
            buffer.list.add(element)
        }

        val subscription = dependentStream.listenWeak(
            target = buffer, listener = ::add
        )

        assertEquals(
            expected = emptyList(),
            actual = buffer.list,
        )

        eventEmitter.emit(1)

        assertEquals(
            expected = listOf("1"),
            actual = buffer.list,
        )

        eventEmitter.emit(2)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer.list,
        )

        subscription.cancel()

        eventEmitter.emit(3)

        assertEquals(
            expected = listOf("1", "2"),
            actual = buffer.list,
        )
    }

    @Test
    fun testListenWeak_sameListener_sameTarget() {
        val eventEmitter = EventEmitter<Int>()

        val target = object {}

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        fun handle(target: Any, element: String) {
        }

        dependentStream.listenWeak(
            target = target,
            listener = ::handle,
        )

        assertIs<IllegalStateException>(
            assertFails {
                dependentStream.listenWeak(
                    target = target,
                    listener = ::handle,
                )
            },
        )
    }

    @Test
    fun testListenWeak_differentListeners_sameTarget() {
        val eventEmitter = EventEmitter<Int>()

        val target = object {}

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        @Suppress("unused")
        fun handle1(target: Any, element: String) {
        }

        @Suppress("unused")
        fun handle2(target: Any, element: String) {
        }

        dependentStream.listenWeak(
            target = target,
            listener = ::handle1,
        )

        dependentStream.listenWeak(
            target = target,
            listener = ::handle2,
        )
    }

    @Test
    fun testListenWeak_sameListener_differentTargets() {
        val eventEmitter = EventEmitter<Int>()

        val target1 = object {}

        val target2 = object {}

        // Create a dependent stream to make the system non-trivial
        val dependentStream = eventEmitter.map { it.toString() }

        @Suppress("unused")
        fun handle(target: Any, element: String) {
        }

        dependentStream.listenWeak(
            target = target1,
            listener = ::handle,
        )

        dependentStream.listenWeak(
            target = target2,
            listener = ::handle,
        )
    }
}
