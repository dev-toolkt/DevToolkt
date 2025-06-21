package dev.toolkt.math.alegebra.linear.matrices.matrix4

import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4
import dev.toolkt.math.algebra.linear.vectors.Vector4
import kotlin.test.Test
import kotlin.test.assertEquals

class EagerMatrix4x4Tests {
    @Test
    fun testApply() {
        val matrix = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(5.0, 6.0, 7.0, 8.0),
            row2 = Vector4(9.0, 10.0, 11.0, 12.0),
            row3 = Vector4(13.0, 14.0, 15.0, 16.0),
        )

        val vector = Vector4(1.0, 0.0, -1.0, 2.0)
        val result = matrix.apply(vector)

        assertEquals(
            expected = Vector4(6.0, 14.0, 22.0, 30.0),
            actual = result,
        )
    }

    @Test
    fun testTimes_identity() {
        val matrixA = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(5.0, 6.0, 7.0, 8.0),
            row2 = Vector4(9.0, 10.0, 11.0, 12.0),
            row3 = Vector4(13.0, 14.0, 15.0, 16.0),
        )

        val result = matrixA * Matrix4x4.identity

        assertEquals(
            expected = matrixA,
            actual = result,
        )
    }

    @Test
    fun testTimes_simple() {
        val matrixA = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(5.0, 6.0, 7.0, 8.0),
            row2 = Vector4(9.0, 10.0, 11.0, 12.0),
            row3 = Vector4(13.0, 14.0, 15.0, 16.0),
        )

        val matrixB = Matrix4x4.rowMajor(
            row0 = Vector4(7.0, 8.0, 9.0, 10.0),
            row1 = Vector4(11.0, 12.0, 13.0, 14.0),
            row2 = Vector4(15.0, 16.0, 17.0, 18.0),
            row3 = Vector4(19.0, 20.0, 21.0, 22.0),
        )

        val result = matrixA * matrixB

        assertEquals(
            expected = Matrix4x4.rowMajor(
                row0 = Vector4(150.0, 160.0, 170.0, 180.0),
                row1 = Vector4(358.0, 384.0, 410.0, 436.0),
                row2 = Vector4(566.0, 608.0, 650.0, 692.0),
                row3 = Vector4(774.0, 832.0, 890.0, 948.0),
            ),
            actual = result,
        )
    }
}
