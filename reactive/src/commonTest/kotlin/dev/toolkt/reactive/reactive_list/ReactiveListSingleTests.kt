package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListSingleTests {
    @Test
    fun testSingle() {
        val mutableCell = MutableCell(initialValue = 10)

        val singleReactiveList = ReactiveList.single(
            element = mutableCell,
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = singleReactiveList.changes,
        )

        assertEquals(
            expected = listOf(10),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableCell.set(20)

        assertEquals(
            expected = listOf(20),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(0),
                        changedElements = listOf(20),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableCell.set(30)

        assertEquals(
            expected = listOf(30),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.change(
                        indexRange = IntRange.single(0),
                        changedElements = listOf(30),
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
