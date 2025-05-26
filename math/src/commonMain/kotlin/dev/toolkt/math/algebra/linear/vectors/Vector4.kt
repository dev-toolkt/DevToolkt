package dev.toolkt.math.algebra.linear.vectors

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericObject.Tolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.matrices.matrix2.Matrix4x2
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x3
import kotlin.math.sqrt

data class Vector4(
    val a0: Double,
    val a1: Double,
    val a2: Double,
    val a3: Double,
) : NumericObject {
    companion object {
        fun full(
            a: Double,
        ): Vector4 = Vector4(
            a0 = a,
            a1 = a,
            a2 = a,
            a3 = a,
        )
    }

    operator fun get(i: Int): Double = when (i) {
        0 -> a0
        1 -> a1
        2 -> a2
        3 -> a3
        else -> throw IndexOutOfBoundsException("Index out of bounds: $i")
    }

    val magnitudeSquared: Double
        get() = a0 * a0 + a1 * a1 + a2 * a2 + a3 * a3

    val magnitude: Double
        get() = sqrt(magnitudeSquared)

    fun isNormalized(): Boolean = magnitudeSquared.equalsWithTolerance(1.0)

    fun normalize(): Vector4 = this / magnitude

    operator fun unaryMinus(): Vector4 = Vector4(
        a0 = -a0,
        a1 = -a1,
        a2 = -a2,
        a3 = -a3,
    )

    operator fun plus(
        other: Vector4,
    ): Vector4 = Vector4(
        a0 = a0 + other.a0,
        a1 = a1 + other.a1,
        a2 = a2 + other.a2,
        a3 = a3 + other.a3,
    )

    operator fun minus(
        other: Vector4,
    ): Vector4 = Vector4(
        a0 = a0 - other.a0,
        a1 = a1 - other.a1,
        a2 = a2 - other.a2,
        a3 = a3 - other.a3,
    )

    operator fun times(
        scalar: Double,
    ): Vector4 = Vector4(
        a0 = a0 * scalar,
        a1 = a1 * scalar,
        a2 = a2 * scalar,
        a3 = a3 * scalar,
    )

    operator fun div(
        scalar: Double,
    ): Vector4 = Vector4(
        a0 = a0 / scalar,
        a1 = a1 / scalar,
        a2 = a2 / scalar,
        a3 = a3 / scalar,
    )

    fun normalizeOrNull(): Vector4? {
        val normalized = normalize()
        return normalized.takeIf { it.isNormalized() }
    }

    fun dot(
        other: Vector4,
    ): Double = a0 * other.a0 + a1 * other.a1 + a2 * other.a2 + a3 * other.a3

    fun hDot(
        other: Matrix4x2,
    ): Vector2 = Vector2(
        this.dot(other.column0),
        this.dot(other.column1),
    )

    fun hDot(
        other: Matrix4x3,
    ): Vector3 = Vector3(
        this.dot(other.column0),
        this.dot(other.column1),
        this.dot(other.column2),
    )

    fun toList(): List<Double> = listOf(
        a0,
        a1,
        a2,
        a3,
    )

    override fun toString(): String = "[$a0, $a1, $a2, $a3]"

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Vector4 -> false
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

        !a3.equalsWithTolerance(
            other = other.a3,
            tolerance = tolerance,
        ) -> false

        else -> true
    }
}

operator fun Double.times(
    vector: Vector4,
): Vector4 = vector * this
