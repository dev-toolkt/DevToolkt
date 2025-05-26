package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.RealFunction
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix3x2
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.times
import dev.toolkt.math.algebra.solveEqualityByBisection
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.SubCubicParametricPolynomial
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCurveFunction
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricLineFunction
import dev.toolkt.core.math.sq
import kotlin.math.ln
import kotlin.math.sqrt

data class QuadraticBezierBinomial(
    val point0: Vector2,
    val point1: Vector2,
    val point2: Vector2,
) : BezierBinomial() {
    constructor(
        pointMatrix: Matrix3x2,
    ) : this(
        point0 = pointMatrix.row0,
        point1 = pointMatrix.row1,
        point2 = pointMatrix.row2,
    )

    val pointMatrix: Matrix3x2
        get() = Matrix3x2(
            row0 = point0,
            row1 = point1,
            row2 = point2,
        )

    private val delta0: Vector2
        get() = point1 - point0

    private val delta1: Vector2
        get() = point2 - point1

    override fun toParametricPolynomial(): SubCubicParametricPolynomial = ParametricPolynomial.Companion.quadratic(
        a = point0 - 2.0 * point1 + point2,
        b = 2.0 * (point1 - point0),
        c = point0,
    )

    override fun locatePoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? {
        TODO("Not yet implemented")
    }

    override fun projectPoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? {
        TODO("Not yet implemented")
    }

    override fun implicitize(): ImplicitCurveFunction {
        TODO("Not yet implemented")
    }

    override fun apply(a: Double): Vector2 {
        val t = a

        val u = 1.0 - t
        val c1 = u * u * point0
        val c2 = 2.0 * u * t * point1
        val c3 = t * t * point2
        return c1 + c2 + c3
    }

    override fun toReprString(): String {
        return """
            |QuadraticBezierBinomial(
            |  point0 = ${point0.toReprString()},
            |  point1 = ${point1.toReprString()},
            |  point2 = ${point2.toReprString()},
            |)
        """.trimMargin()
    }

    val primaryArcLength: Double
        get() {
            val b = point1 - point0
            val f = point2 - point1
            val a = f - b

            val aMSq = a.magnitudeSquared // |A|^2
            val aM = sqrt(aMSq) // |A|

            val aMCb = aMSq * aM // |A|^3
            val bM = b.magnitude
            val fM = f.magnitude

            val ab = a.dot(b)
            val af = a.dot(f)

            val expr1 = (fM * af - bM * ab) / aMSq
            val expr2 = f.cross(b).sq / aMCb
            val expr3 = ln((aM * fM + af) / (aM * bM + ab))

            return expr1 + expr2 * expr3
        }

    val arcLengthFunction = object : RealFunction<Double> {
        override fun apply(a: Double): Double = calculateArcLengthUpTo(t = a)
    }

    fun calculateArcLengthUpTo(
        t: Double,
    ): Double {
        val b = point1 - point0
        val f = point2 - point1
        val a = f - b

        val aMSq = a.magnitudeSquared // |A|^2
        val aM = sqrt(aMSq) // |A|

        val d = a.dot(b) / aMSq
        val k = b.magnitudeSquared / aMSq - d.sq

        val u = t + d

        val sqrtUk = sqrt(u.sq + k)
        val sqrtDk = sqrt(d.sq + k)

        val expr1 = k * ln((u + sqrtUk) / (d + sqrtDk))
        val expr2 = (u * sqrtUk) - (d * sqrtDk)

        return aM * (expr1 + expr2)
    }

    /**
     * Find the t value for the given arc length in the primary range
     *
     * @return t value in the range [0, 1] or null if no solution is found
     * ([arcLength] > [primaryArcLength])
     */
    fun locateArcLength(
        arcLength: Double,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? {
        require(arcLength >= 0.0)

        return arcLengthFunction.solveEqualityByBisection(
            y = arcLength,
            range = primaryTRange,
            tolerance = tolerance,
        )
    }

    fun raise(): CubicBezierBinomial = CubicBezierBinomial(
        pointMatrix = CubicBezierBinomial.raiseMatrix * pointMatrix,
    )

    fun evaluatePartially(t: Double): ParametricLineFunction {
        val subPoint0 = point0 + delta0 * t
        val subPoint1 = point1 + delta1 * t

        return ParametricLineFunction.Companion.of(
            point0 = subPoint0,
            point1 = subPoint1,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is QuadraticBezierBinomial -> false
        !point0.equalsWithTolerance(other.point0, tolerance = tolerance) -> false
        !point1.equalsWithTolerance(other.point1, tolerance = tolerance) -> false
        !point2.equalsWithTolerance(other.point2, tolerance = tolerance) -> false
        else -> true
    }
}
