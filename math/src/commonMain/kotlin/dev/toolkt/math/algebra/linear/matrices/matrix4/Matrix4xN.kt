package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.matrices.matrix2.Matrix4x2
import dev.toolkt.math.algebra.linear.matrices.matrix2.MatrixNx2
import dev.toolkt.math.algebra.linear.vectors.Vector4
import dev.toolkt.math.algebra.linear.vectors.VectorN

data class Matrix4xN(
    val columns: List<Vector4>,
) : NumericObject {
    val row0: VectorN
        get() = VectorN(
            columns.map { it.a0 },
        )

    val row1: VectorN
        get() = VectorN(
            columns.map { it.a1 },
        )

    val row2: VectorN
        get() = VectorN(
            columns.map { it.a2 },
        )

    val row3: VectorN
        get() = VectorN(
            columns.map { it.a3 },
        )

    val width: Int
        get() = columns.size

    val transposed: MatrixNx4
        get() = MatrixNx4(
            rows = columns,
        )

    fun apply(
        argumentVector: VectorN,
    ): Vector4 = Vector4(
        row0.dot(argumentVector),
        row1.dot(argumentVector),
        row2.dot(argumentVector),
        row3.dot(argumentVector),
    )

    operator fun times(
        other: MatrixNx2,
    ): Matrix4x2 {
        require(width == other.height) {
            "Incompatible matrix sizes: 4x${width} and ${other.height}x2"
        }

        return Matrix4x2(
            row0 = row0.applyT(other),
            row1 = row1.applyT(other),
            row2 = row2.applyT(other),
            row3 = row3.applyT(other),
        )
    }

    operator fun times(
        other: MatrixNx4,
    ): Matrix4x4 {
        require(width == other.height) {
            "Incompatible matrix sizes: 4x${width} and ${other.height}x4"
        }

        return Matrix4x4.columnMajor(
            column0 = apply(other.column0),
            column1 = apply(other.column1),
            column2 = apply(other.column2),
            column3 = apply(other.column3),
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when (other) {
        !is Matrix4xN -> false
        else -> columns.equalsWithTolerance(other.columns, tolerance = tolerance)
    }
}
