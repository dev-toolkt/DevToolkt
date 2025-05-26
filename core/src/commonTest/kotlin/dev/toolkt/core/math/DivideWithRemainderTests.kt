package dev.toolkt.core.math

import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class DivideWithRemainderTests {
    @Test
    fun testDivideByZero() {
        assertIs<IllegalArgumentException>(
            assertFails {
                1.234.divideWithRemainder(0)
            }
        )
    }

    @Test
    fun testDivideZeroBy() {
        val (q, r) = 0.0.divideWithRemainder(4)

        assertEquals(
            expected = 0,
            actual = q,
        )

        assertEqualsWithTolerance(
            expected = 0.0,
            actual = r,
        )
    }

    @Test
    fun testDivideByOne() {
        val (q, r) = 1.234.divideWithRemainder(1)

        assertEquals(
            expected = 1,
            actual = q,
        )

        assertEqualsWithTolerance(
            expected = 0.234,
            actual = r,
        )
    }


    @Test
    fun testDivideOneBy() {
        val (q, r) = 1.0.divideWithRemainder(2)

        assertEquals(
            expected = 0,
            actual = q,
        )

        assertEqualsWithTolerance(
            expected = 1.0,
            actual = r,
        )
    }

    @Test
    fun testDivideArbitrary() {
        val (q, r) = 12.456.divideWithRemainder(3)

        assertEquals(
            expected = 4,
            actual = q,
        )

        assertEqualsWithTolerance(
            expected = 0.456,
            actual = r,
        )
    }
}