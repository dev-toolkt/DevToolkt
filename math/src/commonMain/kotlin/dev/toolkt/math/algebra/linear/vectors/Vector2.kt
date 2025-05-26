package dev.toolkt.math.algebra.linear.vectors

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericObject.Tolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import kotlin.math.sqrt

data class Vector2(
    val a0: Double,
    val a1: Double,
) : NumericObject {
    companion object {
        val Zero = Vector2(
            a0 = 0.0,
            a1 = 0.0,
        )

        fun full(
            a: Double,
        ): Vector2 = Vector2(
            a0 = a,
            a1 = a,
        )

        fun distance(
            first: Vector2,
            second: Vector2,
        ) = (second - first).magnitude

        fun distanceSquared(
            first: Vector2,
            second: Vector2,
        ) = (second - first).magnitudeSquared
    }

    operator fun get(i: Int): Double = when (i) {
        0 -> a0
        1 -> a1
        else -> throw IndexOutOfBoundsException("Index out of bounds: $i")
    }

    val magnitudeSquared: Double
        get() = a0 * a0 + a1 * a1

    val magnitude: Double
        get() = sqrt(magnitudeSquared)

    fun isNormalized(): Boolean = magnitudeSquared.equalsWithTolerance(1.0)

    fun normalize(): Vector2 = this / magnitude

    fun toVector3(
        a2: Double = 1.0,
    ): Vector3 = Vector3(
        a0 = a0,
        a1 = a1,
        a2 = a2,
    )

    operator fun unaryMinus(): Vector2 = Vector2(
        a0 = -a0,
        a1 = -a1,
    )

    operator fun plus(
        other: Vector2,
    ): Vector2 = Vector2(
        a0 = a0 + other.a0,
        a1 = a1 + other.a1,
    )

    operator fun minus(
        other: Vector2,
    ): Vector2 = Vector2(
        a0 = a0 - other.a0,
        a1 = a1 - other.a1,
    )

    operator fun times(
        scalar: Double,
    ): Vector2 = Vector2(
        a0 = a0 * scalar,
        a1 = a1 * scalar,
    )

    operator fun times(
        other: Vector2,
    ): Vector2 = Vector2(
        a0 = a0 * other.a0,
        a1 = a1 * other.a1,
    )

    operator fun div(
        scalar: Double,
    ): Vector2 = Vector2(
        a0 = a0 / scalar,
        a1 = a1 / scalar,
    )

    fun normalizeOrNull(): Vector2? {
        val normalized = normalize()
        return normalized.takeIf { it.isNormalized() }
    }

    fun dot(
        other: Vector2,
    ): Double = a0 * other.a0 + a1 * other.a1

    fun cross(
        other: Vector2,
    ): Double = a0 * other.a1 - a1 * other.a0

    fun toList(): List<Double> = listOf(
        a0,
        a1,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Vector2 -> false
        !a0.equalsWithTolerance(
            other = other.a0,
            tolerance = tolerance,
        ) -> false

        !a1.equalsWithTolerance(
            other = other.a1,
            tolerance = tolerance,
        ) -> false

        else -> true
    }

    fun toReprString(): String = "Vector2($a0, $a1)"
}

operator fun Double.times(
    vector: Vector2,
): Vector2 = vector * this
