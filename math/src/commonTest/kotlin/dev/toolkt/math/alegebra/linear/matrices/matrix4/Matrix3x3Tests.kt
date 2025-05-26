package dev.toolkt.math.alegebra.linear.matrices.matrix4

import dev.toolkt.math.algebra.linear.matrices.matrix3.Matrix3x3
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class Matrix3x3Tests {
    @Test
    fun testInvert_singular() {
        assertNull(
            actual = Matrix3x3.zero.invert(),
        )
    }

    @Test
    fun testInvert_simple() {
        val matrix = Matrix3x3.rowMajor(
            Vector3(1.23, 2.34, 3.45),
            Vector3(4.56, 5.67, 6.78),
            Vector3(7.89, 8.90, 9.01),
        )

        val matrixInverted = assertNotNull(
            matrix.invert(),
        )

        assertEqualsWithTolerance(
            expected = Matrix3x3.rowMajor(
                Vector3(-2.78215, 2.89226, -1.11111),
                Vector3(3.73004, -4.85116, 2.22222),
                Vector3(-1.24819, 2.2592, -1.11111),
            ),
            actual = matrixInverted,
        )
    }
}
