package dev.toolkt.geometry

import kotlin.math.sqrt

/**
 * A measure of length or distance in 2D space.
 */
sealed class Span : SpatialObject, Comparable<Span> {
    data object Zero : Span() {
        override fun times(factor: Double): Span = Zero

        override val value: Double = 0.0

        override val valueSquared: Double = 0.0
    }

    data class Plain(
        override val value: Double,
    ) : Span() {
        override fun times(
            factor: Double,
        ): Span = Plain(
            value = value * factor,
        )

        override val valueSquared: Double = value * value
    }

    data class Squared(
        override val valueSquared: Double,
    ) : Span() {
        override fun times(
            factor: Double,
        ): Span = Squared(
            valueSquared = valueSquared * factor * factor,
        )

        override val value: Double
            get() = sqrt(valueSquared)
    }

    companion object {
        fun of(value: Double): Span = Span.Plain(
            value = value,
        )
    }

    override fun equalsWithSpatialTolerance(
        other: SpatialObject,
        tolerance: SpatialObject.SpatialTolerance,
    ): Boolean = when {
        other !is Span -> false
        else -> tolerance.equalsApproximately(this, other)
    }

    fun equalsApproximatelyZero(
        tolerance: SpatialObject.SpatialTolerance = SpatialObject.SpatialTolerance.default,
    ): Boolean = tolerance.equalsApproximatelyZero(this)

    override fun compareTo(
        other: Span,
    ): Int = valueSquared.compareTo(other.valueSquared)

    abstract operator fun times(factor: Double): Span

    operator fun div(other: Span): Double = sqrt(valueSquared / other.valueSquared)

    abstract val value: Double

    abstract val valueSquared: Double

}
