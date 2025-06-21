package dev.toolkt.geometry

import dev.toolkt.core.ReprObject
import dev.toolkt.core.indentLater
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricLineFunction

/**
 * A ray in 2D Euclidean space, described by the equation p = s + td for t >= 0
 */
class Ray(
    internal val startingPoint: Point,
    internal val direction: Direction,
) : ReprObject {
    private val basisFunction = ParametricLineFunction(
        s = startingPoint.pointVector,
        d = direction.normalizedDirectionVector,
    )

    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Ray = Ray(
            startingPoint = point,
            direction = direction,
        )
    }

    val opposite: Ray
        get() = Ray(
            startingPoint = startingPoint,
            direction = direction.opposite,
        )

    fun findIntersection(
        other: Ray,
    ): Point? {
        val l0 = basisFunction
        val l1 = other.basisFunction

        val t0 = l0.solveIntersection(l1) ?: return null
        if (t0 < 0.0) return null

        val potentialIntersectionPoint = l0.apply(t0)

        val t1 = l1.locatePoint(
            potentialIntersectionPoint,
            tolerance = NumericObject.Tolerance.Default,
        ) ?: return null

        if (t1 < 0.0) return null

        return Point(
            pointVector = potentialIntersectionPoint,
        )
    }

    override fun toReprString(): String {
        return """
            |Ray(
            |  startingPoint = ${startingPoint.toReprString()},
            |  direction = ${direction.toReprString().indentLater()},
            |)
        """.trimMargin()
    }
}
