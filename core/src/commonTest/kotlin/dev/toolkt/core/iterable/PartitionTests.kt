package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PartitionTests {
    @Test
    fun testPartitionAt() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.partitionAt(2)
        assertEquals(Partitioned(listOf(1, 2), 3, listOf(4, 5)), result)

        val outOfBoundsResult = list.partitionAt(10)
        assertNull(outOfBoundsResult)

        val negativeIndexResult = list.partitionAt(-1)
        assertNull(negativeIndexResult)
    }

    @Test
    fun testPartitionAtCenter() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.partitionAtCenter()
        assertEquals(Partitioned(listOf(1, 2), 3, listOf(4, 5)), result)

        val emptyListResult = emptyList<Int>().partitionAtCenter()
        assertNull(emptyListResult)
    }
}
