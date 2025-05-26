package dev.toolkt.geometry.splines

import dev.toolkt.geometry.BoundingBox
import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.LineSegment
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.indentLater
import dev.toolkt.core.toReprString
import dev.toolkt.core.iterable.mapCarrying
import dev.toolkt.core.iterable.uncons
import dev.toolkt.core.iterable.withPrevious

/**
 * A composite open curve assumed to be tangent-continuous (G1).
 */
data class OpenSpline(
    /**
     * The curve that starts this spline
     */
    val firstCurve: PrimitiveCurve,
    /**
     * A list of links, where each link starts at the end of the previous one,
     * and ends at the start of the next one. The first link starts at the end of
     * the first curve.
     */
    val trailingSequentialLinks: List<Spline.Link>,
) : OpenCurve(), Spline {
    companion object {
        fun OpenSpline(
            origin: Point,
            sequentialLinks: List<Spline.Link>,
        ): OpenSpline {
            val (firstLink, trailingLinks) = sequentialLinks.uncons()
                ?: throw IllegalArgumentException("The list of sequential links must not be empty")

            val firstCurve = firstLink.bind(start = origin)

            return OpenSpline(
                firstCurve = firstCurve,
                trailingSequentialLinks = trailingLinks,
            )
        }

        /**
         * @param sequentialCurves a list of curves, where each curves starts at
         * the end of the previous one, and ends at the start of the next one.
         *
         * @return an open spline in the shape of the given sequential curves
         */
        fun connect(
            sequentialCurves: List<OpenCurve>,
        ): OpenSpline {
            val sequentialPrimitiveCurves = sequentialCurves.flatMap { it.subCurves }

            val (firstCurve, trailingCurves) = sequentialPrimitiveCurves.uncons()
                ?: throw IllegalArgumentException("The list of sequential curves must not be empty")

            return OpenSpline(
                firstCurve = firstCurve,
                trailingSequentialLinks = trailingCurves.withPrevious(
                    outerLeft = firstCurve,
                ).map { (prevCurve, curve) ->
                    Spline.Link.connect(
                        prevCurve = prevCurve,
                        curve = curve,
                    )
                },
            )
        }

        fun findIntersections(
            subjectOpenSpline: OpenSpline,
            objectOpenSpline: OpenSpline,
            tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
        ): Set<Intersection> {

            TODO()
        }

        fun findIntersections(
            subjectPrimitiveCurve: PrimitiveCurve,
            objectOpenSpline: OpenSpline,
            tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
        ): Set<Intersection> {

            TODO()
        }
    }

    val origin: Point
        get() = firstCurve.start

    val sequentialCurves: List<PrimitiveCurve>
        get() = listOf(firstCurve) + trailingSequentialCurves

    private val trailingSequentialCurves: List<PrimitiveCurve>
        get() {
            val (trailingCurves, _) = trailingSequentialLinks.mapCarrying(
                initialCarry = firstCurve.end,
            ) { start, link ->
                Pair(
                    link.bind(start = start),
                    link.end,
                )
            }

            return trailingCurves
        }

    override fun transformBy(
        transformation: Transformation,
    ): OpenSpline = OpenSpline(
        firstCurve = firstCurve.transformBy(
            transformation = transformation,
        ),
        trailingSequentialLinks = trailingSequentialLinks.map {
            it.transformBy(transformation = transformation)
        },
    )

    override fun splitAt(
        coord: Coord,
    ): Pair<OpenCurve, OpenCurve> {
        TODO()
    }

    override fun findOffsetCurve(
        offset: Double,
    ): OpenCurve = connect(
        sequentialCurves.map {
            it.findOffsetCurve(offset = offset)
        },
    )

    override fun findBoundingBox(): BoundingBox = BoundingBox.unionAll(
        boundingBoxes = segmentCurves.map { it.findBoundingBox() },
    )

    override fun findIntersections(
        objectCurve: OpenCurve,
    ): Set<Intersection> {
        return objectCurve.findIntersectionsOpenSpline(
            subjectSpline = this,
        )
    }

    override fun findIntersectionsLineSegment(
        subjectLineSegment: LineSegment,
    ): Set<Intersection> = OpenSpline.findIntersections(
        subjectPrimitiveCurve = subjectLineSegment,
        objectOpenSpline = this,
        tolerance = NumericObject.Tolerance.Default,
    )

    override fun findIntersectionsBezierCurve(
        subjectBezierCurve: BezierCurve,
    ): Set<Intersection> = OpenSpline.findIntersections(
        subjectPrimitiveCurve = subjectBezierCurve,
        objectOpenSpline = this,
        tolerance = NumericObject.Tolerance.Default,
    )

    override fun findIntersectionsOpenSpline(
        subjectSpline: OpenSpline,
    ): Set<Intersection> = OpenSpline.findIntersections(
        subjectOpenSpline = subjectSpline,
        objectOpenSpline = this,
        tolerance = NumericObject.Tolerance.Default,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is OpenSpline -> false
        !trailingSequentialLinks.equalsWithTolerance(other.links, tolerance) -> false
        else -> true
    }

    override val links: List<Spline.Link>
        get() = trailingSequentialLinks

    override val start: Point
        get() = firstCurve.start

    override val end: Point
        get() = links.lastOrNull()?.end ?: firstCurve.end

    override val subCurves: List<PrimitiveCurve>
        get() = sequentialCurves

    override val pathFunction: FeatureFunction<Point> = piecewiseFeatureFunction(PrimitiveCurve::pathFunction)

    override val tangentDirectionFunction: FeatureFunction<Direction?> =
        piecewiseFeatureFunction(PrimitiveCurve::tangentDirectionFunction)

    private fun <A> piecewiseFeatureFunction(
        extract: (PrimitiveCurve) -> FeatureFunction<A>,
    ): FeatureFunction<A> = FeatureFunction.piecewise(
        pieces = subCurves.map(extract),
    )

    fun toLineSegment(): LineSegment? = sequentialCurves.singleOrNull() as? LineSegment

    fun toBezierCurve(): BezierCurve? = sequentialCurves.singleOrNull() as? BezierCurve

    override val segmentCurves: List<PrimitiveCurve>
        get() = sequentialCurves

    override fun toReprString(): String {
        return """
            |OpenSpline(
            |  firstCurve = ${firstCurve.toReprString().indentLater()},
            |  trailingSequentialLinks = ${trailingSequentialLinks.toReprString().indentLater()},
            |)
        """.trimMargin()
    }
}
