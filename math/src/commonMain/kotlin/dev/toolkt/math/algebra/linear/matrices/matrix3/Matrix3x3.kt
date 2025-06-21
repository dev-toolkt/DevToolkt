package dev.toolkt.math.algebra.linear.matrices.matrix3

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix3x4
import dev.toolkt.math.algebra.linear.vectors.Vector3

sealed class Matrix3x3 : NumericObject {
    companion object {
        val zero = rowMajor(
            row0 = Vector3(0.0, 0.0, 0.0),
            row1 = Vector3(0.0, 0.0, 0.0),
            row2 = Vector3(0.0, 0.0, 0.0),
        )

        val identity = rowMajor(
            row0 = Vector3(1.0, 0.0, 0.0),
            row1 = Vector3(0.0, 1.0, 0.0),
            row2 = Vector3(0.0, 0.0, 1.0),
        )

        fun rowMajor(
            row0: Vector3,
            row1: Vector3,
            row2: Vector3,
        ): Matrix3x3 = RowMajorMatrix3x3(
            row0 = row0,
            row1 = row1,
            row2 = row2,
        )

        fun columnMajor(
            column0: Vector3,
            column1: Vector3,
            column2: Vector3,
        ): Matrix3x3 = ColumnMajorMatrix3x3(
            column0 = column0,
            column1 = column1,
            column2 = column2,
        )
    }

    final override fun equals(
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
        else -> throw IllegalArgumentException("Invalid row index: $i")
    }

    fun getColumn(
        j: Int,
    ): Vector3 = when (j) {
        0 -> column0
        1 -> column1
        2 -> column2
        else -> throw IllegalArgumentException("Invalid column index: $j")
    }

    val adjugate: Matrix3x3
        get() {
            val a = row0.a0
            val b = row0.a1
            val c = row0.a2
            val d = row1.a0
            val e = row1.a1
            val f = row1.a2
            val g = row2.a0
            val h = row2.a1
            val i = row2.a2

            return Matrix3x3.rowMajor(
                row0 = Vector3(
                    -f * h + e * i,
                    c * h - b * i,
                    -c * e + b * f,
                ),
                row1 = Vector3(
                    f * g - d * i,
                    -c * g + a * i,
                    c * d - a * f,
                ),
                row2 = Vector3(
                    -e * g + d * h,
                    b * g - a * h,
                    -b * d + a * e,
                ),
            )
        }

    val determinant: Double
        get() {
            val a = row0.a0
            val b = row0.a1
            val c = row0.a2
            val d = row1.a0
            val e = row1.a1
            val f = row1.a2
            val g = row2.a0
            val h = row2.a1
            val i = row2.a2

            return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)
        }

    protected fun equalsWithToleranceRowWise(
        other: Matrix3x3,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
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

    fun invert(): Matrix3x3? {
        val determinant = determinant

        if (determinant == 0.0) {
            return null
        }

        val adjugate = this.adjugate

        // {{-9.2553, 9.6216, -3.6963}, {12.4086, -16.1382, 7.3926}, {-4.1523, 7.5156, -3.6963}}

        return adjugate / determinant
    }

    fun dotV(other: Vector3): Vector3 = Vector3(
        row0.dot(other),
        row1.dot(other),
        row2.dot(other),
    )

    operator fun times(
        other: Matrix3x4,
    ): Matrix3x4 = Matrix3x4(
        column0 = this.dotV(other.column0),
        column1 = this.dotV(other.column1),
        column2 = this.dotV(other.column2),
        column3 = this.dotV(other.column3),
    )

    operator fun times(
        scalar: Double,
    ): Matrix3x3 = Matrix3x3.rowMajor(
        row0 = row0 * scalar,
        row1 = row1 * scalar,
        row2 = row2 * scalar,
    )

    operator fun div(
        scalar: Double,
    ): Matrix3x3 = Matrix3x3.rowMajor(
        row0 = row0 / scalar,
        row1 = row1 / scalar,
        row2 = row2 / scalar,
    )

    abstract val row0: Vector3
    abstract val row1: Vector3
    abstract val row2: Vector3

    abstract val column0: Vector3
    abstract val column1: Vector3
    abstract val column2: Vector3

    abstract val transposed: Matrix3x3

    abstract operator fun get(
        i: Int,
        j: Int,
    ): Double

    abstract fun apply(argumentVector: Vector3): Vector3

    abstract operator fun times(other: Matrix3x3): Matrix3x3
}
