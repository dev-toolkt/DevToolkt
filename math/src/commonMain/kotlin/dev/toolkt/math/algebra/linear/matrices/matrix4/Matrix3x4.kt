package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.matrices.matrix2.Matrix4x2
import dev.toolkt.math.algebra.linear.matrices.matrix3.Matrix3x3
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.Vector4

data class Matrix3x4(
    val column0: Vector3,
    val column1: Vector3,
    val column2: Vector3,
    val column3: Vector3,
) : NumericObject {
    companion object {
        val zero = columnMajor(
            column0 = Vector3(0.0, 0.0, 0.0),
            column1 = Vector3(0.0, 0.0, 0.0),
            column2 = Vector3(0.0, 0.0, 0.0),
            column3 = Vector3(0.0, 0.0, 0.0),
        )

        fun rowMajor(
            row0: Vector3,
            row1: Vector3,
            row2: Vector3,
            row3: Vector3,
        ): Matrix3x4 = TODO()

        fun columnMajor(
            column0: Vector3,
            column1: Vector3,
            column2: Vector3,
            column3: Vector3,
        ): Matrix3x4 = Matrix3x4(
            column0 = column0,
            column1 = column1,
            column2 = column2,
            column3 = column3,
        )
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
    ): Vector4 = when (i) {
        0 -> row0
        1 -> row1
        2 -> row2
        else -> throw IllegalArgumentException("Invalid row index: $i")
    }

    fun getColumn(
        j: Int,
    ): Vector3 = when (j) {
        0 -> column0
        1 -> column1
        2 -> column2
        3 -> column3
        else -> throw IllegalArgumentException("Invalid column index: $j")
    }

    operator fun get(
        i: Int,
    ): Vector4 = getRow(i = i)

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is Matrix3x4 -> false
        !column0.equalsWithTolerance(other.column0, tolerance = tolerance) -> false
        !column1.equalsWithTolerance(other.column1, tolerance = tolerance) -> false
        !column2.equalsWithTolerance(other.column2, tolerance = tolerance) -> false
        !column3.equalsWithTolerance(other.column3, tolerance = tolerance) -> false
        else -> true
    }

    override fun toString(): String = """[
        |  $row0,
        |  $row1,
        |  $row2,
        |]
    """.trimMargin()

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    val row0: Vector4
        get() = Vector4(
            a0 = column0.a0,
            a1 = column1.a0,
            a2 = column2.a0,
            a3 = column3.a0,
        )

    val row1: Vector4
        get() = Vector4(
            a0 = column0.a1,
            a1 = column1.a1,
            a2 = column2.a1,
            a3 = column3.a1,
        )

    val row2: Vector4
        get() = Vector4(
            a0 = column0.a2,
            a1 = column1.a2,
            a2 = column2.a2,
            a3 = column3.a2,
        )

    val transposed: Matrix4x3
        get() = Matrix4x3(
            row0 = column0,
            row1 = column1,
            row2 = column2,
            row3 = column3,
        )

    operator fun times(
        other: Matrix4x2,
    ): Matrix3x2 = Matrix3x2(
        row0 = row0.hDot(other),
        row1 = row1.hDot(other),
        row2 = row2.hDot(other),
    )

    operator fun times(
        other: Matrix4x3,
    ): Matrix3x3 = Matrix3x3.Companion.rowMajor(
        row0 = row0.hDot(other),
        row1 = row1.hDot(other),
        row2 = row2.hDot(other),
    )
}
