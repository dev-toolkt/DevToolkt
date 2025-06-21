package dev.toolkt.geometry.math.implicit_curve_functions

import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.polynomials.plus
import dev.toolkt.math.algebra.polynomials.times

/**
 * A cubic bivariate polynomial in the form...
 *
 * a3 * x^3 +
 * a2b1 * x^2y +
 * a1b2 * xy^2 +
 * b3 * y^3 +
 * a2 * x^2 +
 * a1b1 * xy +
 * b2 * y^2 +
 * a1 * x +
 * b1 * y +
 * c
 */
data class ImplicitCubicCurveFunction(
    val a3: Double,
    val a2b1: Double,
    val a1b2: Double,
    val b3: Double,
    val a2: Double,
    val a1b1: Double,
    val b2: Double,
    val a1: Double,
    val b1: Double,
    val c: Double,
) : ImplicitCurveFunction() {
    companion object {
        fun of(
            a3: Double,
            a2b1: Double,
            a1b2: Double,
            b3: Double,
            a2: Double,
            a1b1: Double,
            b2: Double,
            a1: Double,
            b1: Double,
            c: Double,
        ): ImplicitCubicCurveFunction = ImplicitCubicCurveFunction(
            a3 = a3,
            a2b1 = a2b1,
            a1b2 = a1b2,
            b3 = b3,
            a2 = a2,
            a1b1 = a1b1,
            b2 = b2,
            a1 = a1,
            b1 = b1,
            c = c,
        )
    }

    operator fun plus(
        other: ImplicitCubicCurveFunction,
    ): ImplicitCubicCurveFunction = ImplicitCubicCurveFunction(
        a3 = a3 + other.a3,
        a2b1 = a2b1 + other.a2b1,
        a1b2 = a1b2 + other.a1b2,
        b3 = b3 + other.b3,
        a2 = a2 + other.a2,
        a1b1 = a1b1 + other.a1b1,
        b2 = b2 + other.b2,
        a1 = a1 + other.a1,
        b1 = b1 + other.b1,
        c = c + other.c,
    )

    operator fun plus(
        other: ImplicitQuadraticCurveFunction,
    ) = ImplicitCubicCurveFunction(
        a3 = a3,
        a2b1 = a2b1,
        a1b2 = a1b2,
        b3 = b3,
        a2 = a2 + other.a2,
        a1b1 = a1b1 + other.a1b1,
        b2 = b2 + other.b2,
        a1 = a1 + other.a1,
        b1 = b1 + other.b1,
        c = c + other.c,
    )

    operator fun unaryMinus(): ImplicitCubicCurveFunction = ImplicitCubicCurveFunction(
        a3 = -a3,
        a2b1 = -a2b1,
        a1b2 = -a1b2,
        b3 = -b3,
        a2 = -a2,
        a1b1 = -a1b1,
        b2 = -b2,
        a1 = -a1,
        b1 = -b1,
        c = -c,
    )

    operator fun minus(
        other: ImplicitCubicCurveFunction,
    ): ImplicitCubicCurveFunction = this + (-other)

    override fun substitute(
        parametricPolynomial: ParametricPolynomial<*>,
    ): Polynomial {
        val x = parametricPolynomial.xPolynomial
        val y = parametricPolynomial.yPolynomial

        return a3 * (x * x * x) + a2b1 * (x * x * y) + a1b2 * (x * y * y) + b3 * (y * y * y) + a2 * (x * x) + a1b1 * (x * y) + b2 * (y * y) + a1 * x + b1 * y + c
    }

    override fun apply(
        v: Vector2,
    ): Double {
        val x = v.x
        val y = v.y

        return a3 * (x * x * x) + a2b1 * (x * x * y) + a1b2 * (x * y * y) + b3 * (y * y * y) + a2 * (x * x) + a1b1 * (x * y) + b2 * (y * y) + a1 * x + b1 * y + c
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ImplicitCubicCurveFunction -> false
        !a3.equalsWithTolerance(other.a3, tolerance = tolerance) -> false
        !a2b1.equalsWithTolerance(other.a2b1, tolerance = tolerance) -> false
        !a1b2.equalsWithTolerance(other.a1b2, tolerance = tolerance) -> false
        !b3.equalsWithTolerance(other.b3, tolerance = tolerance) -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        !a1b1.equalsWithTolerance(other.a1b1, tolerance = tolerance) -> false
        !b2.equalsWithTolerance(other.b2, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !b1.equalsWithTolerance(other.b1, tolerance = tolerance) -> false
        !c.equalsWithTolerance(other.c, tolerance = tolerance) -> false
        else -> true
    }
}
