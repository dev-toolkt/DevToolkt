package dev.toolkt.math.alegebra.linear.matrices.matrix4

import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4
import dev.toolkt.math.algebra.linear.vectors.Vector4
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Matrix4x4Tests {
    @Test
    fun testIsUpperTriangular() {
        val upperTriangular = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(0.0, 5.0, 6.0, 7.0),
            row2 = Vector4(0.0, 0.0, 8.0, 9.0),
            row3 = Vector4(0.0, 0.0, 0.0, 10.0),
        )

        assertTrue(
            actual = upperTriangular.isUpperTriangular(),
        )

        val nonUpperTriangular = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(5.0, 6.0, 7.0, 8.0),
            row2 = Vector4(9.0, 10.0, 11.0, 12.0),
            row3 = Vector4(13.0, 14.0, 15.0, 16.0),
        )

        assertFalse(
            actual = nonUpperTriangular.isUpperTriangular(),
        )
    }

    @Test
    fun testIsLowerTriangular() {
        val lowerTriangular = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 0.0, 0.0, 0.0),
            row1 = Vector4(2.0, 3.0, 0.0, 0.0),
            row2 = Vector4(4.0, 5.0, 6.0, 0.0),
            row3 = Vector4(7.0, 8.0, 9.0, 10.0),
        )

        assertTrue(
            actual = lowerTriangular.isLowerTriangular(),
        )

        val nonLowerTriangular = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(5.0, 6.0, 7.0, 8.0),
            row2 = Vector4(9.0, 10.0, 11.0, 12.0),
            row3 = Vector4(13.0, 14.0, 15.0, 16.0),
        )

        assertFalse(
            actual = nonLowerTriangular.isLowerTriangular(),
        )
    }

    @Test
    fun testSwapRows() {
        val matrix = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(5.0, 6.0, 7.0, 8.0),
            row2 = Vector4(9.0, 10.0, 11.0, 12.0),
            row3 = Vector4(13.0, 14.0, 15.0, 16.0),
        )

        val swapped = matrix.swapRows(i0 = 0, i1 = 2)

        assertEquals(
            expected = Matrix4x4.rowMajor(
                row0 = Vector4(9.0, 10.0, 11.0, 12.0),
                row1 = Vector4(5.0, 6.0, 7.0, 8.0),
                row2 = Vector4(1.0, 2.0, 3.0, 4.0),
                row3 = Vector4(13.0, 14.0, 15.0, 16.0),
            ),
            actual = swapped,
        )
    }

    @Test
    fun testSolveByBackSubstitution() {
        val upperTriangular = Matrix4x4.rowMajor(
            row0 = Vector4(2.0, -1.0, 0.0, 0.0),
            row1 = Vector4(0.0, 3.0, -1.0, 0.0),
            row2 = Vector4(0.0, 0.0, 4.0, -1.0),
            row3 = Vector4(0.0, 0.0, 0.0, 5.0),
        )

        val yVector = Vector4(1.0, 2.0, 3.0, 4.0)
        val solution = upperTriangular.solveByBackSubstitution(yVector)

        assertEqualsWithTolerance(
            expected = Vector4(0.99166, 0.98333, 0.95, 0.8),
            actual = solution,
        )
    }

    @Test
    fun testSolveByForwardSubstitution() {
        val lowerTriangular = Matrix4x4.rowMajor(
            row0 = Vector4(2.0, 0.0, 0.0, 0.0),
            row1 = Vector4(-1.0, 3.0, 0.0, 0.0),
            row2 = Vector4(0.0, -1.0, 4.0, 0.0),
            row3 = Vector4(0.0, 0.0, -1.0, 5.0),
        )

        val yVector = Vector4(2.0, 1.0, 0.0, -1.0)
        val solution = lowerTriangular.solveByForwardSubstitution(yVector)

        assertEqualsWithTolerance(
            expected = Vector4(
                1.0,
                0.66667,
                0.16667,
                -0.16667,
            ),
            actual = solution,
        )
    }

    @Test
    fun testLupDecompose() {
        val matrix = Matrix4x4.rowMajor(
            row0 = Vector4(2.0, 1.0, 1.0, 0.0),
            row1 = Vector4(4.0, 3.0, 3.0, 1.0),
            row2 = Vector4(8.0, 7.0, 9.0, 5.0),
            row3 = Vector4(6.0, 7.0, 9.0, 8.0),
        )

        val decomposition = assertNotNull(matrix.lupDecompose())

        val (l, u, p) = decomposition

        assertEquals(
            expected = p * matrix,
            actual = l * u,
        )
    }

    @Test
    fun testInvertSingular() {
        assertNull(
            actual = Matrix4x4.zero.invert(),
        )
    }
}
