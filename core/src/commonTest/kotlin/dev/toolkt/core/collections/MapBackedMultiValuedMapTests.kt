package dev.toolkt.core.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapBackedMultiValuedMapTests {
    @Test
    fun testNew() {
        val mutableMultiValuedMap = MutableMultiValuedMap.new<Int, String>()

        assertFalse(mutableMultiValuedMap.containsKey(10))
        assertFalse(mutableMultiValuedMap.containsKey(20))
        assertFalse(mutableMultiValuedMap.containsKey(30))

        assertFalse(mutableMultiValuedMap.containsMapping(10, "A"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "B"))
        assertFalse(mutableMultiValuedMap.containsMapping(30, "B"))

        assertEquals(
            expected = 0,
            actual = mutableMultiValuedMap.size,
        )

        assertEquals(
            expected = emptyMap(),
            actual = mutableMultiValuedMap.asMap(),
        )

        val collection = mutableMultiValuedMap.get(10)

        assertTrue(collection.isEmpty())

        assertTrue(mutableMultiValuedMap.isEmpty())
        assertTrue(mutableMultiValuedMap.keys.isEmpty())
        assertTrue(mutableMultiValuedMap.entries.isEmpty())
    }

    @Test
    fun newFromMap() {
        val mutableMultiValuedMap = MutableMultiValuedMap.newFromMap(
            map = mutableMapOf(
                10 to mutableSetOf("A"),
                20 to mutableSetOf("A", "B"),
                30 to mutableSetOf("C", "D"),
            ),
        )

        assertTrue(mutableMultiValuedMap.containsKey(10))
        assertTrue(mutableMultiValuedMap.containsKey(20))
        assertTrue(mutableMultiValuedMap.containsKey(30))

        assertFalse(mutableMultiValuedMap.containsKey(5))
        assertFalse(mutableMultiValuedMap.containsKey(25))
        assertFalse(mutableMultiValuedMap.containsKey(35))

        assertTrue(mutableMultiValuedMap.containsMapping(10, "A"))
        assertFalse(mutableMultiValuedMap.containsMapping(10, "B"))
        assertFalse(mutableMultiValuedMap.containsMapping(10, "C"))
        assertFalse(mutableMultiValuedMap.containsMapping(10, "D"))

        assertTrue(mutableMultiValuedMap.containsMapping(20, "A"))
        assertTrue(mutableMultiValuedMap.containsMapping(20, "B"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "C"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "D"))

        assertFalse(mutableMultiValuedMap.containsMapping(30, "A"))
        assertFalse(mutableMultiValuedMap.containsMapping(30, "B"))
        assertTrue(mutableMultiValuedMap.containsMapping(30, "C"))
        assertTrue(mutableMultiValuedMap.containsMapping(30, "D"))

        assertEquals(
            expected = 5,
            actual = mutableMultiValuedMap.size,
        )

        assertEquals(
            expected = mapOf(
                10 to setOf("A"),
                20 to setOf("A", "B"),
                30 to setOf("C", "D"),
            ),
            actual = mutableMultiValuedMap.asMap(),
        )

        assertEquals(
            expected = setOf("A", "B"),
            actual = mutableMultiValuedMap.get(20)
        )

        assertFalse(mutableMultiValuedMap.isEmpty())

        assertEquals(
            expected = setOf(10, 20, 30),
            actual = mutableMultiValuedMap.keys,
        )

        assertEquals(
            expected = listOf(
                Pair(10, "A"),
                Pair(20, "A"),
                Pair(20, "B"),
                Pair(30, "C"),
                Pair(30, "D"),
            ),
            actual = mutableMultiValuedMap.entries.map { it.toPair() },
        )
    }

    @Test
    fun testPut() {
        val mutableMultiValuedMap = MutableMultiValuedMap.new<Int, String>()

        mutableMultiValuedMap.put(10, "A")

        assertTrue(mutableMultiValuedMap.containsKey(10))
        assertFalse(mutableMultiValuedMap.containsKey(20))

        assertTrue(mutableMultiValuedMap.containsMapping(10, "A"))
        assertFalse(mutableMultiValuedMap.containsMapping(10, "B"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "A"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "B"))

        assertTrue(mutableMultiValuedMap.containsValue("A"))
        assertFalse(mutableMultiValuedMap.containsValue("B"))
    }

    @Test
    fun testRemove() {
        val mutableMultiValuedMap = MutableMultiValuedMap.newFromMap(
            map = mutableMapOf(
                10 to mutableSetOf("A"),
                20 to mutableSetOf("A", "B"),
                30 to mutableSetOf("C", "D"),
            ),
        )

        mutableMultiValuedMap.remove(20)

        assertTrue(mutableMultiValuedMap.containsKey(10))
        assertFalse(mutableMultiValuedMap.containsKey(20))

        assertTrue(mutableMultiValuedMap.containsMapping(10, "A"))
        assertTrue(mutableMultiValuedMap.containsMapping(30, "C"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "A"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "B"))

        assertTrue(mutableMultiValuedMap.containsValue("A"))
        assertFalse(mutableMultiValuedMap.containsValue("B"))

        assertEquals(
            expected = 3,
            actual = mutableMultiValuedMap.size,
        )

        assertEquals(
            expected = setOf(10, 30),
            actual = mutableMultiValuedMap.keys,
        )
    }

    @Test
    fun testRemoveMapping() {
        val mutableMultiValuedMap = MutableMultiValuedMap.newFromMap(
            map = mutableMapOf(
                10 to mutableSetOf("A"),
                20 to mutableSetOf("A", "B"),
                30 to mutableSetOf("C", "D"),
            ),
        )

        mutableMultiValuedMap.removeMapping(20, "A")

        assertTrue(mutableMultiValuedMap.containsKey(10))
        assertTrue(mutableMultiValuedMap.containsKey(20))

        assertTrue(mutableMultiValuedMap.containsMapping(10, "A"))
        assertTrue(mutableMultiValuedMap.containsMapping(30, "C"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "A"))
        assertTrue(mutableMultiValuedMap.containsMapping(20, "B"))

        assertTrue(mutableMultiValuedMap.containsValue("A"))
        assertTrue(mutableMultiValuedMap.containsValue("B"))

        assertEquals(
            expected = 4,
            actual = mutableMultiValuedMap.size,
        )

        assertEquals(
            expected = setOf(10, 20, 30),
            actual = mutableMultiValuedMap.keys,
        )

        mutableMultiValuedMap.removeMapping(20, "B")

        assertTrue(mutableMultiValuedMap.containsKey(10))
        assertFalse(mutableMultiValuedMap.containsKey(20))

        assertTrue(mutableMultiValuedMap.containsMapping(10, "A"))
        assertTrue(mutableMultiValuedMap.containsMapping(30, "C"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "A"))
        assertFalse(mutableMultiValuedMap.containsMapping(20, "B"))

        assertTrue(mutableMultiValuedMap.containsValue("A"))
        assertFalse(mutableMultiValuedMap.containsValue("B"))

        assertEquals(
            expected = 3,
            actual = mutableMultiValuedMap.size,
        )

        assertEquals(
            expected = setOf(10, 30),
            actual = mutableMultiValuedMap.keys,
        )
    }

    @Test
    fun testClear() {
        val mutableMultiValuedMap = MutableMultiValuedMap.newFromMap(
            map = mutableMapOf(
                10 to mutableSetOf("A"),
                20 to mutableSetOf("A", "B"),
                30 to mutableSetOf("C", "D"),
            ),
        )

        mutableMultiValuedMap.clear()

        assertEquals(
            expected = 0,
            actual = mutableMultiValuedMap.size,
        )

        assertEquals(
            expected = emptySet(),
            actual = mutableMultiValuedMap.keys,
        )

        assertTrue(mutableMultiValuedMap.isEmpty())
    }
}
