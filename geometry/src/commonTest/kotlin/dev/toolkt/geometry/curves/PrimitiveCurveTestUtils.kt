package dev.toolkt.geometry.curves

import dev.toolkt.geometry.Point
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance

internal data class ExpectedIntersection(
    val point: Point,
    val firstCoord: OpenCurve.Coord,
    val secondCoord: OpenCurve.Coord,
) {
    fun swap(): ExpectedIntersection = ExpectedIntersection(
        point = point,
        firstCoord = secondCoord,
        secondCoord = firstCoord,
    )
}

internal fun <CurveT : OpenCurve> testIntersectionsSymmetric(
    firstCurve: CurveT,
    secondCurve: CurveT,
    findIntersections: (CurveT, CurveT) -> Set<OpenCurve.Intersection>,
    expectedIntersections: List<ExpectedIntersection>,
    tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
) {
    val intersectionsOneWay = findIntersections(
        firstCurve,
        secondCurve,
    )

    assertIntersectionsEqual(
        expectedIntersections = expectedIntersections,
        actualIntersections = intersectionsOneWay,
        tolerance = tolerance,
    )

    val intersectionsOtherWay = findIntersections(
        secondCurve,
        firstCurve,
    )

    assertIntersectionsEqual(
        expectedIntersections = expectedIntersections.map {
            it.swap()
        },
        actualIntersections = intersectionsOtherWay,
        tolerance = tolerance,
    )
}

internal fun assertIntersectionsEqual(
    expectedIntersections: List<ExpectedIntersection>,
    actualIntersections: Set<OpenCurve.Intersection>,
    tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
) {
    assertEqualsWithTolerance(
        expected = expectedIntersections.map { intersection ->
            object : OpenCurve.Intersection() {
                override val point: Point = intersection.point
                override val subjectCoord: OpenCurve.Coord = intersection.firstCoord
                override val objectCoord: OpenCurve.Coord = intersection.secondCoord
            }
        },
        actual = actualIntersections.sortedBy { it.point.x },
        tolerance = tolerance,
    )
}
