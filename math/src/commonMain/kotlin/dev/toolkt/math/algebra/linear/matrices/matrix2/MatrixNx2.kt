package dev.toolkt.math.algebra.linear.matrices.matrix2

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.VectorN

data class MatrixNx2(
    val rows: List<Vector2>,
) : NumericObject {
    val column0: VectorN
        get() = VectorN(
            rows.map { it.a0 },
        )

    val column1: VectorN
        get() = VectorN(
            rows.map { it.a1 },
        )

    val height: Int
        get() = rows.size

    val transposed: Matrix2xN
        get() = Matrix2xN(
            columns = rows,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when (other) {
        !is MatrixNx2 -> false

        else -> rows.equalsWithTolerance(
            other.rows,
            tolerance = tolerance,
        )
    }
}
