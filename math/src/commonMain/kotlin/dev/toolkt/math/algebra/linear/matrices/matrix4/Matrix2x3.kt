package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3

data class Matrix2x3(
    val column0: Vector2,
    val column1: Vector2,
    val column2: Vector2,
) : NumericObject {
    companion object {
        val zero = columnMajor(
            column0 = Vector2(0.0, 0.0),
            column1 = Vector2(0.0, 0.0),
            column2 = Vector2(0.0, 0.0),
        )

        fun rowMajor(
            row0: Vector3,
            row1: Vector3,
        ): Matrix2x3 = TODO()

        fun columnMajor(
            column0: Vector2,
            column1: Vector2,
            column2: Vector2,
        ): Matrix2x3 = Matrix2x3(
            column0 = column0,
            column1 = column1,
            column2 = column2,
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
    ): Vector3 = when (i) {
        0 -> row0
        1 -> row1
        else -> throw IllegalArgumentException("Invalid row index: $i")
    }

    fun getColumn(
        j: Int,
    ): Vector2 = when (j) {
        0 -> column0
        1 -> column1
        2 -> column2
        else -> throw IllegalArgumentException("Invalid column index: $j")
    }

    operator fun get(
        i: Int,
    ): Vector3 = getRow(i = i)

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is Matrix2x3 -> false
        !column0.equalsWithTolerance(other.column0, tolerance = tolerance) -> false
        !column1.equalsWithTolerance(other.column1, tolerance = tolerance) -> false
        !column2.equalsWithTolerance(other.column2, tolerance = tolerance) -> false
        else -> true
    }

    override fun toString(): String = """[
        |  $row0,
        |  $row1,
        |]
    """.trimMargin()

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    val row0: Vector3
        get() = Vector3(
            a0 = column0.a0,
            a1 = column1.a0,
            a2 = column2.a0,
        )

    val row1: Vector3
        get() = Vector3(
            a0 = column0.a1,
            a1 = column1.a1,
            a2 = column2.a1,
        )

    val transposed: Matrix3x2
        get() = Matrix3x2(
            row0 = column0,
            row1 = column1,
            row2 = column2,
        )
}
