package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
import dev.toolkt.reactive.EventStreamVerifier
import dev.toolkt.reactive.cell.MutableCell
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListSingleNotNullTests {
    @Test
    fun testSingleNotNull() {
        val mutableCell = MutableCell<Int?>(initialValue = 10)

        val singleReactiveList = ReactiveList.singleNotNull(
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

        mutableCell.set(null)

        assertEquals(
            expected = emptyList(),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = listOf(
                ReactiveList.Change.single(
                    update = ReactiveList.Change.Update.remove(
                        index = 0,
                    ),
                )!!,
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        mutableCell.set(null)

        assertEquals(
            expected = emptyList(),
            actual = singleReactiveList.currentElements,
        )

        assertEquals(
            expected = emptyList(),
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
                    update = ReactiveList.Change.Update.insert(
                        index = 0,
                        newElement = 30,
                    ),
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
