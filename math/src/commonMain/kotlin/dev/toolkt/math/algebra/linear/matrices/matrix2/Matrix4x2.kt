package dev.toolkt.math.algebra.linear.matrices.matrix2

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector4

data class Matrix4x2(
    val row0: Vector2,
    val row1: Vector2,
    val row2: Vector2,
    val row3: Vector2,
) : NumericObject {
    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Matrix4x2 -> false

        !row0.equalsWithTolerance(other.row0, tolerance = tolerance) -> false
        !row1.equalsWithTolerance(other.row1, tolerance = tolerance) -> false
        !row2.equalsWithTolerance(other.row2, tolerance = tolerance) -> false
        !row3.equalsWithTolerance(other.row3, tolerance = tolerance) -> false
        else -> true
    }

    val column0: Vector4
        get() = Vector4(
            row0.a0,
            row1.a0,
            row2.a0,
            row3.a0,
        )

    val column1: Vector4
        get() = Vector4(
            row0.a1,
            row1.a1,
            row2.a1,
            row3.a1,
        )
}
