package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class MutableReactiveListTests {
    @Test
    fun testSet() {
        val originalContent = listOf(
            0,
            10,
            20,
            30,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mutableReactiveList.changes,
        )

        assertEquals(
            expected = originalContent,
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.set(
            index = 1,
            newValue = 11,
        )

        assertEquals(
            expected = listOf(
                0,
                11,
                20,
                30,
            ),
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(1),
                        changedElements = listOf(11),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.set(
            index = 3,
            newValue = 31,
        )

        assertEquals(
            expected = listOf(
                0,
                11,
                20,
                31,
            ),
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(3),
                        changedElements = listOf(31),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testRemoveAt() {
        val originalContent = listOf(
            0,
            10,
            20,
            30,
            40,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mutableReactiveList.changes,
        )

        assertEquals(
            expected = originalContent,
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.removeAt(
            index = 3,
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                20,
                40,
            ),
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.remove(
                        index = 3,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.removeAt(
            index = 2,
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                40,
            ),
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.remove(
                        index = 2,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }


    @Test
    fun testAddAll() {
        val originalContent = listOf(
            0,
            10,
            20,
            30,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = mutableReactiveList.changes,
        )

        assertEquals(
            expected = originalContent,
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.addAll(
            index = 2,
            elements = listOf(11, 12, 13),
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                11,
                12,
                13,
                20,
                30,
            ),
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.insert(
                        index = 2,
                        newElements = listOf(11, 12, 13),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.addAll(
            index = 4,
            elements = listOf(-12, -13),
        )

        assertEquals(
            expected = listOf(
                0,
                10,
                11,
                12,
                -12,
                -13,
                13,
                20,
                30,
            ),
            actual = mutableReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.insert(
                        index = 2,
                        newElements = listOf(-12, -13),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
