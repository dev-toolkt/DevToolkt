package dev.toolkt.core.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull

class EntryRemoverTests {
    @Test
    fun testInsertEffectively_newElement() {
        val mutableMap = mutableMapOf(1 to "A", 2 to "B", 3 to "C")

        mutableMap.insertEffectively(4, "D")

        assertEquals(
            expected = "D",
            actual = mutableMap[4],
        )
    }

    @Test
    fun testInsertEffectivelyWeak_newElement() {
        val object1 = object {}
        val object2 = object {}
        val object3 = object {}

        val mutableMap = mutableMapOf(
            object1 to "A",
            object2 to "B",
        )

        mutableMap.insertEffectivelyWeak(object3, "D")

        assertEquals(
            expected = "D",
            actual = mutableMap[object3],
        )
    }

    @Test
    fun testInsertEffectively_duplicate() {
        val mutableMap = mutableMapOf(1 to "A", 2 to "B", 3 to "C")

        assertIs<IllegalStateException>(
            assertFails {
                mutableMap.insertEffectively(2, "B")
            },
        )

        assertIs<IllegalStateException>(
            assertFails {
                mutableMap.insertEffectively(2, "X")
            },
        )
    }

    @Test
    fun testInsertEffectivelyWeak_duplicate() {
        val mutableMap = mutableMapOf(1 to "A", 2 to "B", 3 to "C")

        assertIs<IllegalStateException>(
            assertFails {
                mutableMap.insertEffectively(2, "B")
            },
        )

        assertIs<IllegalStateException>(
            assertFails {
                mutableMap.insertEffectively(2, "X")
            },
        )
    }

    @Test
    fun testInsertEffectively_remove() {
        val mutableMap = mutableMapOf(1 to "A", 2 to "B", 3 to "C")

        val elementRemover = mutableMap.insertEffectively(4, "D")

        val removedValue = elementRemover.remove()

        assertEquals(
            expected = "D",
            actual = removedValue,
        )

        assertFalse(mutableMap.contains(4))

        val removedValueAgain = elementRemover.remove()

        assertNull(removedValueAgain)
    }

    @Test
    fun testInsertEffectively_removeEffectively() {
        val mutableMap = mutableMapOf(1 to "A", 2 to "B", 3 to "C")

        val elementRemover = mutableMap.insertEffectively(4, "D")

        elementRemover.removeEffectively()

        assertIs<IllegalStateException>(
            assertFails {
                elementRemover.removeEffectively()
            },
        )
    }
}
