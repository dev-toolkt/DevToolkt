package dev.toolkt.geometry.splines

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test

class ClosedSplineTests {
    @Test
    fun testSmoothSubSplines() {
        val point0FreeJoint = Point(849.97, 1083.35)
        val point1Control = Point(923.62, 1116.05)
        val point2Control = Point(1015.23, 1213.44)
        val point3SmoothJoint = Point(1061.95, 1336.57)
        val point4Control = Point(1111.89, 1468.18)
        val point5Control = Point(1143.21, 1618.69)

        val point6FreeJoint = Point(1149.75, 1695.65)
        val point7Control = Point(1228.37, 1673.57)
        val point8Control = Point(1302.86, 1563.13)
        val point9SmoothJoint = Point(1506.80, 1553.93)
        val point10Control = Point(1750.43, 1542.92)
        val point11Control = Point(1680.53, 1442.38)
        val point12SmoothJoint = Point(1877.12, 1435.89)
        val point13Control = Point(2069.22, 1429.54)
        val point14Control = Point(2160.31, 1476.10)

        val point15FreeJoint = Point(2305.43, 1341.55)
        val point16Control = Point(2267.93, 1108.87)
        val point17Control = Point(2203.85, 790.42)

        val point18FreeJoint = Point(1838.79, 561.24)
        val point19Control = Point(1478.44, 611.55)
        val point20Control = Point(1447.10, 715.54)
        val point21SmoothJoint = Point(1283.83, 758.94)
        val point22Control = Point(1084.05, 812.07)
        val point23Control = Point(997.57, 842.14)

        val closedSpline = ClosedSpline(
            cyclicLinks = listOf(
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point1Control,
                        secondControl = point2Control,
                    ),
                    end = point3SmoothJoint,
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point4Control,
                        secondControl = point5Control,
                    ),
                    end = point6FreeJoint,
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point7Control,
                        secondControl = point8Control,
                    ),
                    end = point9SmoothJoint,
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point10Control,
                        secondControl = point11Control,
                    ),
                    end = point12SmoothJoint,
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point13Control,
                        secondControl = point14Control,
                    ),
                    end = point15FreeJoint,
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point16Control,
                        secondControl = point17Control,
                    ),
                    end = point18FreeJoint,
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point19Control,
                        secondControl = point20Control,
                    ),
                    end = point21SmoothJoint,
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point22Control,
                        secondControl = point23Control,
                    ),
                    end = point0FreeJoint,
                ),
            ),
        )

        assertEqualsWithTolerance(
            expected = listOf(
                OpenSpline(
                    firstCurve = BezierCurve(
                        start = point0FreeJoint,
                        firstControl = point1Control,
                        secondControl = point2Control,
                        end = point3SmoothJoint,
                    ),
                    trailingSequentialLinks = listOf(
                        Spline.Link(
                            edge = BezierCurve.Edge(
                                firstControl = point4Control,
                                secondControl = point5Control,
                            ),
                            end = point6FreeJoint,
                        ),
                    ),
                ),
                OpenSpline(
                    firstCurve = BezierCurve(
                        start = point6FreeJoint,
                        firstControl = point7Control,
                        secondControl = point8Control,
                        end = point9SmoothJoint,
                    ),
                    trailingSequentialLinks = listOf(
                        Spline.Link(
                            edge = BezierCurve.Edge(
                                firstControl = point10Control,
                                secondControl = point11Control,
                            ),
                            end = point12SmoothJoint,
                        ),
                        Spline.Link(
                            edge = BezierCurve.Edge(
                                firstControl = point13Control,
                                secondControl = point14Control,
                            ),
                            end = point15FreeJoint,
                        ),
                    ),
                ),
                OpenSpline(
                    firstCurve = BezierCurve(
                        start = point15FreeJoint,
                        firstControl = point16Control,
                        secondControl = point17Control,
                        end = point18FreeJoint,
                    ),
                    trailingSequentialLinks = emptyList(),
                ),
                OpenSpline(
                    firstCurve = BezierCurve(
                        start = point18FreeJoint,
                        firstControl = point19Control,
                        secondControl = point20Control,
                        end = point21SmoothJoint,
                    ),
                    trailingSequentialLinks = listOf(
                        Spline.Link(
                            edge = BezierCurve.Edge(
                                firstControl = point22Control,
                                secondControl = point23Control,
                            ),
                            end = point0FreeJoint,
                        ),
                    ),
                ),
            ),
            actual = closedSpline.smoothSubSplines,
        )
    }
}
