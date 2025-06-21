package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.matrices.matrix2.Matrix4x2
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.Vector4

data class Matrix4x3(
    val row0: Vector3,
    val row1: Vector3,
    val row2: Vector3,
    val row3: Vector3,
) : NumericObject {
    companion object {
        val zero = rowMajor(
            row0 = Vector3(0.0, 0.0, 0.0),
            row1 = Vector3(0.0, 0.0, 0.0),
            row2 = Vector3(0.0, 0.0, 0.0),
            row3 = Vector3(0.0, 0.0, 0.0),
        )

        fun rowMajor(
            row0: Vector3,
            row1: Vector3,
            row2: Vector3,
            row3: Vector3,
        ): Matrix4x3 = Matrix4x3(
            row0 = row0,
            row1 = row1,
            row2 = row2,
            row3 = row3,
        )

        fun columnMajor(
            column0: Vector3,
            column1: Vector3,
            column2: Vector3,
        ): Matrix4x3 = TODO()
    }

    override fun equals(
        other: Any?,
    ): Boolean {
        return equalsWithTolerance(
            other = other as? NumericObject ?: return false,
            tolerance = NumericObject.Tolerance.Zero,
        )
    }

    fun getRow(
        i: Int,
    ): Vector3 = when (i) {
        0 -> row0
        1 -> row1
        2 -> row2
        3 -> row3
        else -> throw IllegalArgumentException("Invalid row index: $i")
    }

    fun getColumn(
        j: Int,
    ): Vector4 = when (j) {
        0 -> column0
        1 -> column1
        2 -> column2
        else -> throw IllegalArgumentException("Invalid column index: $j")
    }

    operator fun get(
        i: Int,
    ): Vector3 = getRow(i = i)

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is Matrix4x3 -> false
        !row0.equalsWithTolerance(other.row0, tolerance = tolerance) -> false
        !row1.equalsWithTolerance(other.row1, tolerance = tolerance) -> false
        !row2.equalsWithTolerance(other.row2, tolerance = tolerance) -> false
        !row3.equalsWithTolerance(other.row3, tolerance = tolerance) -> false
        else -> true
    }

    override fun toString(): String = """[
        |  $row0,
        |  $row1,
        |  $row2,
        |  $row3,
        |]
    """.trimMargin()

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    val column0: Vector4
        get() = Vector4(
            a0 = row0.a0,
            a1 = row1.a0,
            a2 = row2.a0,
            a3 = row3.a0,
        )

    val column1: Vector4
        get() = Vector4(
            a0 = row0.a1,
            a1 = row1.a1,
            a2 = row2.a1,
            a3 = row3.a1,
        )

    val column2: Vector4
        get() = Vector4(
            a0 = row0.a2,
            a1 = row1.a2,
            a2 = row2.a2,
            a3 = row3.a2,
        )

    val transposed: Matrix3x4
        get() = Matrix3x4(
            column0 = row0,
            column1 = row1,
            column2 = row2,
            column3 = row3,
        )

    operator fun times(
        other: Matrix3x4,
    ): Matrix4x4 = Matrix4x4.rowMajor(
        row0 = row0.hDot(other),
        row1 = row1.hDot(other),
        row2 = row2.hDot(other),
        row3 = row3.hDot(other),
    )

    operator fun times(
        other: Matrix3x2,
    ): Matrix4x2 = Matrix4x2(
        row0 = this.row0.hDot(other),
        row1 = this.row1.hDot(other),
        row2 = this.row2.hDot(other),
        row3 = this.row3.hDot(other),
    )

    /**
     * A Moore-Penrose pseudoinverse (assuming this is a full column rank matrix)
     */
    fun pseudoInverse(): Matrix3x4 {
        val matrixTransposed = this.transposed

        val gramMatrix = matrixTransposed * this

        val gramMatrixInverted = gramMatrix.invert() ?: throw AssertionError("Matrix is not invertible")

        return gramMatrixInverted * matrixTransposed
    }
}
