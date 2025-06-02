package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.empty
import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class LoopedReactiveListTests {
    @Test
    @Ignore // FIXME
    fun testLoop_empty() {
        val originalContent = emptyList<Int>()

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val loopedReactiveList = LoopedReactiveList<Int>()

        val changesVerifier = EventStreamVerifier(
            eventStream = loopedReactiveList.changes,
        )

        assertEquals(
            expected = emptyList(),
            actual = loopedReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        loopedReactiveList.loop(mutableReactiveList)

        assertEquals(
            expected = emptyList(),
            actual = loopedReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableReactiveList.addAll(
            index = 0,
            elements = listOf(10, 20),
        )

        assertEquals(
            expected = listOf(
                10,
                20,
            ),
            actual = loopedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(0),
                        changedElements = listOf(10, 20),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testLoop_nonEmpty() {
        val originalContent = listOf(
            0,
            10,
            20,
        )

        val mutableReactiveList = MutableReactiveList(
            initialContent = originalContent,
        )

        val loopedReactiveList = LoopedReactiveList<Int>()

        val changesVerifier = EventStreamVerifier(
            eventStream = loopedReactiveList.changes,
        )

        assertEquals(
            expected = emptyList(),
            actual = loopedReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        loopedReactiveList.loop(mutableReactiveList)

        assertEquals(
            expected = originalContent,
            actual = loopedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(0),
                        changedElements = listOf(0, 10, 20),
                    ),
                ),
            ),
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
            ),
            actual = loopedReactiveList.currentElements,
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

        mutableReactiveList.addAll(
            index = 3,
            elements = listOf(30, 40),
        )

        assertEquals(
            expected = listOf(
                0,
                11,
                20,
                30,
                40,
            ),
            actual = loopedReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.empty(3),
                        changedElements = listOf(30, 40),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
