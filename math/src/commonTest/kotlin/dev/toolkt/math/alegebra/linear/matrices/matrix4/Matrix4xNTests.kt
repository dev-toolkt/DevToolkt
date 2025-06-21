package dev.toolkt.math.alegebra.linear.matrices.matrix4

import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4xN
import dev.toolkt.math.algebra.linear.matrices.matrix4.MatrixNx4
import dev.toolkt.math.algebra.linear.vectors.Vector4
import dev.toolkt.math.algebra.linear.vectors.VectorN
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class Matrix4xNTests {
    @Test
    fun testApply() {
        val matrix = Matrix4xN(
            columns = listOf(
                Vector4(1.0, 0.0, 0.0, 0.0),
                Vector4(0.0, 1.0, 0.0, 0.0),
                Vector4(0.0, 0.0, 1.0, 0.0),
                Vector4(0.0, 0.0, 0.0, 1.0),
            )
        )
        val vector = VectorN(listOf(1.0, 2.0, 3.0, 4.0))
        val result = matrix.apply(vector)

        assertEquals(
            expected = Vector4(1.0, 2.0, 3.0, 4.0),
            actual = result,
        )
    }

    @Test
    fun testTimes() {
        val matrix4xN = Matrix4xN(
            columns = listOf(
                Vector4(1.0, 2.0, 3.0, 4.0),
                Vector4(5.0, 6.0, 7.0, 8.0),
                Vector4(9.0, 10.0, 11.0, 12.0),
                Vector4(13.0, 14.0, 15.0, 16.0),
                Vector4(17.0, 18.0, 19.0, 20.0),
                Vector4(21.0, 22.0, 23.0, 24.0),
            ),
        )

        val matrixNx4 = MatrixNx4(
            rows = listOf(
                Vector4(25.0, 26.0, 27.0, 28.0),
                Vector4(29.0, 30.0, 31.0, 32.0),
                Vector4(33.0, 34.0, 35.0, 36.0),
                Vector4(37.0, 38.0, 39.0, 40.0),
                Vector4(41.0, 42.0, 43.0, 44.0),
                Vector4(45.0, 46.0, 47.0, 48.0),
            ),
        )

        val result = matrix4xN * matrixNx4

        assertEqualsWithTolerance(
            expected = Matrix4x4.rowMajor(
                row0 = Vector4(2590.0, 2656.0, 2722.0, 2788.0),
                row1 = Vector4(2800.0, 2872.0, 2944.0, 3016.0),
                row2 = Vector4(3010.0, 3088.0, 3166.0, 3244.0),
                row3 = Vector4(3220.0, 3304.0, 3388.0, 3472.0),
            ),
            actual = result,
        )
    }

    @Test
    fun testTransposed() {
        val matrix = Matrix4xN(
            columns = listOf(
                Vector4(1.0, 2.0, 3.0, 4.0),
                Vector4(5.0, 6.0, 7.0, 8.0),
            )
        )
        val transposed = matrix.transposed

        assertEquals(
            expected = MatrixNx4(
                rows = listOf(
                    Vector4(1.0, 2.0, 3.0, 4.0),
                    Vector4(5.0, 6.0, 7.0, 8.0),
                )
            ),
            actual = transposed,
        )
    }
}
