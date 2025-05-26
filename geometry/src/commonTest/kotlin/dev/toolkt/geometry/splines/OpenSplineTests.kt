package dev.toolkt.geometry.splines

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.ExpectedIntersection
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.curves.testIntersectionsSymmetric
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenSplineTests {
    @Test
    fun testPath() {
        val start = Point(272.7262878417969, 159.526123046875)

        val edge0 = BezierCurve.Edge(
            firstControl = Point(339.06092071533203, 513.923095703125),
            secondControl = Point(376.43798065185547, 373.21461486816406),
        )

        val joint0 = Point(425.1772003173828, 304.31105041503906)

        val edge1 = BezierCurve.Edge(
            firstControl = Point(456.03489112854004, 260.68693923950195),
            secondControl = Point(515.4947204589844, 215.17504119873047),
        )

        val joint1 = Point(563.93896484375, 241.8033905029297)

        val edge2 = BezierCurve.Edge(
            firstControl = Point(634.254035949707, 280.4534797668457),
            secondControl = Point(611.4755859375, 340.7131118774414),
        )

        val joint2 = Point(663.8353042602539, 387.37701416015625)

        val edge3 = BezierCurve.Edge(
            firstControl = Point(753.6513977050781, 467.42271423339844),
            secondControl = Point(864.3053665161133, 61.7249755859375),
        )

        val end = Point(832.1429672241211, 483.6293258666992)

        val openSpline = OpenSpline(
            firstCurve = edge0.bind(
                start,
                joint0,
            ),
            trailingSequentialLinks = listOf(
                Spline.Link(
                    edge = edge1,
                    end = joint1,
                ),
                Spline.Link(
                    edge = edge2,
                    end = joint2,
                ),
                Spline.Link(
                    edge = edge3,
                    end = end,
                ),
            ),
        )

        assertEquals(
            expected = start,
            actual = openSpline.pathFunction.start,
        )

        assertEqualsWithTolerance(
            expected = Point(309.3747166748048, 317.28693518066416),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.2 / 4.0),
            ),
        )

        assertEquals(
            expected = joint0,
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.25),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(459.6020209140777, 266.53840482711786),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 1.3 / 4.0),
            ),
        )

        assertEquals(
            expected = joint1,
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.5),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(614.3989881591797, 296.3029407348632),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 2.4 / 4.0),
            ),
        )

        assertEquals(
            expected = joint2,
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.75),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(793.7310705184937, 307.3061761856079),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 3.5 / 4.0),
            ),
        )

        assertEquals(
            expected = end,
            actual = openSpline.pathFunction.end,
        )
    }

    @Test
    @Ignore // TODO: Implement spline intersections
    fun testFindIntersections() {
        val firstSpline = OpenSpline(
            firstCurve = BezierCurve(
                start = Point(233.92449010844575, 500.813035986871),
                firstControl = Point(343.26973984755205, 339.3318789926634),
                secondControl = Point(364.86141567931736, 441.71541718852677),
                end = Point(474.9262537955492, 476.6114032280002),
            ),
            trailingSequentialLinks = listOf(
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = Point(519.5884855585136, 490.77100026252083),
                        secondControl = Point(494.16220097805854, 391.80245562403434),
                    ),
                    end = Point(575.6728757231976, 378.29095118203804),
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = Point(649.6017351174123, 366.03533410416276),
                        secondControl = Point(667.8388768639852, 466.4942731106785),
                    ),
                    end = Point(851.7185424238287, 456.1511196513238),
                ),
            ),
        )

        val secondSpline = OpenSpline(
            firstCurve = BezierCurve(
                start = Point(227.50050939555513, 441.366014502496),
                firstControl = Point(336.8457591346614, 279.8848575082884),
                secondControl = Point(302.16848767048214, 480.16296585374766),
                end = Point(412.233325786714, 515.0589518932211),
            ),
            trailingSequentialLinks = listOf(
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = Point(456.8978870316896, 529.2204687698832),
                        secondControl = Point(518.3551673235797, 228.38708781373862),
                    ),
                    end = Point(569.248895010307, 318.84392969766304),
                ),
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = Point(616.1208679000192, 402.1519062370553),
                        secondControl = Point(619.158747941794, 540.5259167604308),
                    ),
                    end = Point(845.2945617109381, 396.7040981669488),
                ),
            ),
        )

        testIntersectionsSymmetric(
            firstCurve = firstSpline,
            secondCurve = secondSpline,
            findIntersections = { firstSpline, secondSpline ->
                OpenSpline.findIntersections(
                    subjectOpenSpline = firstSpline,
                    objectOpenSpline = secondSpline,
                )
            },
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(324.0, 413.0),
                    firstCoord = OpenCurve.Coord.start,
                    secondCoord = OpenCurve.Coord.start,
                ),
                ExpectedIntersection(
                    point = Point(453.0, 468.0),
                    firstCoord = OpenCurve.Coord.start,
                    secondCoord = OpenCurve.Coord.start,
                ),
                ExpectedIntersection(
                    point = Point(596.0, 377.0),
                    firstCoord = OpenCurve.Coord.start,
                    secondCoord = OpenCurve.Coord.start,
                ),
            ),
        )
    }
}
