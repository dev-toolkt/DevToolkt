package dev.toolkt.math.algebra.linear.vectors

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericObject.Tolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix3x2
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix3x4
import kotlin.math.sqrt

data class Vector3(
    val a0: Double,
    val a1: Double,
    val a2: Double,
) : NumericObject {
    companion object {
        fun full(
            a: Double,
        ): Vector3 = Vector3(
            a0 = a,
            a1 = a,
            a2 = a,
        )
    }

    operator fun get(i: Int): Double = when (i) {
        0 -> a0
        1 -> a1
        2 -> a2
        else -> throw IndexOutOfBoundsException("Index out of bounds: $i")
    }

    val magnitudeSquared: Double
        get() = a0 * a0 + a1 * a1 + a2 * a2

    val magnitude: Double
        get() = sqrt(magnitudeSquared)

    fun isNormalized(): Boolean = magnitudeSquared.equalsWithTolerance(1.0)

    fun normalize(): Vector3 = this / magnitude

    operator fun unaryMinus(): Vector3 = Vector3(
        a0 = -a0,
        a1 = -a1,
        a2 = -a2,
    )

    operator fun plus(
        other: Vector3,
    ): Vector3 = Vector3(
        a0 = a0 + other.a0,
        a1 = a1 + other.a1,
        a2 = a2 + other.a2,
    )

    operator fun minus(
        other: Vector3,
    ): Vector3 = Vector3(
        a0 = a0 - other.a0,
        a1 = a1 - other.a1,
        a2 = a2 - other.a2,
    )

    operator fun times(
        scalar: Double,
    ): Vector3 = Vector3(
        a0 = a0 * scalar,
        a1 = a1 * scalar,
        a2 = a2 * scalar,
    )

    operator fun div(
        scalar: Double,
    ): Vector3 = Vector3(
        a0 = a0 / scalar,
        a1 = a1 / scalar,
        a2 = a2 / scalar,
    )

    fun normalizeOrNull(): Vector3? {
        val normalized = normalize()
        return normalized.takeIf { it.isNormalized() }
    }

    fun dot(
        other: Vector3,
    ): Double = a0 * other.a0 + a1 * other.a1 + a2 * other.a2

    fun hDot(
        matrix: Matrix3x2,
    ): Vector2 = Vector2(
        this.dot(matrix.column0),
        this.dot(matrix.column1),
    )

    fun hDot(
        matrix: Matrix3x4,
    ): Vector4 = Vector4(
        this.dot(matrix.column0),
        this.dot(matrix.column1),
        this.dot(matrix.column2),
        this.dot(matrix.column3),
    )

    fun cross(
        other: Vector3,
    ): Vector3 = Vector3(
        a0 = a1 * other.a2 - a2 * other.a1,
        a1 = a2 * other.a0 - a0 * other.a2,
        a2 = a0 * other.a1 - a1 * other.a0,
    )

    fun toList(): List<Double> = listOf(
        a0,
        a1,
        a2,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Vector3 -> false

        !a0.equalsWithTolerance(
            other = other.a0,
            tolerance = tolerance,
        ) -> false

        !a1.equalsWithTolerance(
            other = other.a1,
            tolerance = tolerance,
        ) -> false

        !a2.equalsWithTolerance(
            other = other.a2,
            tolerance = tolerance,
        ) -> false

        else -> true
    }
}

operator fun Double.times(
    vector: Vector3,
): Vector3 = vector * this
