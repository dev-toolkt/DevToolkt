package dev.toolkt.math.alegebra.linear.matrices.matrix4

import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4xN
import dev.toolkt.math.algebra.linear.matrices.matrix4.MatrixNx4
import dev.toolkt.math.algebra.linear.vectors.Vector4
import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixNx4Tests {
    @Test
    fun testTransposed() {
        val matrix = MatrixNx4(
            rows = listOf(
                Vector4(1.0, 2.0, 3.0, 4.0),
                Vector4(5.0, 6.0, 7.0, 8.0),
            )
        )
        val transposed = matrix.transposed

        assertEquals(
            expected = Matrix4xN(
                columns = listOf(
                    Vector4(1.0, 2.0, 3.0, 4.0),
                    Vector4(5.0, 6.0, 7.0, 8.0),
                )
            ),
            actual = transposed,
        )
    }
}
