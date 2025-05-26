package dev.toolkt.geometry.curves

import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import kotlin.test.Test

class LineSegmentTests {
    @Test
    fun testFindIntersections_LineSegment_LineSegment_noIntersections_parallel() {
        val firstLineSegment = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        val secondLineSegment = LineSegment(
            start = Point(356.45270601450466, 503.7880075864232),
            end = Point(488.8733739397776, 364.2630461395838),
        )

        testIntersectionsSymmetric(
            firstCurve = firstLineSegment,
            secondCurve = secondLineSegment,
            findIntersections = { firstLineSegment, secondLineSegment ->
                LineSegment.findIntersections(
                    subjectLineSegment = firstLineSegment,
                    objectLineSegment = secondLineSegment,
                )
            },
            expectedIntersections = emptyList(),
        )
    }

    @Test
    fun testFindIntersections_LineSegment_LineSegment_noIntersections_oneMissing() {
        val firstLineSegment = LineSegment(
            start = Point(476.7364224829216, 355.56234711426623),
            end = Point(610.9940051091198, 523.1189590812173),
        )

        val secondLineSegment = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(463.06982851106113, 271.56572992954716),
        )

        testIntersectionsSymmetric(
            firstCurve = firstLineSegment,
            secondCurve = secondLineSegment,
            findIntersections = { firstLineSegment, secondLineSegment ->
                LineSegment.findIntersections(
                    subjectLineSegment = firstLineSegment,
                    objectLineSegment = secondLineSegment,
                )
            },
            expectedIntersections = emptyList(),
        )
    }

    @Test
    fun testFindIntersections_LineSegment_LineSegment_noIntersections_bothMissing() {
        val firstLineSegment = LineSegment(
            start = Point(575.9748502568091, 359.84856350754853),
            end = Point(610.9940051091198, 523.1189590812173),
        )

        val secondLineSegment = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        testIntersectionsSymmetric(
            firstCurve = firstLineSegment,
            secondCurve = secondLineSegment,
            findIntersections = { firstLineSegment, secondLineSegment ->
                LineSegment.findIntersections(
                    subjectLineSegment = firstLineSegment,
                    objectLineSegment = secondLineSegment,
                )
            },
            expectedIntersections = emptyList(),
        )
    }

    @Test
    fun testFindIntersections_LineSegment_LineSegment_oneIntersection() {
        val firstLineSegment = LineSegment(
            start = Point(324.4306395542335, 242.63695647226996),
            end = Point(610.9940051091198, 523.1189590812173),
        )

        val secondLineSegment = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(463.06982851106113, 271.56572992954716),
        )

        testIntersectionsSymmetric(
            firstCurve = firstLineSegment,
            secondCurve = secondLineSegment,
            findIntersections = { firstLineSegment, secondLineSegment ->
                LineSegment.findIntersections(
                    subjectLineSegment = firstLineSegment,
                    objectLineSegment = secondLineSegment,
                )
            },
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(419.650273, 335.835867),
                    firstCoord = OpenCurve.Coord(t = 0.332281),
                    secondCoord = OpenCurve.Coord(t = 0.682180),
                ),
            ),
        )
    }
}
