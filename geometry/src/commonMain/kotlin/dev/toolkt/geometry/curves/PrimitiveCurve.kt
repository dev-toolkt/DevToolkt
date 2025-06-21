package dev.toolkt.geometry.curves

import dev.toolkt.core.ReprObject
import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.splines.OpenSpline
import dev.toolkt.geometry.splines.Spline
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericObject.Tolerance
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction

abstract class PrimitiveCurve : OpenCurve() {
    abstract class Edge : NumericObject, ReprObject {
        companion object;

        abstract fun bind(
            start: Point,
            end: Point,
        ): PrimitiveCurve

        fun semiBind(
            end: Point,
        ): Spline.Link {
            return Spline.Link(
                edge = this,
                end = end,
            )
        }

        abstract fun transformBy(
            transformation: Transformation,
        ): Edge
    }

    companion object {
        /**
         * Finds intersections between two curves by solving the intersection
         * equation
         *
         * @param simpleSubjectCurve a curve that's not more complex than [complexObjectCurve]
         * @param complexObjectCurve a curve that's not simpler than [simpleSubjectCurve]
         */
        fun findIntersectionsByEquationSolving(
            simpleSubjectCurve: PrimitiveCurve,
            complexObjectCurve: PrimitiveCurve,
            tolerance: Tolerance.Absolute,
        ): Set<Intersection> {
            // Solve the intersection equation for the curves (for t ∈ ℝ)
            val tValues = simpleSubjectCurve.basisFunction.solveIntersectionEquation(
                other = complexObjectCurve.basisFunction,
            )

            // Filter out intersections outside either curve
            return tValues.mapNotNull { tSimple ->
                val coordSimple = Coord.of(t = tSimple) ?: return@mapNotNull null

                val potentialIntersectionPoint = simpleSubjectCurve.evaluate(coord = coordSimple)

                val tComplex = complexObjectCurve.basisFunction.locatePoint(
                    point = potentialIntersectionPoint.pointVector,
                    tolerance = tolerance,
                ) ?: throw UnsupportedOperationException("Cannot find t for the complex curve")

                val coordComplex = Coord.of(t = tComplex) ?: return@mapNotNull null

                object : Intersection() {
                    override val point = potentialIntersectionPoint

                    override val subjectCoord: Coord = coordSimple

                    override val objectCoord: Coord = coordComplex
                }
            }.toSet()
        }
    }

    // TODO: Make this final
    final override val subCurves: List<PrimitiveCurve>
        get() = listOf(this)

    final override val pathFunction: FeatureFunction<Point> by lazy {
        FeatureFunction.wrap(basisFunction).map { vector ->
            Point(pointVector = vector)
        }
    }

    private val basisFunctionDerivative: ParametricPolynomial<*> by lazy {
        basisFunction.findDerivative()
    }

    override val tangentDirectionFunction: FeatureFunction<Direction?> by lazy {
        FeatureFunction.wrap(basisFunctionDerivative).map { vector ->
            Direction.normalize(vector)
        }
    }

    fun connectsSmoothly(
        nextCurve: PrimitiveCurve,
    ): Boolean {
        require(end == nextCurve.start)

        val endTangent =
            this.endTangent ?: throw IllegalStateException("Cannot check smoothness of a curve with no end tangent")

        val nextStartTangent = nextCurve.startTangent
            ?: throw IllegalStateException("Cannot check smoothness of a curve with no start tangent")

        return endTangent.equalsWithRadialTolerance(nextStartTangent)
    }

    abstract override fun transformBy(
        transformation: Transformation,
    ): PrimitiveCurve

    final override fun findIntersectionsOpenSpline(
        subjectSpline: OpenSpline,
    ): Set<Intersection> = Intersection.swap(
        OpenSpline.findIntersections(
            subjectPrimitiveCurve = this,
            objectOpenSpline = subjectSpline,
        ),
    )

    final override fun findIntersectionsLineSegment(
        subjectLineSegment: LineSegment,
    ): Set<Intersection> = PrimitiveCurve.findIntersectionsByEquationSolving(
        // Line segment is never more complex than other primitive curves
        simpleSubjectCurve = subjectLineSegment,
        complexObjectCurve = this,
        tolerance = Tolerance.Default,
    )

    abstract val basisFunction: ParametricCurveFunction

    abstract val edge: Edge

    abstract override fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve>

    abstract fun evaluate(coord: Coord): Point

    /**
     * Locate the point on the curve
     *
     * @return If the [point] is on the curve, coordinate of that point. If the
     * point is not on the curve, `null`
     */
    abstract fun locatePoint(point: Point): Coord?
}
