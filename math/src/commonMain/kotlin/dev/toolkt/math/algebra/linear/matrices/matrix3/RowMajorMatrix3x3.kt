package dev.toolkt.math.algebra.linear.matrices.matrix3

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector3

internal data class RowMajorMatrix3x3(
    override val row0: Vector3,
    override val row1: Vector3,
    override val row2: Vector3,
) : EagerMatrix3x3() {
    override fun get(
        i: Int,
        j: Int,
    ): Double = getRow(i)[j]

    override val transposed: Matrix3x3
        get() = ColumnMajorMatrix3x3(
            column0 = row0,
            column1 = row1,
            column2 = row2,
        )

    override val column0: Vector3
        get() = Vector3(
            a0 = row0.a0,
            a1 = row1.a0,
            a2 = row2.a0,
        )

    override val column1: Vector3
        get() = Vector3(
            a0 = row0.a1,
            a1 = row1.a1,
            a2 = row2.a1,
        )

    override val column2: Vector3
        get() = Vector3(
            a0 = row0.a2,
            a1 = row1.a2,
            a2 = row2.a2,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Matrix3x3 -> false
        else -> equalsWithToleranceRowWise(
            other = other,
            tolerance = tolerance,
        )
    }
}
