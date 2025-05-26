package dev.toolkt.math.alegebra.linear.matrices.matrix3

import dev.toolkt.math.algebra.linear.matrices.matrix3.Matrix3x3
import dev.toolkt.math.algebra.linear.vectors.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals

class EagerMatrix3x3Tests {
    @Test
    fun testApply() {
        val matrix = Matrix3x3.rowMajor(
            row0 = Vector3(1.0, 2.0, 3.0),
            row1 = Vector3(4.0, 5.0, 6.0),
            row2 = Vector3(7.0, 8.0, 9.0),
        )

        val vector = Vector3(1.0, 0.0, -1.0)
        val result = matrix.apply(vector)

        assertEquals(
            expected = Vector3(-2.0, -2.0, -2.0),
            actual = result,
        )
    }

    @Test
    fun testTimes_identity() {
        val matrixA = Matrix3x3.rowMajor(
            row0 = Vector3(1.0, 2.0, 3.0),
            row1 = Vector3(4.0, 5.0, 6.0),
            row2 = Vector3(7.0, 8.0, 9.0),
        )

        val result = matrixA * Matrix3x3.identity

        assertEquals(
            expected = matrixA,
            actual = result,
        )
    }

    @Test
    fun testTimes_simple() {
        val matrixA = Matrix3x3.rowMajor(
            row0 = Vector3(1.0, 2.0, 3.0),
            row1 = Vector3(4.0, 5.0, 6.0),
            row2 = Vector3(7.0, 8.0, 9.0),
        )

        val matrixB = Matrix3x3.rowMajor(
            row0 = Vector3(7.0, 8.0, 9.0),
            row1 = Vector3(10.0, 11.0, 12.0),
            row2 = Vector3(13.0, 14.0, 15.0),
        )

        val result = matrixA * matrixB

        assertEquals(
            expected = Matrix3x3.rowMajor(
                row0 = Vector3(66.0, 72.0, 78.0),
                row1 = Vector3(156.0, 171.0, 186.0),
                row2 = Vector3(246.0, 270.0, 294.0),
            ),
            actual = result,
        )
    }
}
