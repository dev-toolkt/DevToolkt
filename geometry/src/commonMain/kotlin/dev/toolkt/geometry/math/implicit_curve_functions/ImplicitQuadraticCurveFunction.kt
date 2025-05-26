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
 * A quadratic bivariate polynomial in the form...
 *
 * a2 * x^2 +
 * a1b1 * xy +
 * b2 * y^2 +
 * a1 * x +
 * b1 * y +
 * c
 */
data class ImplicitQuadraticCurveFunction(
    val a2: Double,
    val a1b1: Double,
    val b2: Double,
    val a1: Double,
    val b1: Double,
    val c: Double,
) : ImplicitCurveFunction() {
    companion object {
        fun of(
            a2: Double,
            a1b1: Double,
            b2: Double,
            a1: Double,
            b1: Double,
            c: Double,
        ): ImplicitQuadraticCurveFunction = ImplicitQuadraticCurveFunction(
            a2 = a2,
            a1b1 = a1b1,
            b2 = b2,
            a1 = a1,
            b1 = b1,
            c = c,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ImplicitQuadraticCurveFunction -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        !a1b1.equalsWithTolerance(other.a1b1, tolerance = tolerance) -> false
        !b2.equalsWithTolerance(other.b2, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !b1.equalsWithTolerance(other.b1, tolerance = tolerance) -> false
        !c.equalsWithTolerance(other.c, tolerance = tolerance) -> false
        else -> true
    }

    operator fun times(
        other: ImplicitLineFunction,
    ): ImplicitCubicCurveFunction = ImplicitCubicCurveFunction.of(
        a3 = a2 * other.a,
        a2b1 = a2 * other.b + a1b1 * other.a,
        a1b2 = a1b1 * other.b + b2 * other.a,
        b3 = b2 * other.b,
        a2 = a2 * other.c + a1 * other.a,
        a1b1 = a1b1 * other.c + a1 * other.b + b1 * other.a,
        b2 = b2 * other.c + b1 * other.b,
        a1 = a1 * other.c + c * other.a,
        b1 = b1 * other.c + c * other.b,
        c = c * other.c,
    )

    operator fun plus(
        other: ImplicitQuadraticCurveFunction,
    ): ImplicitQuadraticCurveFunction = ImplicitQuadraticCurveFunction(
        a2 = a2 + other.a2,
        a1b1 = a1b1 + other.a1b1,
        b2 = b2 + other.b2,
        a1 = a1 + other.a1,
        b1 = b1 + other.b1,
        c = c + other.c,
    )

    operator fun plus(
        other: ImplicitLineFunction,
    ): ImplicitQuadraticCurveFunction = ImplicitQuadraticCurveFunction(
        a2 = a2,
        a1b1 = a1b1,
        b2 = b2,
        a1 = a1 + other.a,
        b1 = b1 + other.b,
        c = c + other.c,
    )

    operator fun unaryMinus(): ImplicitQuadraticCurveFunction = ImplicitQuadraticCurveFunction(
        a2 = -a2,
        a1b1 = -a1b1,
        b2 = -b2,
        a1 = -a1,
        b1 = -b1,
        c = -c,
    )

    operator fun minus(
        other: ImplicitQuadraticCurveFunction,
    ): ImplicitQuadraticCurveFunction = this + (-other)

    operator fun minus(
        other: ImplicitLineFunction,
    ): ImplicitQuadraticCurveFunction = this + (-other)

    override fun apply(v: Vector2): Double {
        val x = v.x
        val y = v.y

        return a2 * (x * x) + a1b1 * (x * y) + b2 * (y * y) + a1 * x + b1 * y + c
    }

    override fun substitute(
        parametricPolynomial: ParametricPolynomial<*>,
    ): Polynomial {
        val x = parametricPolynomial.xPolynomial
        val y = parametricPolynomial.yPolynomial

        return a2 * (x * x) + a1b1 * (x * y) + b2 * (y * y) + a1 * x + b1 * y + c
    }

    operator fun minus(
        other: ImplicitCubicCurveFunction,
    ): ImplicitCubicCurveFunction = this + (-other)

    private operator fun plus(
        implicitCubicCurveFunction: ImplicitCubicCurveFunction,
    ): ImplicitCubicCurveFunction = implicitCubicCurveFunction + this
}
