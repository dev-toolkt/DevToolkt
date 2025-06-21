package dev.toolkt.geometry

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericObject.Tolerance
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricLineFunction
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.math.algebra.linear.vectors.times

/**
 * A line segment in the Euclidean plane. Typically, it can be considered a
 * distinguished section of a specific straight line, but if the [start] and
 * the [end] points are the same point, it can be considered a point lying on
 * infinitely many lines.
 */
data class LineSegment(
    override val start: Point,
    override val end: Point,
) : PrimitiveCurve() {
    data object Edge : PrimitiveCurve.Edge() {
        override fun bind(
            start: Point,
            end: Point,
        ): PrimitiveCurve = LineSegment(
            start = start,
            end = end,
        )

        override fun transformBy(transformation: Transformation): Edge = this

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: Tolerance,
        ): Boolean = other == Edge

        override fun toString(): String = "LineSegment.Edge"

        override fun toReprString(): String = "LineSegment.Edge"
    }

    companion object {
        fun findIntersections(
            subjectLineSegment: LineSegment,
            objectLineSegment: LineSegment,
        ): Set<Intersection> {
            // The two line segments is not different from other primitive curves
            return LineSegment.findIntersections(
                subjectLineSegment = subjectLineSegment,
                objectPrimitiveCurve = objectLineSegment,
            )
        }

        /**
         * Finds intersections between a line segment and any primitive curve
         */
        internal fun findIntersections(
            subjectLineSegment: LineSegment,
            objectPrimitiveCurve: PrimitiveCurve,
        ): Set<Intersection> {
            // The line segment is always the simple curve (at least not more
            // complex than the other one) and equation solving is the only
            // reasonable strategy in this case
            return PrimitiveCurve.findIntersectionsByEquationSolving(
                simpleSubjectCurve = subjectLineSegment,
                complexObjectCurve = objectPrimitiveCurve,
                tolerance = Tolerance.Default,
            )
        }
    }

    val length: Span
        get() = Point.distanceBetween(start, end)

    override val basisFunction = ParametricLineFunction(
        s = start.pointVector,
        d = end.pointVector - start.pointVector,
    )

    override val edge = Edge

    override fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve> {
        TODO("Not yet implemented")
    }

    override fun findOffsetCurve(
        offset: Double,
    ): LineSegment {
        val normal = this.normalDirection
            ?: throw IllegalStateException("Cannot find offset curve of a line segment with no normal")

        return transformBy(
            transformation = PrimitiveTransformation.Translation.inDirection(
                direction = normal,
                distance = Span.of(value = offset),
            )
        )
    }

    override fun findBoundingBox(): BoundingBox = BoundingBox.of(
        pointA = start,
        pointB = end,
    )

    override fun evaluate(
        coord: Coord,
    ): Point = Point(
        pointVector = start.pointVector + coord.t * (end.pointVector - start.pointVector),
    )

    override fun locatePoint(point: Point): Coord? {
        val tValue = basisFunction.locatePoint(
            point.pointVector,
            tolerance = Tolerance.Default,
        ) ?: return when {
            // If a line is degenerated, _all_ t-values are a good answer, but
            // we don't want to say that the point is not on the curve when it
            // kind of is
            start.equalsWithTolerance(point) -> Coord.start

            else -> null
        }

        return Coord.of(t = tValue)
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is LineSegment -> false
        !start.equalsWithTolerance(other.start, tolerance = tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance = tolerance) -> false
        else -> true
    }

    val containingLine: Line?
        get() = Line.throughPoints(
            point0 = start,
            point1 = end,
        )

    val tangentDirection: Direction?
        get() = start.directionTo(end)

    val normalDirection: Direction?
        get() = tangentDirection?.normal

    override val tangentDirectionFunction = FeatureFunction.constant(tangentDirection)

    override fun transformBy(
        transformation: Transformation,
    ): LineSegment = LineSegment(
        start = start.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    override fun findIntersections(
        objectCurve: OpenCurve,
    ): Set<Intersection> = objectCurve.findIntersectionsLineSegment(
        subjectLineSegment = this,
    )

    override fun findIntersectionsBezierCurve(
        subjectBezierCurve: BezierCurve,
    ): Set<Intersection> = Intersection.swap(
        intersections = PrimitiveCurve.findIntersectionsByEquationSolving(
            simpleSubjectCurve = this,
            complexObjectCurve = subjectBezierCurve,
            tolerance = Tolerance.Default,
        ),
    )

    override fun toReprString(): String {
        return """
            |LineSegment(
            |  start = ${start.toReprString()},
            |  end = ${end.toReprString()},
            |)
        """.trimMargin()
    }
}
