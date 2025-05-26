package dev.toolkt.geometry

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericObject.Tolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Translation
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.iterable.clusterSimilar

data class Point(
    val pointVector: Vector2,
) : SpatialObject, NumericObject {
    companion object {
        fun areCollinear(
            firstPoint: Point,
            secondPoint: Point,
            testPoint: Point,
            tolerance: Tolerance,
        ): Boolean {
            TODO()
        }

        fun average(
            points: Iterable<Point>,
        ): Point {
            val ax = points.map { it.x }.average()
            val ay = points.map { it.y }.average()

            return Point(
                x = ax,
                y = ay,
            )
        }

        fun consolidate(
            points: Set<Point>,
            tolerance: SpatialObject.SpatialTolerance,
        ): Set<Point> {
            // Sorting points by the distance from origin helps, but it
            // will fail for multiple clouds that have the same distance from
            // origin with the given tolerance
            val pointsSorted = points.sortedBy { it.pointVector.magnitudeSquared }

            val pointClusters: List<List<Point>> = pointsSorted.clusterSimilar { cloud, point ->
                val cloudAverage = average(cloud)

                distanceBetween(cloudAverage, point) < tolerance.spanTolerance
            }

            return pointClusters.map { pointCluster ->
                Point.average(pointCluster)
            }.toSet()
        }

        fun midPoint(
            a: Point,
            b: Point,
        ): Point = Point(
            pointVector = a.pointVector + (b.pointVector - a.pointVector) / 2.0,
        )

        val origin: Point = Point(
            x = 0.0,
            y = 0.0,
        )

        fun distanceBetween(
            one: Point,
            another: Point,
        ): Span = Span.Squared(
            valueSquared = (one.pointVector - another.pointVector).magnitudeSquared,
        )
    }

    constructor(
        x: Double,
        y: Double,
    ) : this(
        pointVector = Vector2(
            x = x,
            y = y,
        ),
    )

    val x: Double
        get() = pointVector.x

    val y: Double
        get() = pointVector.y

    fun transformBy(
        transformation: Transformation,
    ): Point = transformation.transform(point = this)

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Point -> false
        !pointVector.equalsWithTolerance(other.pointVector, tolerance = tolerance) -> false
        else -> true
    }

    override fun equalsWithSpatialTolerance(
        other: SpatialObject,
        tolerance: SpatialObject.SpatialTolerance,
    ): Boolean = when {
        other !is Point -> false

        else -> distanceBetween(this, other).equalsWithSpatialTolerance(
            Span.Zero,
            tolerance = tolerance,
        )
    }

    fun translateByDistance(
        direction: Direction,
        distance: Span,
    ): Point = transformBy(
        Translation.inDirection(
            direction = direction,
            distance = distance,
        ),
    )

    fun translationTo(
        target: Point,
    ): Translation = Translation.between(
        origin = this,
        target = target,
    )

    fun reflectedBy(
        mirror: Point,
    ): Point = mirror.transformBy(
        transformation = translationTo(mirror),
    )

    fun directionTo(
        target: Point,
    ): Direction? = translationTo(target = target).direction

    fun castRay(
        direction: Direction,
    ): Ray = Ray.inDirection(
        point = this,
        direction = direction,
    )

    fun castRayTo(
        target: Point,
    ): Ray? = directionTo(target = target)?.let {
        castRay(direction = it)
    }

    override fun toString(): String = toReprString()

    fun toReprString(): String = "Point($x, $y)"
}
