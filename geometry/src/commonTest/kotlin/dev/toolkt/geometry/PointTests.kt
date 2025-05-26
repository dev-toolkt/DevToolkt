package dev.toolkt.geometry

import kotlin.test.Test

data class PointCloud(
    val p0: Point,
    val fuzz: List<Pair<Double, Double>>,
) {
    private fun buildPoint(
        rf: Double,
        df: Double,
        spatialTolerance: SpatialObject.SpatialTolerance,
    ): Point {
        val angle = RelativeAngle.fractional(f = rf)
        val direction = Direction.fromAngle(angle = angle)
        val distance = spatialTolerance.spanTolerance * df * 0.5

        return p0.translateByDistance(
            direction = direction,
            distance = distance,
        )
    }

    fun build(
        spatialTolerance: SpatialObject.SpatialTolerance,
    ): Set<Point> = fuzz.map { (rf, df) ->
        buildPoint(
            rf = rf,
            df = df,
            spatialTolerance = spatialTolerance,
        )
    }.toSet()
}

class PointTests {
    @Test
    fun testConsolidate() {
        val spatialTolerance = SpatialObject.SpatialTolerance(
            spanTolerance = Span.of(value = 1e-2)
        )

        val geometricTolerance = GeometricObject.GeometricTolerance(
            spatialTolerance = spatialTolerance,
            radialTolerance = RelativeAngle.RadialTolerance.zero,
        )

        val pointClouds = listOf(
            PointCloud(
                p0 = Point(-15.8803, 4.23614),
                fuzz = listOf(
                    Pair(0.86, 0.46),
                    Pair(0.22, 0.99),
                    Pair(0.45, 0.6),
                )
            ),
            PointCloud(
                p0 = Point(-4.02671, 16.4626),
                fuzz = listOf(
                    Pair(0.23, 0.66),
                    Pair(0.44, 0.14),
                    Pair(0.53, 0.6),
                    Pair(0.6, 0.62),
                ),
            ),
            PointCloud(
                p0 = Point(19.3711, -2.90319),
                fuzz = listOf(
                    Pair(0.7, 0.53),
                    Pair(0.77, 0.5),
                    Pair(0.68, 0.09),
                    Pair(0.23, 0.86),
                    Pair(0.64, 0.27),
                ),
            ),
            PointCloud(
                p0 = Point(-12.8332, 10.0046),
                fuzz = listOf(
                    Pair(0.78, 0.53),
                    Pair(0.86, 0.8),
                ),
            ),
            PointCloud(
                p0 = Point(12.9167, 7.64897),
                fuzz = listOf(
                    Pair(0.2, 0.97),
                    Pair(0.02, 0.55),
                    Pair(0.9, 0.16),
                ),
            ),
            PointCloud(
                p0 = Point(-7.11203, 17.0744),
                fuzz = listOf(
                    Pair(0.74, 0.45),
                    Pair(0.98, 0.34),
                    Pair(0.42, 0.91),
                    Pair(0.15, 0.81),
                ),
            ),
            PointCloud(
                p0 = Point(-16.5921, 19.6118),
                fuzz = listOf(
                    Pair(0.23, 0.58),
                    Pair(0.84, 0.01),
                    Pair(0.96, 0.7),
                ),
            ),
            PointCloud(
                p0 = Point(-4.47583, -14.5868),
                fuzz = listOf(
                    Pair(0.4, 0.95),
                    Pair(0.99, 0.3),
                    Pair(0.98, 0.2),
                    Pair(0.9, 0.5),
                ),
            ),
        )

        val basePoints = pointClouds.map { it.p0 }.toSet()

        val clutteredPoints = pointClouds.flatMap { pointCloud ->
            pointCloud.build(spatialTolerance = spatialTolerance)
        }.toSet()

        val consolidatedPoints = Point.consolidate(
            points = clutteredPoints,
            tolerance = spatialTolerance,
        )

        assertEqualsWithGeometricTolerance(
            expected = basePoints.sortedBy { it.x },
            actual = consolidatedPoints.sortedBy { it.x },
            tolerance = geometricTolerance,
        )
    }
}
