package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector4

internal class RowMajorMatrix4x4(
    override val row0: Vector4,
    override val row1: Vector4,
    override val row2: Vector4,
    override val row3: Vector4,
) : EagerMatrix4x4() {
    override fun get(
        i: Int,
        j: Int,
    ): Double = getRow(i)[j]

    override val transposed: Matrix4x4
        get() = ColumnMajorMatrix4x4(
            column0 = row0,
            column1 = row1,
            column2 = row2,
            column3 = row3,
        )

    override val column0: Vector4
        get() = Vector4(
            a0 = row0.a0,
            a1 = row1.a0,
            a2 = row2.a0,
            a3 = row3.a0,
        )

    override val column1: Vector4
        get() = Vector4(
            a0 = row0.a1,
            a1 = row1.a1,
            a2 = row2.a1,
            a3 = row3.a1,
        )

    override val column2: Vector4
        get() = Vector4(
            a0 = row0.a2,
            a1 = row1.a2,
            a2 = row2.a2,
            a3 = row3.a2,
        )

    override val column3: Vector4
        get() = Vector4(
            a0 = row0.a3,
            a1 = row1.a3,
            a2 = row2.a3,
            a3 = row3.a3,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Matrix4x4 -> false
        else -> equalsWithToleranceRowWise(
            other = other,
            tolerance = tolerance,
        )
    }
}
