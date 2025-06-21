package dev.toolkt.geometry.curves

import dev.toolkt.core.ReprObject
import dev.toolkt.geometry.BoundingBox
import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point.Companion.distanceBetween
import dev.toolkt.geometry.Ray
import dev.toolkt.geometry.SpatialObject
import dev.toolkt.geometry.splines.OpenSpline
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.RealFunction
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.math.avgOf
import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.iterable.clusterSimilar
import dev.toolkt.core.math.split
import dev.toolkt.core.range.width
import kotlin.jvm.JvmInline

/**
 * A curve defined in range t [0, 1] that is open (having a specified start and
 * end point), assumed to be tangent-continuous (G1).
 */
abstract class OpenCurve : NumericObject, ReprObject {
    abstract class FeatureFunction<out A> {
        companion object {
            fun <A> constant(
                value: A,
            ): FeatureFunction<A> = object : FeatureFunction<A>() {
                override fun evaluate(
                    coord: Coord,
                ): A = value
            }

            fun <A> piecewise(
                pieces: List<FeatureFunction<A>>,
            ): FeatureFunction<A> {
                require(pieces.isNotEmpty())

                return object : FeatureFunction<A>() {
                    override fun evaluate(
                        coord: Coord,
                    ): A = when (coord.t) {
                        1.0 -> pieces.last().end

                        else -> {
                            val ts = coord.t * pieces.size
                            val (index, t) = ts.split()

                            val piece = pieces[index]

                            piece.evaluate(
                                coord = Coord(t = t)
                            )
                        }
                    }
                }
            }

            fun <A, B, C> map2(
                functionA: FeatureFunction<A>,
                functionB: FeatureFunction<B>,
                transform: (A, B) -> C,
            ): FeatureFunction<C> = object : FeatureFunction<C>() {
                override fun evaluate(
                    coord: Coord,
                ): C = transform(
                    functionA.evaluate(coord),
                    functionB.evaluate(coord),
                )
            }

            fun <A> wrap(
                realFunction: RealFunction<A>,
            ): FeatureFunction<A> = object : FeatureFunction<A>() {
                override fun evaluate(
                    coord: Coord,
                ): A = realFunction.apply(coord.t)
            }
        }

        fun <B> map(
            transform: (A) -> B,
        ): FeatureFunction<B> = object : FeatureFunction<B>() {
            override fun evaluate(
                coord: Coord,
            ): B = transform(
                this@FeatureFunction.evaluate(coord),
            )
        }

        val start: A
            get() = evaluate(Coord.start)

        val end: A
            get() = evaluate(Coord.end)

        abstract fun evaluate(
            coord: Coord,
        ): A
    }

    @JvmInline
    value class Coord(
        /**
         * The t-value for the basis function
         */
        val t: Double,
    ) : NumericObject, ReprObject, Comparable<Coord> {
        companion object {
            private val tRange = 0.0..1.0

            val start = Coord(t = 0.0)

            val half = Coord(t = 0.5)

            val end = Coord(t = 1.0)

            val fullRange = start..end

            fun average(
                coords: Iterable<Coord>,
            ): Coord {
                val tAverage = coords.map { it.t }.average()

                return Coord(
                    t = tAverage,
                )
            }

            fun generateSubRanges(
                coordRange: ClosedRange<OpenCurve.Coord> = fullRange,
                sampleCount: Int,
            ): Sequence<ClosedRange<Coord>> = LinSpace.generateSubRanges(
                range = coordRange.tRange,
                sampleCount = sampleCount,
            ).map {
                it.toCoordRange()!!
            }

            /**
             * @param t t-value, unconstrained
             * @return coord if t is in [0, 1], null otherwise
             */
            fun of(t: Double): Coord? = when (t) {
                in tRange -> Coord(t = t)
                else -> null
            }

            fun ofSaturated(t: Double): Coord = when {
                t < 0.0 -> start
                t > 1.0 -> end
                else -> Coord(t = t)
            }
        }

        val complement: Coord
            get() = Coord(
                t = 1.0 - t,
            )

        init {
            require(t in tRange)
        }

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Coord -> false
            !t.equalsWithTolerance(other.t, tolerance) -> false
            else -> true
        }

        override fun toReprString(): String = "Coord(t = $t)"

