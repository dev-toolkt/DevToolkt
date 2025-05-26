package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.geometry.findProjectionScale
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.divideWithTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitLineFunction
import dev.toolkt.core.math.avgOf

/**
 * Represents a line in 2D space in parametric form: p = s + d * t
 *
 * Given a t-value, it returns the point on the line at that t-value.
 */
data class ParametricLineFunction(
    val d: Vector2,
    val s: Vector2,
) : ParametricCurveFunction() {
    companion object {
        fun of(
            point0: Vector2,
            point1: Vector2,
        ): ParametricLineFunction {
            val d = point1 - point0

            return ParametricLineFunction(
                d = d,
                s = point0,
            )
        }
    }

    override fun apply(a: Double): Vector2 {
        val t = a
        return s + d * t
    }

    override fun toParametricPolynomial() = ParametricPolynomial.Companion.linear(
        a1 = d,
        a0 = s,
    )

    override fun implicitize(): ImplicitLineFunction = ImplicitLineFunction(
        a = d.y,
        b = -d.x,
        c = d.cross(s),
    )

    /**
     * Solve the intersection of two lines
     *
     * @return The intersection t-value for this curve
     */
    fun solveIntersection(
        other: ParametricLineFunction,
    ): Double? = solveIntersectionEquation(other).singleOrNull()

    /**
     * Solve the equation s + d * t = p for t
     *
     * @return the t-value for the [point] if it lies on the line, a
     * t-value of a point lying on the line close to [point] (but _not_ an
     * actual projection) if [point] does not lye on the line, `null` if this
     * line degenerates to a point
     */
    override fun locatePoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? {
        val tx: Double? = (point.x - s.x).divideWithTolerance(
            d.x,
            tolerance = tolerance,
        )

        val ty: Double? = (point.y - s.y).divideWithTolerance(
            d.y,
            tolerance = tolerance,
        )

        return when {
            tx != null && ty != null -> {
                // If the point we locate is close tho the curve, p(avg(t_x, t_y))
                // is a point on the curve that is closer to the located point
                // than either p(t_x) or p(t_y)
                avgOf(tx, ty)
            }

            else -> tx ?: ty
        }
    }

    override fun projectPoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute
    ): Double? {
        val sp = point - s
        return sp.findProjectionScale(d, tolerance = tolerance)
    }

    override fun toReprString(): String {
        return """
            |ParametricLineFunction(
            |  d = ${d.toReprString()},
            |  s = ${s.toReprString()},
            |)
        """.trimMargin()
    }

    val point0: Vector2
        get() = s

    val point1: Vector2
        get() = s + d
}
