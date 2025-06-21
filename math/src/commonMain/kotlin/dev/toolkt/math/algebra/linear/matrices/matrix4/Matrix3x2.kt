package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.matrices.matrix3.Matrix3x3
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3

data class Matrix3x2(
    val row0: Vector2,
    val row1: Vector2,
    val row2: Vector2,
) : NumericObject {
    companion object {
        val zero = rowMajor(
            row0 = Vector2(0.0, 0.0),
            row1 = Vector2(0.0, 0.0),
            row2 = Vector2(0.0, 0.0),
        )

        fun rowMajor(
            row0: Vector2,
            row1: Vector2,
            row2: Vector2,
        ): Matrix3x2 = Matrix3x2(
            row0 = row0,
            row1 = row1,
            row2 = row2,
        )

        fun columnMajor(
            column0: Vector3,
            column1: Vector3,
        ): Matrix3x2 = TODO()
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
    ): Vector2 = when (i) {
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
        else -> throw IllegalArgumentException("Invalid column index: $j")
    }

    operator fun get(
        i: Int,
    ): Vector2 = getRow(i = i)

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is Matrix3x2 -> false
        !row0.equalsWithTolerance(other.row0, tolerance = tolerance) -> false
        !row1.equalsWithTolerance(other.row1, tolerance = tolerance) -> false
        !row2.equalsWithTolerance(other.row2, tolerance = tolerance) -> false
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

    val column0: Vector3
        get() = Vector3(
            a0 = row0.a0,
            a1 = row1.a0,
            a2 = row2.a0,
        )

    val column1: Vector3
        get() = Vector3(
            a0 = row0.a1,
            a1 = row1.a1,
            a2 = row2.a1,
        )

    val transposed: Matrix2x3
        get() = Matrix2x3(
            column0 = row0,
            column1 = row1,
            column2 = row2,
        )

    operator fun times(
        other: Matrix2x3,
    ): Matrix3x3 = TODO()
}
