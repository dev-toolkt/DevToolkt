package dev.toolkt.math.algebra.linear.matrices.matrix2

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.VectorN

data class Matrix2xN(
    val columns: List<Vector2>,
) : NumericObject {
    val row0: VectorN
        get() = VectorN(
            columns.map { it.a0 },
        )

    val row1: VectorN
        get() = VectorN(
            columns.map { it.a1 },
        )

    val width: Int
        get() = columns.size

    val transposed: MatrixNx2
        get() = MatrixNx2(
            rows = columns,
        )

    fun apply(
        argumentVector: VectorN,
    ): Vector2 = Vector2(
        row0.dot(argumentVector),
        row1.dot(argumentVector),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when (other) {
        !is Matrix2xN -> false
        else -> columns.equalsWithTolerance(other.columns, tolerance = tolerance)
    }
}
