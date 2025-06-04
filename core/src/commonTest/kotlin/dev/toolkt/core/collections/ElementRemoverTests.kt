package dev.toolkt.core.collections

import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ElementRemoverTests {
    @Test
    fun testInsert_newElement() {
        val mutableSet = mutableSetOf(1, 2, 3)

        val elementRemover = mutableSet.insert(4)

        assertNotNull(elementRemover)

        assertTrue(mutableSet.contains(4))
    }

    @Test
    fun testInsert_duplicate() {
        val mutableSet = mutableSetOf(1, 2, 3)

        val elementRemover = mutableSet.insert(2)

        assertNull(elementRemover)
    }

    @Test
    fun testInsert_remove() {
        val mutableSet = mutableSetOf(1, 2, 3)

        val elementRemover = mutableSet.insert(4)!!

        val wasRemoved = elementRemover.remove()

        assertTrue(wasRemoved)

        assertFalse(mutableSet.contains(4))

        val wasRemovedAgain = elementRemover.remove()

        assertFalse(wasRemovedAgain)
    }

    @Test
    fun testInsert_removeEffectively() {
        val mutableSet = mutableSetOf(1, 2, 3)

        val elementRemover = mutableSet.insert(4)!!

        elementRemover.removeEffectively()

        assertIs<IllegalStateException>(
            assertFails {
                elementRemover.removeEffectively()
            },
        )
    }

    @Test
    fun testInsertEffectively_newElement() {
        val mutableSet = mutableSetOf(1, 2, 3)

        val elementRemover = mutableSet.insertEffectively(4)

        assertNotNull(elementRemover)

        assertTrue(mutableSet.contains(4))
    }

    @Test
    fun testInsertEffectively_duplicate() {
        val mutableSet = mutableSetOf(1, 2, 3)

        assertIs<IllegalStateException>(
            assertFails {
                mutableSet.insertEffectively(3)
            },
        )
    }
}
