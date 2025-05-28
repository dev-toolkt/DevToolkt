package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListMapTests {
    @Test
    fun testMap() {
        val originalContent = listOf(
            0,
            10,
            20,
            30,
            40,
            50,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val mappedList = mutableReactiveList.map {
            -it
        }

        val changesVerifier = EventStreamVerifier(
            eventStream = mappedList.changes,
        )

        assertEquals(
            expected = listOf(
                0,
                -10,
                -20,
                -30,
                -40,
                -50,
            ),
            actual = mappedList.currentElements,
        )

        mutableReactiveList.set(
            index = 2,
            newValue = 21,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(2),
                        changedElements = listOf(-21),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = listOf(
                0,
                -10,
                -21,
                -30,
                -40,
                -50,
            ),
            actual = mappedList.currentElements,
        )

        mutableReactiveList.set(
            index = 4,
            newValue = 41,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(4),
                        changedElements = listOf(-41),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = listOf(
                0,
                -10,
                -21,
                -30,
                -41,
                -50,
            ),
            actual = mappedList.currentElements,
        )
    }
}
