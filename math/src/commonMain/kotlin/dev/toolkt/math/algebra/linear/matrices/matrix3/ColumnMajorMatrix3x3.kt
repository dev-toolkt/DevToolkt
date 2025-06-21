package dev.toolkt.math.algebra.linear.matrices.matrix3

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector3

internal data class ColumnMajorMatrix3x3(
    override val column0: Vector3,
    override val column1: Vector3,
    override val column2: Vector3,
) : EagerMatrix3x3() {
    override fun get(i: Int, j: Int): Double = getColumn(j)[i]

    override val transposed: Matrix3x3
        get() = RowMajorMatrix3x3(
            row0 = column0,
            row1 = column1,
            row2 = column2,
        )

    override val row0: Vector3
        get() = Vector3(
            a0 = column0.a0,
            a1 = column1.a0,
            a2 = column2.a0,
        )

    override val row1: Vector3
        get() = Vector3(
            a0 = column0.a1,
            a1 = column1.a1,
            a2 = column2.a1,
        )

    override val row2: Vector3
        get() = Vector3(
            a0 = column0.a2,
            a1 = column1.a2,
            a2 = column2.a2,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other is Matrix3x3 -> when {
            other is ColumnMajorMatrix3x3 -> when {
                !column0.equalsWithTolerance(other.column0, tolerance = tolerance) -> false
                !column1.equalsWithTolerance(other.column1, tolerance = tolerance) -> false
                !column2.equalsWithTolerance(other.column2, tolerance = tolerance) -> false
                else -> true
            }

            else -> equalsWithToleranceRowWise(
                other = other,
                tolerance = tolerance,
            )
        }

        else -> false
    }
}
