package dev.toolkt.math

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.divideWithTolerance
import dev.toolkt.core.numeric.equalsWithTolerance

data class Ratio(
    val nominator: Double,
    val denominator: Double,
) : NumericObject {
    companion object {
        val ZeroByZero = Ratio(
            nominator = 0.0,
            denominator = 0.0,
        )
    }

    val valueOrNull: Double?
        get() = nominator.divideWithTolerance(denominator)

    val value: Double
        get() = nominator / denominator

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Ratio -> false
        !nominator.equalsWithTolerance(other.nominator, tolerance = tolerance) -> false
        !denominator.equalsWithTolerance(other.denominator, tolerance = tolerance) -> false
        else -> true
    }
}
