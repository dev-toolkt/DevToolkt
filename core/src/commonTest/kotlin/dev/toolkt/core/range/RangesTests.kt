package dev.toolkt.core.range

import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RangesTests {
    @Test
    fun testNormalize() {
        val range = -2.0..3.14

        assertEqualsWithTolerance(
            expected = -0.019455252918287952,
            actual = range.normalize(-2.1),
        )

        assertEqualsWithTolerance(
            expected = 0.0,
            actual = range.normalize(-2.0),
        )

        assertEqualsWithTolerance(
            expected = 0.6291828793774318,
            actual = range.normalize(1.234),
        )

        assertEqualsWithTolerance(
            expected = 1.0,
            actual = range.normalize(3.14),
        )

        assertEqualsWithTolerance(
            expected = 1.1673151750972761,
            actual = range.normalize(4.0),
        )
    }

    @Test
    fun testOverlaps_emptyContained() {
        assertTrue(
            (3 until 3).overlaps(1 until 5)
        )
    }

    @Test
    fun testOverlaps_touchingRight() {
        assertFalse(
            (2 until 5).overlaps(5 until 8)
        )
    }

    @Test
    fun testOverlaps_touchingLeft() {
        assertFalse(
            (20 until 25).overlaps(5 until 20)
        )
    }

    @Test
    fun testOverlaps_missedLeft() {
        assertFalse(
            (4 until 5).overlaps(10 until 20)
        )
    }

    @Test
    fun testOverlaps_missedRight() {
        assertFalse(
            (4 until 5).overlaps(1 until 2)
        )
    }

    @Test
    fun testOverlaps_containing() {
        assertTrue(
            (2 until 20).overlaps(5 until 8)
        )
    }

    @Test
    fun testOverlaps_enclosed() {
        assertTrue(
            (4 until 5).overlaps(2 until 10)
        )
    }

    @Test
    fun testOverlaps_same() {
        assertTrue(
            (2 until 20).overlaps(2 until 20)
        )
    }

    @Test
    fun testOverlaps_touchingInsideLeft() {
        assertTrue(
            (5 until 20).overlaps(5 until 8)
        )
    }

    @Test
    fun testOverlaps_touchingInsideRight() {
        assertTrue(
            (10 until 20).overlaps(15 until 20)
        )
    }
}