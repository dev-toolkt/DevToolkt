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
 * A bilinear polynomial in the form a * x + b * y + c
 */
data class ImplicitLineFunction(
    val a: Double,
    val b: Double,
    val c: Double,
) : ImplicitCurveFunction() {
    companion object {
        fun of(
            a1: Double,
            b1: Double,
            c: Double,
        ): ImplicitLineFunction = ImplicitLineFunction(
            a = a1,
            b = b1,
            c = c,
        )
    }

    operator fun plus(
        other: ImplicitLineFunction,
    ): ImplicitLineFunction = ImplicitLineFunction(
        a = a + other.a,
        b = b + other.b,
        c = c + other.c,
    )

    operator fun unaryMinus(): ImplicitLineFunction = ImplicitLineFunction(
        a = -a,
        b = -b,
        c = -c,
    )

    operator fun minus(
        other: ImplicitLineFunction,
    ): ImplicitLineFunction = this + (-other)

    operator fun times(
        s: Double,
    ): ImplicitLineFunction = ImplicitLineFunction(
        a = a * s,
        b = b * s,
        c = c * s,
    )

    operator fun times(
        other: ImplicitLineFunction,
    ): ImplicitQuadraticCurveFunction = ImplicitQuadraticCurveFunction.of(
        a2 = a * other.a,
        a1b1 = a * other.b + b * other.a,
        b2 = b * other.b,
        a1 = a * other.c + c * other.a,
        b1 = b * other.c + c * other.b,
        c = c * other.c,
    )

    operator fun times(
        other: ImplicitQuadraticCurveFunction,
    ): ImplicitCubicCurveFunction = other * this

    fun substitute(
        x: Polynomial,
        y: Polynomial,
    ): Polynomial = a * x + b * y + c

    override fun substitute(
        p: ParametricPolynomial<*>,
    ): Polynomial = substitute(
        x = p.xPolynomial,
        y = p.yPolynomial,
    )

    override fun apply(
        v: Vector2,
    ): Double = a * v.x + b * v.y + c

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ImplicitLineFunction -> false
        !a.equalsWithTolerance(other.a, tolerance = tolerance) -> false
        !b.equalsWithTolerance(other.b, tolerance = tolerance) -> false
        !c.equalsWithTolerance(other.c, tolerance = tolerance) -> false
        else -> true
    }
}

operator fun Double.times(
    other: ImplicitLineFunction,
): ImplicitLineFunction = other * this
