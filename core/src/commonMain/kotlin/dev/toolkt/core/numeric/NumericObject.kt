package dev.toolkt.core.numeric

import dev.toolkt.core.numeric.NumericObject.Tolerance
import kotlin.jvm.JvmName
import kotlin.math.abs

interface NumericObject {
    sealed class Tolerance {
        data object Zero : Tolerance() {
            override fun equalsApproximately(
                value: Double, reference: Double
            ): Boolean = value == reference
        }

        data class Absolute(
            val absoluteTolerance: Double,
        ) : Tolerance() {
            operator fun times(factor: Double) = Absolute(
                absoluteTolerance = absoluteTolerance * factor,
            )

            override fun equalsApproximately(
                value: Double,
                reference: Double,
            ): Boolean = abs(value - reference) <= absoluteTolerance
        }

        data class Relative(
            val relativeTolerance: Double,
        ) : Tolerance() {
            init {
                require(relativeTolerance > 0.0 && relativeTolerance < 0.25)
            }

            override fun equalsApproximately(
                value: Double,
                reference: Double,
            ): Boolean = abs(value - reference) <= relativeTolerance * abs(reference)
        }

        companion object {
            val Default = Absolute(
                absoluteTolerance = 10e-6,
            )
        }

        abstract fun equalsApproximately(
            value: Double,
            reference: Double,
        ): Boolean
    }

    fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance = Tolerance.Default,
    ): Boolean
}

fun <T : NumericObject> T?.equalsWithToleranceOrNull(
    other: NumericObject?,
    tolerance: Tolerance = Tolerance.Default,
): Boolean = when {
    this != null && other != null -> equalsWithTolerance(
        other = other,
        tolerance = tolerance,
    )

    this == null && other == null -> true

    else -> false
}

fun Double.equalsZeroWithTolerance(
    tolerance: Tolerance.Absolute = Tolerance.Default,
): Boolean = tolerance.equalsApproximately(this, 0.0)

fun Double.equalsWithTolerance(
    other: Double,
    tolerance: Tolerance = Tolerance.Default,
): Boolean = tolerance.equalsApproximately(this, other)

fun Double.divideWithTolerance(
    divisor: Double,
    tolerance: Tolerance = Tolerance.Default,
): Double? = when {
    divisor.equalsWithTolerance(
        0.0,
        tolerance = tolerance
    ) -> null

    else -> this / divisor
}

@JvmName("equalsWithToleranceListDouble")
fun List<Double>.equalsWithTolerance(
    other: List<Double>,
    tolerance: Tolerance = Tolerance.Default,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceOrNullListDouble")
fun List<Double>?.equalsWithToleranceOrNull(
    other: List<Double>?,
    tolerance: Tolerance = Tolerance.Default,
): Boolean = when {
    this != null && other != null -> equalsWithTolerance(
        other = other,
        tolerance = tolerance,
    )

    this == null && other == null -> true

    else -> false
}

@JvmName("equalsWithToleranceListNumericObject")
fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    tolerance: Tolerance = Tolerance.Default,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}
