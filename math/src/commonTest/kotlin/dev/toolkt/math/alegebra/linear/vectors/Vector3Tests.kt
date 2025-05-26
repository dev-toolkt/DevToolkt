package dev.toolkt.math.alegebra.linear.vectors

import dev.toolkt.math.algebra.linear.vectors.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Vector3Tests {
    @Test
    fun testMagnitude() {
        val vector = Vector3(1.0, 2.0, 2.0)

        assertEquals(
            expected = 3.0,
            actual = vector.magnitude,
            absoluteTolerance = 1e-9,
        )
    }

    @Test
    fun testIsNormalized() {
        val normalizedVector = Vector3(0.0, 0.0, 1.0)

        assertTrue(
            actual = normalizedVector.isNormalized(),
        )

        val nonNormalizedVector = Vector3(1.0, 2.0, 2.0)

        assertFalse(
            actual = nonNormalizedVector.isNormalized(),
        )
    }

    @Test
    fun testNormalize() {
        val vector = Vector3(1.0, 2.0, 2.0)

        val normalized = vector.normalize()

        assertTrue(
            actual = normalized.isNormalized(),
        )
    }

    @Test
    fun testNormalizeOrNull() {
        val zeroVector = Vector3(0.0, 0.0, 0.0)

        assertNull(
            actual = zeroVector.normalizeOrNull(),
        )

        val vector = Vector3(1.0, 2.0, 2.0)
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
        val vector1 = Vector3(1.0, 2.0, 3.0)
        val vector2 = Vector3(4.0, 5.0, 6.0)

        assertEquals(
            expected = 32.0,
            actual = vector1.dot(vector2),
            absoluteTolerance = 1e-9,
        )
    }

    @Test
    fun testCrossProduct() {
        val vector1 = Vector3(1.0, 0.0, 0.0)
        val vector2 = Vector3(0.0, 1.0, 0.0)

        assertEquals(
            expected = Vector3(0.0, 0.0, 1.0),
            actual = vector1.cross(vector2),
        )
    }

    @Test
    fun testAddition() {
        val vector = Vector3(1.0, 2.0, 3.0)

        val result = vector + Vector3(1.0, 1.0, 1.0)

        assertEquals(
            expected = Vector3(2.0, 3.0, 4.0),
            actual = result,
        )
    }

    @Test
    fun testSubtraction() {
        val vector1 = Vector3(5.0, 7.0, 9.0)
        val vector2 = Vector3(1.0, 2.0, 3.0)

        val result = vector1 - vector2

        assertEquals(
            expected = Vector3(4.0, 5.0, 6.0),
            actual = result,
        )
    }

    @Test
    fun testMultiplicationWithScalar() {
        val vector = Vector3(1.0, 2.0, 3.0)

        val result = vector * 2.0

        assertEquals(
            expected = Vector3(2.0, 4.0, 6.0),
            actual = result,
        )
    }

    @Test
    fun testDivisionByScalar() {
        val vector = Vector3(2.0, 4.0, 6.0)

        val result = vector / 2.0

        assertEquals(
            expected = Vector3(1.0, 2.0, 3.0),
            actual = result,
        )
    }

    @Test
    fun testUnaryMinus() {
        val vector = Vector3(1.0, -2.0, 3.0)

        val result = -vector

        assertEquals(
            expected = Vector3(-1.0, 2.0, -3.0),
            actual = result,
        )
    }
}
