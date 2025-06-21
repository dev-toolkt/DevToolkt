package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.matrices.matrix2.Matrix4x2
import dev.toolkt.math.algebra.linear.matrices.matrix2.MatrixNx2
import dev.toolkt.math.algebra.linear.matrices.matrix3.Matrix3x3
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix3x4
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x3
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4
import dev.toolkt.math.algebra.linear.matrices.matrix4.MatrixNx4
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.Vector4
import dev.toolkt.math.algebra.linear.vectors.times
import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.geometry.math.LowParametricPolynomial
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.RationalImplicitPolynomial
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCubicCurveFunction
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitLineFunction
import dev.toolkt.geometry.math.implicit_curve_functions.times
import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.iterable.partitionAtCenter
import dev.toolkt.core.iterable.withNeighboursOrNull
import dev.toolkt.math.minByUnimodalWithSelectee
import dev.toolkt.math.minByWithSelecteeOrNull

data class CubicBezierBinomial(
    val point0: Vector2,
    val point1: Vector2,
    val point2: Vector2,
    val point3: Vector2,
) : BezierBinomial() {
    companion object {
        const val n = 3

        /**
         * The characteristic matrix of the cubic Bézier curve.
         */
        val characteristicMatrix = Matrix4x4.Companion.rowMajor(
            row0 = Vector4(-1.0, 3.0, -3.0, 1.0),
            row1 = Vector4(3.0, -6.0, 3.0, 0.0),
            row2 = Vector4(-3.0, 3.0, 0.0, 0.0),
            row3 = Vector4(1.0, 0.0, 0.0, 0.0),
        )

        /**
         * A matrix for raising a quadratic curve to a cubic curve
         */
        val raiseMatrix = Matrix4x3.Companion.rowMajor(
            row0 = Vector3(1.0, 0.0, 0.0),
            row1 = Vector3(1.0 / 3.0, 2.0 / 3.0, 0.0),
            row2 = Vector3(0.0, 2.0 / 3.0, 1.0 / 3.0),
            row3 = Vector3(0.0, 0.0, 1.0),
        )

        val raiseMatrixPseudoInverse: Matrix3x4 = raiseMatrix.pseudoInverse()

        val characteristicMatrixInverted =
            characteristicMatrix.invert() ?: throw AssertionError("The characteristic matrix is not invertible")

        fun bestFit(
            samples: List<Sample>,
        ): CubicBezierBinomial {
            val pMatrix = MatrixNx2(
                rows = samples.map { it.point },
            )

            // T
            val tMatrix = MatrixNx4(
                rows = samples.map { it ->
                    CubicPolynomial.Companion.monomialVector(it.t)
                },
            )

            // (M^-1) * (T^t * T)^-1 * T^t
            val dMatrix = characteristicMatrixInverted * tMatrix.pseudoInverse()

            // P (control points)
            val pointMatrix = dMatrix * pMatrix

            return CubicBezierBinomial(
                pointMatrix = pointMatrix,
            )
        }
    }

    constructor(
        pointMatrix: Matrix4x2,
    ) : this(
        point0 = pointMatrix.row0,
        point1 = pointMatrix.row1,
        point2 = pointMatrix.row2,
        point3 = pointMatrix.row3,
    )

    val pointMatrix: Matrix4x2
        get() = Matrix4x2(
            row0 = point0,
            row1 = point1,
            row2 = point2,
            row3 = point3,
        )

    private val delta0: Vector2
        get() = point1 - point0

    private val delta1: Vector2
        get() = point2 - point1

    private val delta2: Vector2
        get() = point3 - point2

    private val x0: Double
        get() = point0.x

    private val y0: Double
        get() = point0.y

    private val x1: Double
        get() = point1.x

    private val y1: Double
        get() = point1.y

    private val x2: Double
        get() = point2.x

    private val y2: Double
        get() = point2.y

    private val x3: Double
        get() = point3.x

    private val y3: Double
        get() = point3.y

    private val l32: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y3 - 3 * y2,
            b = 3 * x2 - 3 * x3,
            c = 3 * x3 * y2 - 3 * x2 * y3,
        )

    private val l31: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y3 - 3 * y1,
            b = 3 * x1 - 3 * x3,
            c = 3 * x3 * y1 - 3 * x1 * y3,
        )

    private val l30: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = y3 - y0,
            b = x0 - x3,
            c = x3 * y0 - x0 * y3,
        )

    private val l21: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 9 * y2 - 9 * y1,
            b = 9 * x1 - 9 * x2,
            c = 9 * x2 * y1 - 9 * x1 * y2,
        )

    private val l20: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y2 - 3 * y0,
            b = 3 * x0 - 3 * x2,
            c = 3 * x2 * y0 - 3 * x0 * y2,
        )

    private val l10: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y1 - 3 * y0,
            b = 3 * x0 - 3 * x1,
            c = 3 * x1 * y0 - 3 * x0 * y1,
        )

    /**
     * Find the polynomial B(t) . B'(t)
     */
    fun findPointProjectionPolynomial(
        g: Vector2,
    ): Polynomial {
        val p0 = point0 - g
        val p1 = point1 - g
        val p2 = point2 - g
        val p3 = point3 - g

        val a = p3 - 3.0 * p2 + 3.0 * p1 - p0
        val b = 3.0 * p2 - 6.0 * p1 + 3.0 * p0
        val c = 3.0 * (p1 - p0)
        val d = p0

        return Polynomial.Companion.normalized(
            c.dot(d),
            c.dot(c) + 2.0 * b.dot(d),
            3.0 * b.dot(c) + 3.0 * a.dot(d),
            4.0 * a.dot(c) + 2.0 * b.dot(b),
            5.0 * a.dot(b),
            3.0 * a.dot(a),
        )
    }

    fun evaluatePartially(t: Double): QuadraticBezierBinomial {
        val subPoint0 = point0 + delta0 * t
        val subPoint1 = point1 + delta1 * t
        val subPoint2 = point2 + delta2 * t

        return QuadraticBezierBinomial(
            point0 = subPoint0,
            point1 = subPoint1,
            point2 = subPoint2,
        )
    }

    fun isFullyOverlapping(
        other: CubicBezierBinomial,
    ): Boolean = normalize().equalsWithTolerance(other.normalize())

    fun normalize(): ParametricPolynomial<*> = toParametricPolynomial().normalize()

    override fun toParametricPolynomial(): LowParametricPolynomial = ParametricPolynomial.Companion.cubic(
        a3 = -point0 + 3.0 * point1 - 3.0 * point2 + point3,
        a2 = 3.0 * point0 - 6.0 * point1 + 3.0 * point2,
        a1 = -3.0 * point0 + 3.0 * point1,
        a0 = point0,
    )

    override fun apply(a: Double): Vector2 {
        val t = a

        val u = 1.0 - t
        val c1 = u * u * u * point0
        val c2 = 3.0 * u * u * t * point1
        val c3 = 3.0 * u * t * t * point2
        val c4 = t * t * t * point3
        return c1 + c2 + c3 + c4
    }

    fun applyFast(t: Double): Vector2 {
        val quadraticBezierBinomial = evaluatePartially(t = t)
        val lineFunction = quadraticBezierBinomial.evaluatePartially(t = t)
        return lineFunction.apply(t)
    }

    override fun locatePoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? {
        // TODO: Move the responsibility of hybrid approaches to a higher layer
        return locatePointByInversionWithControlCheck(
            point = point,
            tolerance = tolerance,
        ) ?: locatePointByProjection(
            point = point,
            tolerance = tolerance,
        )
    }

    // TODO: Nuke the control check, simplify the verification, move to a higher
    //  layer
    private fun locatePointByInversionWithControlCheck(
        point: Vector2,
        tolerance: NumericObject.Tolerance,
    ): Double? {
        val eps = 10e-8

        val locatedTValue = locatePointByInversion(
            point = point,
        )

        // A control check with an extremely close point
        val locatedControlTValue = locatePointByInversion(
            point = point + Vector2(eps, eps),
        )

        return when {
            locatedTValue == null || locatedControlTValue == null -> null

            locatedTValue.equalsWithTolerance(
                locatedControlTValue,
                tolerance = tolerance,
            ) -> locatedTValue

            // If the t-value for the control point is not remotely close to the located t-value, we're near the
            // self-intersection and the results cannot be trusted
            else -> null
        }
    }

    /**
     * Solve the equation p(t) = p0
     *
     * @return either...
     * - a t-value (t-value of the [point] if it lies on the curve or a t-value
     * of a point lying on the curve close to [point], but not an actual projection,
     * or a t-value of a quasi-random point if [point] is close to the self-intersection)
     * - `null` if the t-value ćouldn't be found, because the curve degenerates
     * to a line or a point, or if [point] is very close to the self-intersection
     */
    internal fun locatePointByInversion(
        point: Vector2,
    ): Double? = inverted?.applyOrNull(point)

    /**
     * @return the t-value (or one of t-values) for [point] if it lies on the
     * curve or `null` if the point doesn't seem to lie on the curve
     */
    internal fun locatePointByProjection(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? {
        // Find the t-values of all points on curve orthogonal to the given point
        // TODO: Some valid points aren't found because the polynomial root
        //       finding doesn't enter the complex domain
        val projectedTValues = projectPointAll(
            point = point,
            tolerance = tolerance,
        )

        // Sample those t-values, so we know both the t-values and their respective
        // points
        val projectedPointSamples = projectedTValues.map { t ->
            Sample(
                t = t,
                point = apply(t),
            )
        }

        // We pick only the points which are the same point we're looking for,
        // typically it should be one point if the given point lies on the
        // curve at all
        val acceptableSamples = projectedPointSamples.filter { sample ->
            sample.point.equalsWithTolerance(
                other = point,
                tolerance = tolerance,
            )
        }

        // We take the representative t-value, the smallest one
        return acceptableSamples.minOfOrNull { it.t }
    }

    data class PointProjection(
        val t: Double,
        val distance: Double,
    )

    /**
     * Project the [point] onto the curve within [range]
     *
     * @return the t-value of the point on the curve closest to [point] or
     * the range start/end value if the true closest t-value is outside of [range]
     */
    fun projectPointIteratively(
        range: ClosedFloatingPointRange<Double>,
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): PointProjection? {
        val (tStart, tMid, tEnd) = LinSpace.generate(
            range = range,
            sampleCount = 12,
        ).withNeighboursOrNull().minByOrNull { (_, tMid, _) ->
            Vector2.Companion.distanceSquared(
                apply(tMid),
                point,
            )
        }!!

        val (tFound, foundDistance) = when {
            tStart == null || tEnd == null -> {
                val tStartEffective = tStart ?: tMid
                val tEndEffective = tEnd ?: tMid

                (tStartEffective..tEndEffective).minByWithSelecteeOrNull { t ->
                    Vector2.Companion.distanceSquared(
                        apply(t),
                        point,
                    )
                } ?: return null
            }

            else -> (tStart..tEnd).minByUnimodalWithSelectee(
                tolerance = tolerance,
            ) { t ->
                Vector2.Companion.distance(
                    apply(t),
                    point,
                )
            }
        }

        return PointProjection(
            t = tFound,
            distance = foundDistance,
        )
    }

    private fun projectPointAll(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): List<Double> {
        val projectionPolynomial = findPointProjectionPolynomial(point)

        val guessedTValue = locatePointByInversionWithControlCheck(
            point = point,
            tolerance = tolerance,
        ) ?: 0.5

        val roots = projectionPolynomial.findTValueRoots(
            guessedTValue = guessedTValue,
            tolerance = tolerance,
        )

        return roots
    }

    fun projectPointClosest(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
    ): Double? {
        val tValues = projectPointAll(
            point = point,
            tolerance = tolerance,
        )

        return tValues.minByOrNull {
            Vector2.Companion.distance(
                apply(it),
                point,
            )
        }
    }

    fun splitAt(
        t: Double,
    ): Pair<CubicBezierBinomial, CubicBezierBinomial> {
        val quadraticBezierBinomial = evaluatePartially(t = t)
        val lineFunction = quadraticBezierBinomial.evaluatePartially(t = t)

        val midPoint = lineFunction.apply(t)

        return Pair(
            CubicBezierBinomial(
                point0 = point0,
                point1 = quadraticBezierBinomial.point0,
                point2 = lineFunction.point0,
                point3 = midPoint,
            ),
            CubicBezierBinomial(
                point0 = midPoint,
                point1 = lineFunction.point1,
                point2 = quadraticBezierBinomial.point2,
                point3 = point3,
            ),
        )
    }

    /**
     * @param tValuesSorted - a sorted list of t-values to split at
     */
    fun splitAtMultipleSorted(
        tValuesSorted: List<Double>,
    ): List<CubicBezierBinomial> {
        val partitioningResult =
            tValuesSorted.partitionAtCenter() ?: return listOf(this) // We're done, no more places to split

        val leftTValues = partitioningResult.previousElements
        val medianTValue = partitioningResult.innerElement
        val rightTValues = partitioningResult.nextElements

        val (leftSplitCurve, rightSplitCurve) = splitAt(
            t = medianTValue,
        )

        val leftCorrectedTValues = leftTValues.map { it / medianTValue }
        val rightCorrectedTValues = rightTValues.map { (it - medianTValue) / (1.0 - medianTValue) }

        val leftSubSplitCurves = leftSplitCurve.splitAtMultipleSorted(
            tValuesSorted = leftCorrectedTValues,
        )

        val rightSubSplitCurves = rightSplitCurve.splitAtMultipleSorted(
            tValuesSorted = rightCorrectedTValues,
        )

        val subCurves = leftSubSplitCurves + rightSubSplitCurves

        return subCurves
    }

    // TODO: Implement a hybrid algorithm (equation / iteration) on a given range
    //  + return distance
    override fun projectPoint(
        point: Vector2,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? = projectPointAll(
        point, tolerance,
    ).singleOrNull()

    /**
     * Find the inverse of the cubic Bézier curve, i.e. the function that maps
     * the point on the curve to the t-value.
     *
     * @return The inverted polynomial, or null if the curve is degenerate
     */
    private fun invert(): RationalImplicitPolynomial? {
        val denominator = 3.0 * Matrix3x3.Companion.rowMajor(
            row0 = point1.toVector3(),
            row1 = point2.toVector3(),
            row2 = point3.toVector3(),
        ).determinant

        if (denominator == 0.0) {
            return null
        }

        val nominator1 = Matrix3x3.Companion.rowMajor(
            row0 = point0.toVector3(),
            row1 = point1.toVector3(),
            row2 = point3.toVector3(),
        ).determinant

        val nominator2 = Matrix3x3.Companion.rowMajor(
            row0 = point0.toVector3(),
            row1 = point2.toVector3(),
            row2 = point3.toVector3(),
        ).determinant

        val c1 = nominator1 / denominator
        val c2 = -(nominator2 / denominator)

        val l10 = this.l10
        val l20 = this.l20
        val l21 = this.l21
        val l30 = this.l30
        val l31 = this.l31

        val la = c1 * l31 + c2 * (l30 + l21) + l20
        val lb = c1 * l30 + c2 * l20 + l10

        return RationalImplicitPolynomial(
            nominatorFunction = lb,
            denominatorFunction = lb - la,
        )
    }

    val inverted: RationalImplicitPolynomial? by lazy { invert() }

    fun lower(): QuadraticBezierBinomial = QuadraticBezierBinomial(
        pointMatrix = raiseMatrixPseudoInverse * pointMatrix,
    )

    override fun implicitize(): ImplicitCubicCurveFunction {
        val l32 = this.l32
        val l31 = this.l31
        val l30 = this.l30
        val l21 = this.l21
        val l20 = this.l20
        val l10 = this.l10

        return calculateDeterminant(
            a = l32,
            b = l31,
            c = l30,
            d = l31,
            e = l30 + l21,
            f = l20,
            g = l30,
            h = l20,
            i = l10,
        )
    }

    override fun toReprString(): String {
        return """
            |CubicBezierBinomial(
            |  point0 = ${point0.toReprString()},
            |  point1 = ${point1.toReprString()},
            |  point2 = ${point2.toReprString()},
            |  point3 = ${point3.toReprString()},
            |)
        """.trimMargin()
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is CubicBezierBinomial -> false
        !point0.equalsWithTolerance(other.point0, tolerance = tolerance) -> false
        !point1.equalsWithTolerance(other.point1, tolerance = tolerance) -> false
        !point2.equalsWithTolerance(other.point2, tolerance = tolerance) -> false
        !point3.equalsWithTolerance(other.point3, tolerance = tolerance) -> false
        else -> true
    }
}

/**
 * Calculate the determinant of a 3x3 polynomial matrix in the form:
 * | a b c |
 * | d e f |
 * | g h i |
 *
 * @return The determinant of the described matrix (a polynomial!)
 */
private fun calculateDeterminant(
    a: ImplicitLineFunction, b: ImplicitLineFunction, c: ImplicitLineFunction,
    d: ImplicitLineFunction, e: ImplicitLineFunction, f: ImplicitLineFunction,
    g: ImplicitLineFunction, h: ImplicitLineFunction, i: ImplicitLineFunction,
): ImplicitCubicCurveFunction = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)
