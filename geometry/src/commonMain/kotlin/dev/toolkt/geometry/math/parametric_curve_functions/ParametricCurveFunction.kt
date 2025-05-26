package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.RealFunction
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.sample
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCurveFunction
import dev.toolkt.core.iterable.LinSpace

abstract class ParametricCurveFunction : RealFunction<Vector2> {
    companion object {
        val primaryTRange = 0.0..1.0
    }

    data class Sample(
        val t: Double,
        val point: Vector2,
    )

    fun findCriticalPoints(): ParametricPolynomial.RootSet = findDerivative().findRoots()

    fun findRoots(): ParametricPolynomial.RootSet = toParametricPolynomial().findRoots()

    /**
     * Solve the intersection of this parametric curve with another parametric curve.
     * It's preferred that this curve is the simpler of two curves.
     *
     * @return A set of intersection parameter values t for this curve.
     */
    fun solveIntersectionEquation(
        other: ParametricCurveFunction,
    ): List<Double> {
        val otherImplicit = other.implicitize()
        val thisParametric = this.toParametricPolynomial()
        val intersectionPolynomial = otherImplicit.substitute(thisParametric)

        // If this curve and the other curve are _the same curve_ (curves
        // sharing the counter-domain of points), the intersection polynomial
        // is unreliable

        return intersectionPolynomial.findTValueRoots(
            guessedTValue = 0.5,
            tolerance = NumericObject.Tolerance.Default,
        )
    }

    fun sample(n: Int): List<Sample> = this.sample(
        linSpace = LinSpace(sampleCount = n)
    ).map {
        Sample(t = it.a, point = it.b)
    }

    fun findDerivative(): ParametricPolynomial<*> = toParametricPolynomial().findDerivative()

    protected fun Polynomial.findTValueRoots(
        guessedTValue: Double,
        tolerance: NumericObject.Tolerance.Absolute,
    ): List<Double> = this.findRoots(
        guessedRoot = guessedTValue,
        tolerance = tolerance,
        areClose = { t0, t1 ->
            val p0 = this@ParametricCurveFunction.apply(t0)
            val p1 = this@ParametricCurveFunction.apply(t1)

            (p0 - p1).magnitude.equalsWithTolerance(0.0)
        },
    )

    val primaryArcLengthApproximate: Double
        get() = calculatePrimaryArcLengthBruteForce(sampleCount = 128)

    val primaryArcLengthNearlyExact: Double
        get() = calculatePrimaryArcLengthBruteForce()

    fun calculatePrimaryArcLengthBruteForce(
        range: ClosedFloatingPointRange<Double> = 0.0..1.0,
        sampleCount: Int = 8192,
    ): Double = LinSpace.generateSubRanges(
        range = range,
        sampleCount = sampleCount,
    ).sumOf { tRange ->
        val p0 = apply(tRange.start)
        val p1 = apply(tRange.endInclusive)
        Vector2.Companion.distance(p0, p1)
    }

    /**
     * Locate a [point] lying on the curve.
     *
     * @return If the [point] is on the curve, the t-value for that point. If the
     * point is not on the curve, a reasonable approximation of the t-value of the
     * point on the curve closest to [point]. If the t-value could not be found
     * (because the curve self-intersects at [point] or is strongly degenerate), null.
     */
    abstract fun locatePoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double?

    /**
     * Project a [point] onto the curve.
     *
     * @return The t-value of the point on the curve closest to [point]. If the
     * t-value could not be found, null.
     */
    abstract fun projectPoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double?

    /**
     * Convert this parametric curve function to an implicit curve function.
     */
    abstract fun implicitize(): ImplicitCurveFunction

    abstract fun toParametricPolynomial(): ParametricPolynomial<*>

    abstract fun toReprString(): String
}
