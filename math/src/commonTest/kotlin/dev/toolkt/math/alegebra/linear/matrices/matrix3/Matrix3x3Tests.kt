package dev.toolkt.math.alegebra.linear.matrices.matrix3

import dev.toolkt.math.algebra.linear.matrices.matrix3.Matrix3x3
import dev.toolkt.math.algebra.linear.vectors.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals

class Matrix3x3Tests {
    @Test
    fun testDeterminant1() {
        val matrix = Matrix3x3.rowMajor(
            row0 = Vector3(1.0, 2.0, 3.0),
            row1 = Vector3(4.0, 5.0, 6.0),
            row2 = Vector3(7.0, 8.0, 9.0),
        )

        val determinant = matrix.determinant

        assertEquals(
            expected = 0.0,
            actual = determinant,
        )
    }

    @Test
    fun testDeterminant2() {
        val matrix = Matrix3x3.rowMajor(
            row0 = Vector3(7.0, -7.0, 2.0),
            row1 = Vector3(-7.0, -5.0, -11.0),
            row2 = Vector3(2.0, -11.0, 13.0),
        )

        val determinant = matrix.determinant

        assertEquals(
            expected = -1611.0,
            actual = determinant,
        )
    }
}
