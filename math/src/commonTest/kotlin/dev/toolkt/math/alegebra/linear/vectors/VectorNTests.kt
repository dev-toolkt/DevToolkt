package dev.toolkt.math.alegebra.linear.vectors

import dev.toolkt.math.algebra.linear.vectors.VectorN
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VectorNTests {
    @Test
    fun testMagnitude() {
        val vector = VectorN(listOf(1.0, 2.0, 2.0))

        assertEqualsWithTolerance(
            expected = 3.0,
            actual = vector.magnitude,
        )
    }

    @Test
    fun testIsNormalized() {
        val normalizedVector = VectorN(listOf(0.5, 0.5, 0.5, 0.5))

        assertTrue(
            actual = normalizedVector.isNormalized(),
        )

        val nonNormalizedVector = VectorN(listOf(1.0, 2.0, 2.0))

        assertFalse(
            actual = nonNormalizedVector.isNormalized(),
        )
    }

    @Test
    fun testNormalize() {
        val vector = VectorN(listOf(1.0, 2.0, 2.0))

        val normalized = vector.normalize()

        assertTrue(
            actual = normalized.isNormalized(),
        )
    }

    @Test
    fun testNormalizeOrNull() {
        val zeroVector = VectorN(listOf(0.0, 0.0, 0.0))

        assertNull(
            actual = zeroVector.normalizeOrNull(),
        )

        val vector = VectorN(listOf(1.0, 2.0, 2.0))

        val normalized = vector.normalizeOrNull()

        assertNotNull(
            actual = normalized,
        )

        assertTrue(
            actual = normalized.isNormalized(),
        )
    }

    @Test
    fun testDotProduct() {
        val vector1 = VectorN(listOf(1.0, 2.0, 3.0))
        val vector2 = VectorN(listOf(4.0, 5.0, 6.0))

        assertEquals(
            expected = 32.0,
            actual = vector1.dot(vector2),
        )
    }

    @Test
    fun testAddition() {
        val vector = VectorN(listOf(1.0, 2.0, 3.0))

        val result = vector + 1.0

        assertEquals(
            expected = VectorN(listOf(2.0, 3.0, 4.0)),
            actual = result,
        )
    }

    @Test
    fun testSubtraction() {
        val vector1 = VectorN(listOf(5.0, 7.0, 9.0))
        val vector2 = VectorN(listOf(1.0, 2.0, 3.0))

        val result = vector1 - vector2

        assertEquals(
            expected = VectorN(listOf(4.0, 5.0, 6.0)),
            actual = result,
        )
    }

    @Test
    fun testMultiplicationWithScalar() {
        val vector = VectorN(listOf(1.0, 2.0, 3.0))

        val result = vector * 2.0

        assertEquals(
            expected = VectorN(listOf(2.0, 4.0, 6.0)),
            actual = result,
        )
    }

    @Test
    fun testDivisionByScalar() {
        val vector = VectorN(listOf(2.0, 4.0, 6.0))

        val result = vector / 2.0

        assertEquals(
            expected = VectorN(listOf(1.0, 2.0, 3.0)),
            actual = result,
        )
    }

    @Test
    fun testUnaryMinus() {
        val vector = VectorN(listOf(1.0, -2.0, 3.0))

        val result = -vector

        assertEquals(
            expected = VectorN(listOf(-1.0, 2.0, -3.0)),
            actual = result,
        )
    }
}
