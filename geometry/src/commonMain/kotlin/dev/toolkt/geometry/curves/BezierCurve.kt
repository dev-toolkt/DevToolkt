package dev.toolkt.geometry.curves

import dev.toolkt.geometry.BoundingBox
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.SpatialObject
import dev.toolkt.geometry.splines.OpenSpline
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.geometry.math.RationalImplicitPolynomial
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction.Companion.primaryTRange
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.iterable.mapCarrying
import dev.toolkt.core.range.linearlyInterpolate
import dev.toolkt.core.range.normalize
import kotlin.math.roundToInt


data class BezierCurve private constructor(
    override val basisFunction: CubicBezierBinomial,
) : PrimitiveCurve() {
    data class Edge(
        val firstControl: Point,
        val secondControl: Point,
    ) : PrimitiveCurve.Edge() {
        val lastControl: Point
            get() = secondControl

        override fun bind(
            start: Point,
            end: Point,
        ): BezierCurve = BezierCurve(
            start = start,
            firstControl = firstControl,
            secondControl = secondControl,
            end = end,
        )

        override fun transformBy(
            transformation: Transformation,
        ): PrimitiveCurve.Edge = Edge(
            firstControl = firstControl.transformBy(transformation = transformation),
            secondControl = secondControl.transformBy(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Edge -> false
            !firstControl.equalsWithTolerance(other.firstControl, tolerance) -> false
            !secondControl.equalsWithTolerance(other.secondControl, tolerance) -> false
            else -> true
        }

        override fun toReprString(): String {
            return """
                |BezierCurve.Edge(
                |  firstControl = ${firstControl.toReprString()},
                |  secondControl = ${secondControl.toReprString()},
                |)
            """.trimMargin()
        }
    }

    companion object {
        /**
         * Finds intersections between two Bézier curves using the default
         * strategy
         */
        fun findIntersections(
            subjectBezierCurve: BezierCurve,
            objectBezierCurve: BezierCurve,
            tolerance: SpatialObject.SpatialTolerance,
        ): Set<Intersection> = when {
            subjectBezierCurve.basisFunction.isFullyOverlapping(
                other = objectBezierCurve.basisFunction,
            ) -> findIntersectionsBySubdivision(
                subjectBezierCurve = subjectBezierCurve,
                objectBezierCurve = objectBezierCurve,
                tolerance = tolerance,
            )

            else -> findIntersectionsByEquationSolving(
                subjectBezierCurve = subjectBezierCurve,
                objectBezierCurve = objectBezierCurve,
            )
        }

        /**
         * Finds intersections between two Bézier curves by solving their
         * intersection equation
         */
        fun findIntersectionsByEquationSolving(
            subjectBezierCurve: BezierCurve,
            objectBezierCurve: BezierCurve,
        ): Set<Intersection> = findIntersectionsByEquationSolving(
            simpleSubjectCurve = subjectBezierCurve,
            complexObjectCurve = objectBezierCurve,
            tolerance = NumericObject.Tolerance.Default,
        )

        /**
         * Finds intersections between two Bézier curves by subdividing them
         * recursively and checking for overlaps
         */
        fun findIntersectionsBySubdivision(
            subjectBezierCurve: BezierCurve,
            objectBezierCurve: BezierCurve,
            tolerance: SpatialObject.SpatialTolerance,
        ): Set<Intersection> {
            val clusteredIntersections = findIntersectionsBySubdivisionRecursively(
                subjectBezierCurve = subjectBezierCurve,
                subjectCoordRange = Coord.fullRange,
                objectBezierCurve = objectBezierCurve,
                objectCoordRange = Coord.fullRange,
                sizeThreshold = tolerance.spanTolerance.value / 2.0,
            )

            val consolidatedIntersections = Intersection.consolidate(
                intersections = clusteredIntersections,
                tolerance = tolerance,
            )

            return consolidatedIntersections
        }

        fun findIntersectionsBySubdivisionRecursively(
            subjectBezierCurve: BezierCurve,
            subjectCoordRange: ClosedRange<Coord>,
            objectBezierCurve: BezierCurve,
            objectCoordRange: ClosedRange<Coord>,
            sizeThreshold: Double,
        ): Set<Intersection> {
            val firstBoundingBox = subjectBezierCurve.findBoundingBox()
            val secondBoundingBox = objectBezierCurve.findBoundingBox()

            if (!firstBoundingBox.overlaps(secondBoundingBox)) {
                return emptySet()
            }

            val isFirstBoundingBoxSmallEnough = firstBoundingBox.smallerSide < sizeThreshold
            val isSecondBoundingBoxSmallEnough = secondBoundingBox.smallerSide < sizeThreshold

            if (isFirstBoundingBoxSmallEnough && isSecondBoundingBoxSmallEnough) {
                val intersectionPoint = Point.Companion.midPoint(
                    firstBoundingBox.center,
                    secondBoundingBox.center,
                )

                return setOf(
                    object : Intersection() {
                        override val point: Point = intersectionPoint

                        override val subjectCoord: Coord = subjectCoordRange.start

                        override val objectCoord: Coord = objectCoordRange.start
                    },
                )
            }

            val (firstCurveLeftCoordRange, firstCurveRightCoordRange) = subjectCoordRange.splitAtHalf()
            val (firstCurveLeft, firstCurveRight) = subjectBezierCurve.splitAt(coord = Coord.half)

            val (secondCurveLeftCoordRange, secondCurveRightCoordRange) = objectCoordRange.splitAtHalf()
            val (secondCurveLeft, secondCurveRight) = objectBezierCurve.splitAt(coord = Coord.half)

            return findIntersectionsBySubdivisionRecursively(
                subjectBezierCurve = firstCurveLeft,
                subjectCoordRange = firstCurveLeftCoordRange,
                objectBezierCurve = secondCurveLeft,
                objectCoordRange = secondCurveLeftCoordRange,
                sizeThreshold = sizeThreshold,
            ) + findIntersectionsBySubdivisionRecursively(
                subjectBezierCurve = firstCurveLeft,
                subjectCoordRange = firstCurveLeftCoordRange,
                objectBezierCurve = secondCurveRight,
                objectCoordRange = secondCurveRightCoordRange,
                sizeThreshold = sizeThreshold,
            ) + findIntersectionsBySubdivisionRecursively(
                subjectBezierCurve = firstCurveRight,
                subjectCoordRange = firstCurveRightCoordRange,
                objectBezierCurve = secondCurveLeft,
                objectCoordRange = secondCurveLeftCoordRange,
                sizeThreshold = sizeThreshold,
            ) + findIntersectionsBySubdivisionRecursively(
                subjectBezierCurve = firstCurveRight,
                subjectCoordRange = firstCurveRightCoordRange,
                objectBezierCurve = secondCurveRight,
                objectCoordRange = secondCurveRightCoordRange,
                sizeThreshold = sizeThreshold,
            )
        }

        /**
         * Finds intersections between a Bézier curve and a line segment
         */
        fun findIntersections(
            subjectLineSegment: LineSegment,
            objectBezierCurve: BezierCurve,
        ): Set<Intersection> = LineSegment.Companion.findIntersections(
            subjectLineSegment = subjectLineSegment,
            objectPrimitiveCurve = objectBezierCurve,
        )
    }

    constructor(
        start: Point,
        firstControl: Point,
        secondControl: Point,
        end: Point,
    ) : this(
        basisFunction = CubicBezierBinomial(
            point0 = start.pointVector,
            point1 = firstControl.pointVector,
            point2 = secondControl.pointVector,
            point3 = end.pointVector,
        ),
    )

    override val start: Point
        get() = Point(basisFunction.point0)

    val firstControl: Point
        get() = Point(basisFunction.point1)

    val secondControl: Point
        get() = Point(basisFunction.point2)

    override val end: Point
        get() = Point(basisFunction.point3)

    val lastControl: Point
        get() = secondControl

    override val edge: Edge
        get() = Edge(
            firstControl = firstControl,
            secondControl = secondControl,
        )

    override fun transformBy(
        transformation: Transformation,
    ): BezierCurve = BezierCurve(
        start = start.transformBy(transformation = transformation),
        firstControl = firstControl.transformBy(transformation = transformation),
        secondControl = secondControl.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    override fun splitAt(
        coord: Coord,
    ): Pair<BezierCurve, BezierCurve> {
        val (firstBasisFunction, secondBasisFunction) = basisFunction.splitAt(t = coord.t)

        return Pair(
            first = BezierCurve(
                basisFunction = firstBasisFunction,
            ),
            second = BezierCurve(
                basisFunction = secondBasisFunction,
            ),
        )
    }

    fun splitToSpline(
        sampleCount: Int,
    ): OpenSpline = OpenSpline.connect(
        sequentialCurves = LinSpace.generateSubRanges(
            range = primaryTRange,
            sampleCount = sampleCount,
        ).map { tRange ->
            trim(coordRange = tRange.toCoordRange()!!)
        }.toList()
    )

    override fun evaluate(
        coord: Coord,
    ): Point = Point(
        pointVector = basisFunction.apply(coord.t),
    )

    override fun locatePoint(point: Point): Coord? {
        val invertedCurve = basisFunction.inverted ?: run {
            // If the curve is degenerated to a line (or a point), it still
            // _can_ contain the given point, but for now we're giving up
            return null
        }

        val tValue = locatePointByInversion(
            invertedCurve = invertedCurve,
            point = point,
        ) ?: basisFunction.locatePointByProjection(
            point = point.pointVector,
            tolerance = NumericObject.Tolerance.Default,
        ) ?: return null

        return Coord.of(t = tValue)
    }

    /**
     * Locate the given point by inversion
     *
     * @return t-value of the [point] if it lies on the curve or `null` if the
     * point is not on the curve or t-value ćouldn't be found by inversion
     */
    private fun locatePointByInversion(
        invertedCurve: RationalImplicitPolynomial,
        point: Point,
    ): Double? {
        val tValue = invertedCurve.applyOrNull(point.pointVector) ?: return null

        val actualPoint = basisFunction.apply(tValue)

        return when {
            actualPoint.equalsWithTolerance(point) -> tValue

            // There are at least two reasons why the points differ: either the
            // given point really doesn't lye on the curve, or it _does_ but
            // is close to the self-intersection (and the 0/0 safeguard failed)
            else -> null
        }
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is BezierCurve -> false
        !start.equalsWithTolerance(other.start, tolerance = tolerance) -> false
        !firstControl.equalsWithTolerance(other.firstControl, tolerance = tolerance) -> false
        !secondControl.equalsWithTolerance(other.secondControl, tolerance = tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance = tolerance) -> false
        else -> true
    }

    override fun findOffsetCurve(
        offset: Double,
    ): BezierCurve = this.transformBy(
        transformation = PrimitiveTransformation.Translation(
            tx = offset,
            ty = offset,
        ),
    )

    override fun findBoundingBox(): BoundingBox {
        val startPoint = pathFunction.start
        val endPoint = pathFunction.end

        val criticalPointSet = basisFunction.findCriticalPoints()

        val criticalXValues = criticalPointSet.xRoots.mapNotNull { t ->
            Coord.of(t = t)?.let { evaluate(it).x }
        }

        val potentialXExtrema = criticalXValues + startPoint.x + endPoint.x
        val xMin = potentialXExtrema.min()
        val xMax = potentialXExtrema.max()

        val criticalYValues = criticalPointSet.yRoots.mapNotNull { t ->
            Coord.of(t = t)?.let { evaluate(it).y }
        }

        val potentialYExtrema = criticalYValues + startPoint.y + endPoint.y
        val yMin = potentialYExtrema.min()
        val yMax = potentialYExtrema.max()

        return BoundingBox.Companion.of(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    override fun findIntersections(objectCurve: OpenCurve): Set<Intersection> {
        return objectCurve.findIntersectionsBezierCurve(
            subjectBezierCurve = this,
        )
    }

    override fun findIntersectionsBezierCurve(
        subjectBezierCurve: BezierCurve,
    ): Set<Intersection> = findIntersections(
        subjectBezierCurve = subjectBezierCurve,
        objectBezierCurve = this,
        tolerance = SpatialObject.SpatialTolerance.default,
    )

    override fun toReprString(): String {
        return """
            |BezierCurve(
            |  start = ${start.toReprString()},
            |  firstControl = ${firstControl.toReprString()},
            |  secondControl = ${secondControl.toReprString()},
            |  end = ${end.toReprString()},
            |)
        """.trimMargin()
    }

    fun trimTo(endCoord: Coord): BezierCurve {
        val (trimmedBasisFunction, _) = basisFunction.splitAt(t = endCoord.t)

        return BezierCurve(
            basisFunction = trimmedBasisFunction,
        )
    }

    fun trimFrom(coord: Coord): BezierCurve {
        val (_, trimmedBasisFunction) = basisFunction.splitAt(t = coord.t)

        return BezierCurve(
            basisFunction = trimmedBasisFunction,
        )
    }

    private fun trimRange(
        coordRange: ClosedRange<Coord>,
    ): BezierCurve {
        val tStart = coordRange.start.t
        val (_, leftTrimmedBasisFunction) = basisFunction.splitAt(t = tStart)

        val tEnd = (tStart..1.0).normalize(coordRange.endInclusive.t)
        val (trimmedCurve, _) = leftTrimmedBasisFunction.splitAt(t = tEnd)

        return BezierCurve(
            basisFunction = trimmedCurve,
        )
    }

    fun trim(
        coordRange: ClosedRange<Coord>,
    ): BezierCurve {
        val startCoord = coordRange.start
        val endCoord = coordRange.endInclusive

        return when {
            startCoord == Coord.start && endCoord == Coord.end -> this
            startCoord == Coord.start -> trimTo(endCoord = endCoord)
            endCoord == Coord.end -> trimFrom(coord = startCoord)
            else -> trimRange(coordRange = coordRange)
        }
    }

    fun containsPoint(point: Point): Boolean {
        val tValue = basisFunction.projectPointClosest(point.pointVector) ?: return false
        val coord = Coord.of(t = tValue) ?: return false

        val distance = Point.distanceBetween(evaluate(coord), point)

        return distance.equalsApproximatelyZero()
    }

    val totalArcLength: Double by lazy {
        calculateArcLength(
            coordRange = Coord.fullRange,
        )
    }

    private data class ArcSegment(
        val startArcLength: Double,
        private val approximationSegment: QuadraticApproximationSegment,
    ) {
        val endArcLength: Double = startArcLength + approximationCurve.primaryArcLength

        private val approximationCurve: QuadraticBezierBinomial
            get() = approximationSegment.approximationCurve

        private val coordRange: ClosedRange<Coord>
            get() = approximationSegment.coordRange

        private val arcLengthRange: ClosedFloatingPointRange<Double>
            get() = startArcLength..endArcLength

        fun locateArcLength(
            arcLength: Double,
            tolerance: NumericObject.Tolerance.Absolute,
        ): Coord? {
            if (arcLength !in arcLengthRange) {
                return null
            }

            // The t-value within the approximation curve
            val localTValue = approximationCurve.locateArcLength(
                arcLength = arcLength - startArcLength,
                tolerance = tolerance,
            ) ?: return null

            // The t-value within the original cubic curve
            val globalTValue = coordRange.tRange.linearlyInterpolate(localTValue)

            return Coord.of(t = globalTValue)
        }
    }

    fun locateArcLength(
        arcLength: Double,
        tolerance: NumericObject.Tolerance.Absolute,
    ): Coord? = lowerInRange(
        coordRange = Coord.fullRange,
    ).mapCarrying(
        initialCarry = 0.0,
    ) { accArcLength, quadraticBezierBinomial ->
        val arcSegment = ArcSegment(
            startArcLength = accArcLength,
            approximationSegment = quadraticBezierBinomial,
        )

        Pair(
            arcSegment,
            arcSegment.endArcLength,
        )
    }.firstNotNullOfOrNull {
        it.locateArcLength(arcLength, tolerance)
    }

    fun calculateArcLengthUpTo(
        endCoord: Coord,
    ): Double = calculateArcLength(
        coordRange = Coord.start..endCoord,
    )

    fun calculateArcLength(
        coordRange: ClosedRange<Coord>,
    ): Double = lowerInRange(
        coordRange = coordRange,
    ).sumOf {
        it.approximationCurve.primaryArcLength
    }

    data class QuadraticApproximationSegment(
        val coordRange: ClosedRange<Coord>,
        val approximationCurve: QuadraticBezierBinomial,
    )

    fun lowerInRange(
        coordRange: ClosedRange<Coord>,
    ): Sequence<QuadraticApproximationSegment> {
        val maxSampleCount = 9

        val sampleCount = (coordRange.coverage * maxSampleCount).roundToInt().coerceAtLeast(
            minimumValue = 2,
        )

        return Coord.generateSubRanges(
            coordRange = coordRange,
            sampleCount = sampleCount,
        ).map { coordRange ->
            val trimmedCurve = trim(coordRange = coordRange)

            QuadraticApproximationSegment(
                coordRange = coordRange,
                approximationCurve = trimmedCurve.basisFunction.lower(),
            )
        }
    }
}
