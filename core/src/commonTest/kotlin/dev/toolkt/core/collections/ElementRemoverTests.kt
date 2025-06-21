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
    fun testInsertWeak_newElement() {
        val mutableSet = mutableSetOf(1, 2, 3)

        val elementRemover = mutableSet.insert(4)

        assertNotNull(elementRemover)

        assertTrue(mutableSet.contains(4))
    }

    @Test
    fun testInsert_newMapping() {
        val mutableMap = mutableMultiValuedMapOf(
            1 to "A1",
            2 to "B1",
            2 to "B2",
            3 to "C1",
            4 to "D1",
            4 to "D2",
        )

        val elementRemover1 = mutableMap.insert(3, "C2")

        assertNotNull(elementRemover1)

        assertTrue(mutableMap.containsMapping(3, "C2"))

        val elementRemover2 = mutableMap.insert(5, "E1")

        assertNotNull(elementRemover2)

        assertTrue(mutableMap.containsMapping(5, "E1"))
    }

    @Test
    fun testInsertWeak_newMapping() {
        val mutableMap = mutableMultiValuedMapOf(
            1 to "A1",
            2 to "B1",
            2 to "B2",
            3 to "C1",
            4 to "D1",
            4 to "D2",
        )

        val elementRemover1 = mutableMap.insert(3, "C2")

        assertNotNull(elementRemover1)

        assertTrue(mutableMap.containsMapping(3, "C2"))

        val elementRemover2 = mutableMap.insert(5, "E1")

        assertNotNull(elementRemover2)

        assertTrue(mutableMap.containsMapping(5, "E1"))
    }

    @Test
    fun testInsert_duplicate() {
        val mutableSet = mutableSetOf(1, 2, 3)

        val elementRemover = mutableSet.insert(2)

        assertNull(elementRemover)
    }

    @Test
    fun testInsert_duplicateMapping() {
        val mutableMap = mutableMultiValuedMapOf(
            1 to "A1",
            2 to "B1",
            2 to "B2",
            3 to "C1",
            4 to "D1",
            4 to "D2",
        )

        val elementRemover = mutableMap.insert(3, "C2")

        assertNotNull(elementRemover)

        assertTrue(mutableMap.containsMapping(3, "C2"))
    }

    @Test
    fun testInsert_mapping_remove() {
        val mutableMap = mutableMultiValuedMapOf(
            1 to "A1",
            2 to "B1",
            2 to "B2",
            3 to "C1",
            4 to "D1",
            4 to "D2",
        )

        val elementRemover = mutableMap.insert(3, "C2")!!

        val wasRemoved = elementRemover.remove()

        assertTrue(wasRemoved)

        assertFalse(mutableMap.containsMapping(3, "C2"))

        val wasRemovedAgain = elementRemover.remove()

        assertFalse(wasRemovedAgain)
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
    fun testRemoveEffectively() {
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
    fun testInsertEffectively_newMapping() {
        val mutableMap = mutableMultiValuedMapOf(
            1 to "A1",
            2 to "B1",
            2 to "B2",
            3 to "C1",
            4 to "D1",
            4 to "D2",
        )

        val elementRemover = mutableMap.insertEffectively(3, "C2")

        assertNotNull(elementRemover)

        assertTrue(mutableMap.containsMapping(3, "C2"))
    }

    @Test
    fun testInsertEffectively_duplicateElement() {
        val mutableSet = mutableSetOf(1, 2, 3)

        assertIs<IllegalStateException>(
            assertFails {
                mutableSet.insertEffectively(3)
            },
        )
    }

    @Test
    fun testInsertEffectively_duplicateMapping() {
        val mutableMap = mutableMultiValuedMapOf(
            1 to "A1",
            2 to "B1",
            2 to "B2",
            3 to "C1",
            4 to "D1",
            4 to "D2",
        )

        assertIs<IllegalStateException>(
            assertFails {
                mutableMap.insertEffectively(3, "C1")
            },
        )
    }

    @Test
    fun testInsertEffectivelyWeak_newElement() {
        val object1 = object {}
        val object2 = object {}
        val object3 = object {}

        val mutableSet = mutableSetOf(object1, object2)

        val elementRemover = mutableSet.insertEffectivelyWeak(object3)

        assertNotNull(elementRemover)

        assertTrue(mutableSet.contains(object3))
    }

    @Test
    fun testInsertEffectivelyWeak_duplicate() {
        val mutableSet = mutableSetOf(1, 2, 3)

        assertIs<IllegalStateException>(
            assertFails {
                mutableSet.insertEffectivelyWeak(3)
            },
        )
    }
}
