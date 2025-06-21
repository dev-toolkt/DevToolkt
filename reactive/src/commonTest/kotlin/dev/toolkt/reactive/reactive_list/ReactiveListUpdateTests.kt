package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.empty
import dev.toolkt.reactive.reactive_list.ReactiveList.Change.Update
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListUpdateTests {
    @Test
    fun testUpdateApplyTo() {
        val originalList = emptyList<Int>()

        val mutableList = originalList.toMutableList()

        val update = Update(
            indexRange = IntRange.empty(0),
            updatedElements = listOf(10, 20),
        )

        update.applyTo(mutableList = mutableList)

        assertEquals(
            expected = listOf(10, 20),
            actual = mutableList,
        )
    }
}
