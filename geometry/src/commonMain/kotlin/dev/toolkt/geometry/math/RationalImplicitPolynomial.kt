package dev.toolkt.geometry.math

import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCurveFunction
import dev.toolkt.math.Ratio
import dev.toolkt.math.algebra.Function
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector2

data class RationalImplicitPolynomial(
    val nominatorFunction: ImplicitCurveFunction,
    val denominatorFunction: ImplicitCurveFunction,
) : Function<Vector2, Ratio>, NumericObject {
    override fun apply(v: Vector2): Ratio = Ratio(
        nominator = nominatorFunction.apply(v),
        denominator = denominatorFunction.apply(v),
    )

    fun applyOrNull(v: Vector2): Double? = apply(v).valueOrNull

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is RationalImplicitPolynomial -> false
        !nominatorFunction.equalsWithTolerance(other.nominatorFunction, tolerance = tolerance) -> false
        !denominatorFunction.equalsWithTolerance(other.denominatorFunction, tolerance = tolerance) -> false
        else -> true
    }
}