        override fun compareTo(other: Coord): Int = t.compareTo(other.t)
    }

    /**
     * The intersection of two curves
     */
    abstract class Intersection : NumericObject {
        companion object {
            fun swap(
                intersections: Set<Intersection>,
            ): Set<Intersection> = intersections.map {
                it.swap()
            }.toSet()

            fun average(
                intersections: Iterable<Intersection>,
            ): Intersection = object : Intersection() {
                override val point: Point = Point.average(
                    points = intersections.map { it.point },
                )

                override val subjectCoord: Coord = Coord.average(
                    coords = intersections.map { it.subjectCoord },
                )

                override val objectCoord: Coord = Coord.average(
                    coords = intersections.map { it.objectCoord },
                )
            }

            fun consolidate(
                intersections: Set<Intersection>,
                tolerance: SpatialObject.SpatialTolerance,
            ): Set<Intersection> {
                val intersectionsSorted = intersections.sortedBy { it.subjectCoord }

                val intersectionClusters = intersectionsSorted.clusterSimilar { intersectionCloud, intersection ->
                    val intersectionCloudAveragePoint = Point.average(
                        points = intersectionCloud.map { it.point }
                    )

                    distanceBetween(intersectionCloudAveragePoint, intersection.point) < tolerance.spanTolerance
                }

                return intersectionClusters.map { intersectionCluster ->
                    Intersection.average(intersections = intersectionCluster)
                }.toSet()
            }
        }

        fun swap(): Intersection {
            return object : Intersection() {
                override val point: Point
                    get() = this@Intersection.point

                override val subjectCoord: Coord
                    get() = this@Intersection.objectCoord

                override val objectCoord: Coord
                    get() = this@Intersection.subjectCoord
            }
        }

        final override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Intersection -> false
            !point.equalsWithTolerance(other.point, tolerance) -> false
            !subjectCoord.equalsWithTolerance(other.subjectCoord, tolerance) -> false
            !objectCoord.equalsWithTolerance(other.objectCoord, tolerance) -> false
            else -> true
        }

        override fun toString(): String {
            return """
                |Intersection(
                |  point = ${point.toReprString()},
                |  subjectCoord = ${subjectCoord.toReprString()},
                |  objectCoord = ${objectCoord.toReprString()},
                |)
            """.trimMargin()
        }

        /**
         * The point of intersection
         */
        abstract val point: Point

        /**
         * The coordinate of the intersection point on the subject curve
         */
        abstract val subjectCoord: Coord

        /**
         * The coordinate of the intersection point on the object curve
         */
        abstract val objectCoord: Coord
    }

    companion object {
        /**
         * Finds intersections between two open curves
         */
        fun findIntersections(
            subjectOpenCurve: OpenCurve,
            objectOpenCurve: OpenCurve,
        ): Set<Intersection> = subjectOpenCurve.findIntersections(
            objectCurve = objectOpenCurve,
        )
    }

    val startTangent: Direction?
        get() = tangentDirectionFunction.start

    val endTangent: Direction?
        get() = tangentDirectionFunction.end

    val tangentRay: FeatureFunction<Ray?> by lazy {
        FeatureFunction.map2(
            functionA = pathFunction,
            functionB = tangentDirectionFunction,
        ) { point, direction ->
            direction?.let { point.castRay(it) }
        }
    }

    abstract val start: Point

    abstract val end: Point

    abstract val subCurves: List<PrimitiveCurve>

    abstract val pathFunction: FeatureFunction<Point>

    abstract val tangentDirectionFunction: FeatureFunction<Direction?>

    abstract fun transformBy(
        transformation: Transformation,
    ): OpenCurve

    abstract fun splitAt(
        coord: Coord,
    ): Pair<OpenCurve, OpenCurve>

    /**
     * Find the offset curve (or its close approximation) of this curve
     */
    abstract fun findOffsetCurve(
        offset: Double,
    ): OpenCurve

    abstract fun findBoundingBox(): BoundingBox

    /**
     * Find the intersections of this curve (also referred to as the "subject
     * curve") with the [objectCurve] curve.
     */
    protected abstract fun findIntersections(
        objectCurve: OpenCurve,
    ): Set<Intersection>

    internal abstract fun findIntersectionsLineSegment(
        subjectLineSegment: LineSegment,
    ): Set<Intersection>

    internal abstract fun findIntersectionsBezierCurve(
        subjectBezierCurve: BezierCurve,
    ): Set<Intersection>

    internal abstract fun findIntersectionsOpenSpline(
        subjectSpline: OpenSpline,
    ): Set<Intersection>
}

fun ClosedRange<OpenCurve.Coord>.splitAtHalf(): Pair<ClosedRange<OpenCurve.Coord>, ClosedRange<OpenCurve.Coord>> {
    val tHalf = avgOf(start.t, endInclusive.t)
    val coordHalf = OpenCurve.Coord(t = tHalf)

    return Pair(
        start..coordHalf,
        coordHalf..endInclusive,
    )
}

val ClosedRange<OpenCurve.Coord>.tRange: ClosedFloatingPointRange<Double>
    get() = start.t..endInclusive.t

/**
 * The fraction of the full coordinate range that this range covers.
 */
val ClosedRange<OpenCurve.Coord>.coverage: Double
    get() = tRange.width

fun ClosedFloatingPointRange<Double>.toCoordRange(): ClosedRange<OpenCurve.Coord>? {
    val startCoord = OpenCurve.Coord.of(this.start) ?: return null
    val endCoord = OpenCurve.Coord.of(this.endInclusive) ?: return null

    return startCoord..endCoord
}
