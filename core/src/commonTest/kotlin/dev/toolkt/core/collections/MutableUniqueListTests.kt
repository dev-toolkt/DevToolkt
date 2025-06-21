package dev.toolkt.core.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MutableUniqueListTests {
    private enum class Fruit {
        Apple, Raspberry, Banana, Orange, Kiwi, Mango, Pineapple, Strawberry, Watermelon, Grape,
    }

    @Test
    fun testInitial() {
        val mutableUniqueList = MutableUniqueList<Fruit>()

        mutableUniqueList.verifyContent(
            elements = emptyList(),
        )
    }

    @Test
    fun testSet() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Pineapple,
        )

        mutableUniqueList[1] = Fruit.Raspberry

        mutableUniqueList.verifyContent(
            Fruit.Banana,
            Fruit.Raspberry,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Pineapple,
        )
    }

    @Test
    fun testSet_duplicate() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Watermelon,
        )

        assertIs<IllegalStateException>(
            assertFails {
                mutableUniqueList[3] = Fruit.Orange
            },
        )
    }

    @Test
    fun testAdd_append_empty() {
        val mutableUniqueList = MutableUniqueList<Fruit>()

        assertTrue(
            actual = mutableUniqueList.add(
                Fruit.Grape,
            )
        )

        mutableUniqueList.verifyContent(
            Fruit.Grape,
        )
    }

    @Test
    fun testAdd_append_nonEmpty() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Grape,
            Fruit.Strawberry,
        )

        assertTrue(
            actual = mutableUniqueList.add(
                Fruit.Orange,
            ),
        )

        mutableUniqueList.verifyContent(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Orange,
        )
    }

    @Test
    fun testAdd_append_duplicate() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Apple,
            Fruit.Mango,
        )

        assertFalse(
            actual = mutableUniqueList.add(
                Fruit.Apple,
            ),
        )

        mutableUniqueList.verifyContent(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Apple,
            Fruit.Mango,
        )
    }

    @Test
    fun testAdd_atIndex_first() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Orange,
        )

        mutableUniqueList.add(
            index = 0,
            element = Fruit.Apple,
        )

        mutableUniqueList.verifyContent(
            Fruit.Apple,
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Orange,
        )
    }


    @Test
    fun testAdd_atIndex_middle() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Watermelon,
            Fruit.Orange,
        )

        mutableUniqueList.add(
            index = 2,
            element = Fruit.Apple,
        )

        mutableUniqueList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Apple,
            Fruit.Watermelon,
            Fruit.Orange,
        )
    }

    @Test
    fun testAdd_atIndex_duplicate() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Watermelon,
            Fruit.Orange,
        )

        assertIs<IllegalStateException>(
            assertFails {
                mutableUniqueList.add(
                    index = 2,
                    Fruit.Watermelon,
                )
            },
        )
    }

    @Test
    fun testAdd_atIndex_last() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableUniqueList.add(
            index = 2,
            element = Fruit.Apple,
        )

        mutableUniqueList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Apple,
            Fruit.Orange,
        )
    }


    @Test
    fun testAdd_atIndex_onePastLast() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableUniqueList.add(
            index = 3,
            element = Fruit.Apple,
        )

        mutableUniqueList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
            Fruit.Apple,
        )
    }


    @Test
    fun testRemoveAt_first() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableUniqueList.removeAt(0)

        mutableUniqueList.verifyContent(
            Fruit.Grape,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemoveAt_middle() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )

        mutableUniqueList.removeAt(1)

        mutableUniqueList.verifyContent(
            Fruit.Strawberry,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemoveAt_last() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableUniqueList.removeAt(2)

        mutableUniqueList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
        )
    }

    @Test
    fun testRemoveAt_pastLast() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        assertIs<IndexOutOfBoundsException>(
            assertFails {
                mutableUniqueList.removeAt(3)
            },
        )
    }

    @Test
    fun testRemove_contained() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        assertTrue(
            actual = mutableUniqueList.remove(
                Fruit.Strawberry,
            ),
        )

        mutableUniqueList.verifyContent(
            Fruit.Grape,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemove_nonContained() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )

        assertFalse(
            actual = mutableUniqueList.remove(
                Fruit.Apple,
            ),
        )

        mutableUniqueList.verifyContent(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )
    }

    @Test
    fun testAsSet() {
        val mutableUniqueList = mutableUniqueListOf(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
            Fruit.Watermelon,
        )

        assertEquals(
            expected = setOf(
                Fruit.Strawberry,
                Fruit.Kiwi,
                Fruit.Orange,
                Fruit.Watermelon,
            ),
            actual = mutableUniqueList.asSet,
        )
    }
}
