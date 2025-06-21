package dev.toolkt.math.alegebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.matrices.matrix4.InvertedMatrix4x4
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4
import dev.toolkt.math.algebra.linear.vectors.Vector4
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertIs

class InvertedMatrix4x4Tests {
    private val tolerance = NumericObject.Tolerance.Absolute(
        absoluteTolerance = 10e-4,
    )

    private val aMatrix = Matrix4x4.rowMajor(
        row0 = Vector4(7.0, 3.3, 3.0, 2.5),
        row1 = Vector4(3.0, 6.0, 0.0, 5.0),
        row2 = Vector4(5.0, 2.0, -1.0, 0.0),
        row3 = Vector4(2.0, 3.0, 1.0, 4.0),
    )

    @Test
    fun testApply() {
        val bVector = Vector4(2.0, 6.0, -10.0, 14.0)

        val aMatrixInverted = assertIs<InvertedMatrix4x4>(aMatrix.invert())

        val cMatrix = aMatrixInverted.apply(bVector)

        assertEqualsWithTolerance(
            expected = Vector4(0.0466, -5.0723, 0.0884, 7.2588),
            actual = cMatrix,
            tolerance = tolerance,
        )
    }

    @Test
    fun testTimes() {
        val bMatrix = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 2.0, 3.0, 4.0),
            row1 = Vector4(5.5, 6.0, -7.0, 8.0),
            row2 = Vector4(9.0, -10.0, 11.0, 12.0),
            row3 = Vector4(13.0, 14.0, 15.0, 16.0),
        )

        val aMatrixInverted = assertIs<InvertedMatrix4x4>(aMatrix.invert())

        val cMatrix = aMatrixInverted * bMatrix

        assertEqualsWithTolerance(
            expected = Matrix4x4.rowMajor(
                row0 = Vector4(4.2536, 0.0466, 7.6841, 5.1801),
                row1 = Vector4(-9.8332, -5.0723, -18.7341, -11.1410),
                row2 = Vector4(-7.3983, 0.0884, -10.0470, -8.3826),
                row3 = Vector4(10.3480, 7.2588, 16.4700, 11.8620),
            ),
            actual = cMatrix,
            tolerance = tolerance,
        )
    }

    @Test
    fun testComputed() {
        val aMatrixInverted = assertIs<InvertedMatrix4x4>(aMatrix.invert())

        assertEqualsWithTolerance(
            expected = Vector4(
                -0.00803859,
                -0.19855305,
                0.22909968,
                0.25321543,
            ),
            actual = aMatrixInverted.row0,
            tolerance = tolerance,
        )

        assertEqualsWithTolerance(
            expected = Vector4(
                0.18488746,
                0.56672026,
                -0.26929260,
                -0.82395498,
            ),
            actual = aMatrixInverted.row1,
            tolerance = tolerance,
        )

        assertEqualsWithTolerance(
            expected = Vector4(
                0.32958199,
                0.14067524,
                -0.39308682,
                -0.38183280,
            ),
            actual = aMatrixInverted.row2,
            tolerance = tolerance,
        )

        assertEqualsWithTolerance(
            expected = Vector4(
                -0.21704180,
                -0.36093248,
                0.18569132,
                0.83681672,
            ),
            actual = aMatrixInverted.row3,
            tolerance = tolerance,
        )
    }
}
