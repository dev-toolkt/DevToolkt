package dev.toolkt.math.alegebra.linear.vectors

import dev.toolkt.geometry.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Vector2Tests {

    @Test
    fun testMagnitude() {
        val vector = Vector2(3.0, 4.0)

        assertEquals(
            expected = 5.0,
            actual = vector.magnitude,
            absoluteTolerance = 1e-9,
        )
    }

    @Test
    fun testIsNormalized() {
        val normalizedVector = Vector2(0.6, 0.8)

        assertTrue(
            actual = normalizedVector.isNormalized(),
        )

        val nonNormalizedVector = Vector2(3.0, 4.0)

        assertFalse(
            actual = nonNormalizedVector.isNormalized(),
        )
    }

    @Test
    fun testNormalize() {
        val vector = Vector2(3.0, 4.0)

        val normalized = vector.normalize()

        assertTrue(
            actual = normalized.isNormalized(),
        )
    }

    @Test
    fun testNormalizeOrNull() {
        val zeroVector = Vector2(0.0, 0.0)

        assertNull(
            actual = zeroVector.normalizeOrNull(),
        )

        val vector = Vector2(3.0, 4.0)

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
        val vector1 = Vector2(1.0, 2.0)
        val vector2 = Vector2(3.0, 4.0)

        assertEquals(
            expected = 11.0,
            actual = vector1.dot(vector2),
            absoluteTolerance = 1e-9,
        )
    }

    @Test
    fun testCrossProduct() {
        val vector1 = Vector2(1.0, 2.0)
        val vector2 = Vector2(3.0, 4.0)

        assertEquals(
            expected = -2.0,
            actual = vector1.cross(vector2),
            absoluteTolerance = 1e-9,
        )
    }

    @Test
    fun testAddition() {
        val vector = Vector2(1.0, 2.0)

        val result = vector + Vector2(3.0, 4.0)

        assertEquals(
            expected = Vector2(4.0, 6.0),
            actual = result,
        )
    }

    @Test
    fun testSubtraction() {
        val vector1 = Vector2(5.0, 7.0)
        val vector2 = Vector2(3.0, 4.0)

        val result = vector1 - vector2

        assertEquals(
            expected = Vector2(2.0, 3.0),
            actual = result,
        )
    }

    @Test
    fun testMultiplicationWithScalar() {
        val vector = Vector2(1.0, 2.0)

        val result = vector * 2.0

        assertEquals(
            expected = Vector2(2.0, 4.0),
            actual = result,
        )
    }

    @Test
    fun testDivisionByScalar() {
        val vector = Vector2(2.0, 4.0)

        val result = vector / 2.0

        assertEquals(
            expected = Vector2(1.0, 2.0),
            actual = result,
        )
    }

    @Test
    fun testUnaryMinus() {
        val vector = Vector2(1.0, -2.0)

        val result = -vector

        assertEquals(
            expected = Vector2(-1.0, 2.0),
            actual = result,
        )
    }
}
