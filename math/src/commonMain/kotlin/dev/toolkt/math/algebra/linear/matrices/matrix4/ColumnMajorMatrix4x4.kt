package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector4

internal class ColumnMajorMatrix4x4(
    override val column0: Vector4,
    override val column1: Vector4,
    override val column2: Vector4,
    override val column3: Vector4,
) : EagerMatrix4x4() {
    override fun get(
        i: Int,
        j: Int,
    ): Double = getColumn(j)[i]

    override val transposed: Matrix4x4
        get() = RowMajorMatrix4x4(
            row0 = column0,
            row1 = column1,
            row2 = column2,
            row3 = column3,
        )

    override val row0: Vector4
        get() = Vector4(
            a0 = column0.a0,
            a1 = column1.a0,
            a2 = column2.a0,
            a3 = column3.a0,
        )

    override val row1: Vector4
        get() = Vector4(
            a0 = column0.a1,
            a1 = column1.a1,
            a2 = column2.a1,
            a3 = column3.a1,
        )

    override val row2: Vector4
        get() = Vector4(
            a0 = column0.a2,
            a1 = column1.a2,
            a2 = column2.a2,
            a3 = column3.a2,
        )

    override val row3: Vector4
        get() = Vector4(
            a0 = column0.a3,
            a1 = column1.a3,
            a2 = column2.a3,
            a3 = column3.a3,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other is Matrix4x4 -> when {
            other is ColumnMajorMatrix4x4 -> when {
                !column0.equalsWithTolerance(other.column0, tolerance = tolerance) -> false
                !column1.equalsWithTolerance(other.column1, tolerance = tolerance) -> false
                !column2.equalsWithTolerance(other.column2, tolerance = tolerance) -> false
                !column3.equalsWithTolerance(other.column3, tolerance = tolerance) -> false
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
