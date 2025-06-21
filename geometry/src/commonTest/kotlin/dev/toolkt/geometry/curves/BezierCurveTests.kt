package dev.toolkt.geometry.curves

import dev.toolkt.geometry.BoundingBox
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Span
import dev.toolkt.geometry.SpatialObject
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BezierCurveTests {
    @Test
    fun testPath() {
        val start = Point(272.7262878417969, 159.526123046875)
        val end = Point(425.1772003173828, 304.31105041503906)

        val bezierCurve = BezierCurve(
            start = start,
            firstControl = Point(339.06092071533203, 513.923095703125),
            secondControl = Point(376.43798065185547, 373.21461486816406),
            end = end,
        )

        assertEqualsWithTolerance(
            expected = start,
            actual = bezierCurve.pathFunction.start,
        )

        val t0 = OpenCurve.Coord(t = 0.2)
        val p0 = Point(309.3747166748048, 317.28693518066416)

        assertEqualsWithTolerance(
            expected = p0,
            actual = bezierCurve.pathFunction.evaluate(coord = t0),
        )

        val t1 = OpenCurve.Coord(t = 0.4)
        val p1 = Point(341.0086751708984, 383.43413623046877)

        assertEqualsWithTolerance(
            expected = p1,
            actual = bezierCurve.pathFunction.evaluate(coord = t1),
        )

        val t2 = OpenCurve.Coord(t = 0.6)
        val p2 = Point(369.5635104980469, 385.17942395019537)

        assertEqualsWithTolerance(
            expected = p2,
            actual = bezierCurve.pathFunction.evaluate(coord = t2),
        )

        assertEqualsWithTolerance(
            expected = end,
            actual = bezierCurve.pathFunction.end,
        )
    }

    @Test
    fun testSplitAt() {
        val originalCurve = BezierCurve(
            start = Point(677.1705641585395, 615.752499524604),
            firstControl = Point(655.0464850886674, 157.66163210658488),
            secondControl = Point(406.05454162416845, 128.84363265872344),
            end = Point(321.3376393038343, 341.3936380645191),
        )

        val splitCurve0 = BezierCurve(
            start = Point(677.1705641585395, 615.752499524604),
            firstControl = Point(667.5507169938228, 416.5577304081198),
            secondControl = Point(615.0344199646079, 298.5320748564445),
            end = Point(551.779651601908, 246.22334843757017),
        )

        val splitCurve1 = BezierCurve(
            start = Point(551.779651601908, 246.22334843757017),
            firstControl = Point(469.56664612041277, 178.2382728472403),
            secondControl = Point(369.2163240151058, 221.2681598941017),
            end = Point(321.3376393038343, 341.3936380645191),
        )

        val (curve0, curve1) = originalCurve.splitAt(
            coord = OpenCurve.Coord(t = 0.43483),
        )

        val tolerance = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-2,
        )

        assertEqualsWithTolerance(
            expected = splitCurve0,
            actual = curve0, tolerance,
        )

        assertEqualsWithTolerance(
            expected = splitCurve1,
            actual = curve1, tolerance,
        )
    }

    @Test
    fun testFindBoundingBox() {
        val bezierCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(1068.5394763946533, 253.16610717773438),
            secondControl = Point(-125.00849723815918, 252.71710205078125),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val boundingBox = bezierCurve.findBoundingBox()

        assertEqualsWithTolerance(
            actual = boundingBox,
            expected = BoundingBox(
                topLeft = Point(273.80049324035645, 312.1176405539444),
                width = 397.6180114746094,
                height = 178.08746808863373,
            ),
        )
    }

    @Test
    fun testFindIntersections_LineSegment_BezierCurve_noIntersections() {
        val lineSegment = LineSegment(
            start = Point(506.3340148925781, 185.1540069580078),
            end = Point(515.3410034179688, 470.50299072265625),
        )

        val bezierCurve = BezierCurve(
            start = Point(378.7720947265625, 369.17799377441406),
            firstControl = Point(506.089111328125, 662.9309844970703),
            secondControl = Point(758.8061218261719, 543.6739959716797),
            end = Point(456.3341064453125, 661.4170074462891),
        )

        assertIntersectionsEqual(
            expectedIntersections = emptyList(),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = lineSegment,
                objectBezierCurve = bezierCurve,
            ),
        )
    }

    @Test
    fun testFindIntersections_LineSegment_BezierCurve_oneIntersection() {
        val lineSegment = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        val bezierCurve = BezierCurve(
            start = Point(233.924490, 500.813035),
            firstControl = Point(584.090705, 596.912517),
            secondControl = Point(479.786356, 425.215015),
            end = Point(321.3376393038343, 341.3936380645191),
        )

        assertIntersectionsEqual(
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(401.375136, 394.846031),
                    firstCoord = OpenCurve.Coord(t = 0.565791),
                    secondCoord = OpenCurve.Coord(t = 0.814485),
                ),
            ),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = lineSegment,
                objectBezierCurve = bezierCurve,
            ),
        )
    }

    @Test
    fun testFindIntersections_LineSegment_BezierCurve_threeIntersections() {
        val firstCurve = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        val secondCurve = BezierCurve(
            start = Point(269.9097848060901, 417.78063346119234),
            firstControl = Point(530.8903776607258, 532.365134061869),
            secondControl = Point(212.92561957028738, 357.7268375013964),
            end = Point(510.4081146117205, 360.39333834120225),
        )

        assertIntersectionsEqual(
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(348.833366, 450.206637),
                    firstCoord = OpenCurve.Coord(t = 0.169011),
                    secondCoord = OpenCurve.Coord(t = 0.140208),
                ),
                ExpectedIntersection(
                    point = Point(374.210966, 423.467544),
                    firstCoord = OpenCurve.Coord(t = 0.360656),
                    secondCoord = OpenCurve.Coord(t = 0.542099),
                ),
                ExpectedIntersection(
                    point = Point(428.627571, 366.131519),
                    firstCoord = OpenCurve.Coord(t = 0.771593),
                    secondCoord = OpenCurve.Coord(t = 0.881570),
                ),
            ),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = firstCurve,
                objectBezierCurve = secondCurve,
            ),
        )
    }

    @Test
    @Ignore // FIXME: Figure out the issues with locatePoint
    fun testFindIntersections_LineSegment_BezierCurve_oneIntersection_splitLoop() {
        val lineSegment = LineSegment(
            start = Point(401.14355433959827, 374.2024184921395),
            end = Point(601.1435543395982, 374.2024184921395),
        )

        // Part of a loop
        val bezierCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        assertIntersectionsEqual(
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(501.14355433959827, 374.2024184921395),
                    firstCoord = OpenCurve.Coord(t = 0.2606471534818411),
                    secondCoord = OpenCurve.Coord(t = 0.8083924553357065),
                ),
            ),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = lineSegment,
                objectBezierCurve = bezierCurve,
            ),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_nineIntersections() {
        val firstCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(1068.5394763946533, 253.16610717773438),
            secondControl = Point(-125.00849723815918, 252.71710205078125),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val secondCurve = BezierCurve(
            start = Point(372.6355152130127, 191.58710479736328),
            firstControl = Point(496.35252571105957, 852.5531311035156),
            secondControl = Point(442.4235095977783, -54.72489929199219),
            end = Point(569.3854846954346, 487.569091796875),
        )

        testBezierIntersectionsConsistentSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = Point(400.0364120882783, 325.7513850441302),
                    firstCoord = OpenCurve.Coord(t = 0.638175514633884),
                    secondCoord = OpenCurve.Coord(t = 0.08321298331026583),
                ),
                ExpectedIntersection(
                    point = Point(415.9864000101944, 388.18876477651054),
                    firstCoord = OpenCurve.Coord(t = 0.8267616607759749),
                    secondCoord = OpenCurve.Coord(t = 0.1435234395147944),
                ),
                ExpectedIntersection(
                    point = Point(433.78055261270123, 434.84732656764527),
                    firstCoord = OpenCurve.Coord(t = 0.08361584060373373),
                    secondCoord = OpenCurve.Coord(t = 0.22787694792239874),
                ),
                ExpectedIntersection(
                    point = Point(459.06587349145525, 424.28587808679634),
                    firstCoord = OpenCurve.Coord(t = 0.10193180513525768),
                    secondCoord = OpenCurve.Coord(t = 0.4025176966209491),
                ),
                ExpectedIntersection(
                    point = Point(462.2096738267076, 414.2195778544469),
                    firstCoord = OpenCurve.Coord(t = 0.8785849663620957),
                    secondCoord = OpenCurve.Coord(t = 0.43011874468531425),
                ),
                ExpectedIntersection(
                    point = Point(491.64500747999983, 312.8831093313188),
                    firstCoord = OpenCurve.Coord(t = 0.46681258537835646),
                    secondCoord = OpenCurve.Coord(t = 0.6822325289818921),
                ),
                ExpectedIntersection(
                    point = Point(515.05453270079, 316.0676656534099),
                    firstCoord = OpenCurve.Coord(t = 0.42505456557773347),
                    secondCoord = OpenCurve.Coord(t = 0.8142156753154873),
                ),
                ExpectedIntersection(
                    point = Point(540.6332704779081, 378.60112801527333),
                    firstCoord = OpenCurve.Coord(t = 0.19350344745951725),
                    secondCoord = OpenCurve.Coord(t = 0.9147383049342437),
                ),
                ExpectedIntersection(
                    point = Point(561.4569242282377, 454.624570407085),
                    firstCoord = OpenCurve.Coord(t = 0.9472752305695555),
                    secondCoord = OpenCurve.Coord(t = 0.9785368635085525),
                ),
            ),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_overlapping() {
        // Although these curves look like two nearly-line-shaped innocent
        // curves crossing in the "X" shape, it's actually a single loop
        // curve cut to pieces

        val firstBezierCurve = BezierCurve(
            start = Point(383.0995044708252, 275.80810546875),
            firstControl = Point(435.23948860168457, 325.49310302734375),
            secondControl = Point(510.3655261993408, 384.4371032714844),
            end = Point(614.6575183868408, 453.4740905761719),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(372.14351081848145, 439.6011047363281),
            firstControl = Point(496.5914783477783, 370.8171081542969),
            secondControl = Point(559.4554920196533, 307.91810607910156),
            end = Point(582.3854846954346, 253.8291015625),
        )

        // It's not clear why this test cases succeeds and the next one (similar) fails
        testBezierIntersectionsConsistentSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = Point(488.177482, 364.171107),
                    secondCoord = OpenCurve.Coord(t = 0.378574),
                    firstCoord = OpenCurve.Coord(t = 0.538009),
                ),
            ),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_splitLoop() {
        // A loop split at its top

        val firstBezierCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(684.4749774932861, 329.1851005554199),
            secondControl = Point(591.8677291870117, 214.5483512878418),
            end = Point(492.59773540496826, 197.3452272415161),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = Point(492.59773540496826, 197.3452272415161),
                    firstCoord = OpenCurve.Coord(t = 0.9999999925494194),
                    secondCoord = OpenCurve.Coord(t = 0.0),
                ),
                ExpectedIntersection(
                    point = Point(501.14355433959827, 374.2024184921395),
                    firstCoord = OpenCurve.Coord(t = 0.2606471534818411),
                    secondCoord = OpenCurve.Coord(t = 0.8083924553357065),
                ),
            ),
        )

        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                // FIXME: At least one intersection should be found, but none are
//                ExpectedIntersection(
//                    firstCoord = OpenCurve.Coord(t = 0.0),
//                    point = Point(501.579334, 374.596689),
//                    secondCoord = OpenCurve.Coord(t = 0.0),
//                ),
            ),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_cutLoop() {
        // A loop cut into two pieces that make it non-obvious that any loop
        // is involved at all (for some reason, possibly numeric accuracy, this
        // one is not problematic!)

        val firstBezierCurve = BezierCurve(
            start = Point(247.45586850992547, 379.490073683598),
            firstControl = Point(422.61086805841114, 396.6670752291757),
            secondControl = Point(531.4859546756852, 386.71814287026064),
            end = Point(594.0656015814893, 364.6746085219802),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(452.41959820093143, 239.38755149520694),
            firstControl = Point(410.63096772289646, 281.7264423034185),
            secondControl = Point(385.13020832675465, 365.70689316897005),
            end = Point(405.2940882855255, 513.4262225999319),
        )

        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    // A reasonable approximation of the intersection point
                    point = Point(398.9586117177181, 388.1522281575668),
                    firstCoord = OpenCurve.Coord(t = 0.326171875),
                    secondCoord = OpenCurve.Coord(t = 0.671875),
                ),
            ),
        )

        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    // A slightly different but also reasonable approximation
                    point = Point(399.1359546266406, 388.1678196955365),
                    firstCoord = OpenCurve.Coord(t = 0.32803571347803123),
                    secondCoord = OpenCurve.Coord(t = 0.672939435717393),
                ),
            ),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_anotherCutLoop() {
        // A loop cut into two pieces that make it non-obvious that any loop
        // is involved at all (for some reason, possibly numeric accuracy, this
        // one IS problematic and confuses the equation solving algorithm)

        // The original loop curve:
        // start = Point(233.92449010844575, 500.813035986871),
        // firstControl = Point(863.426829231712, 303.18800785949134),
        // secondControl = Point(53.73076075494464, 164.97814335091425),
        // end = Point(551.3035908506827, 559.7310384198445),

        val firstBezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(422.77519184542564, 441.5255275486571),
            secondControl = Point(482.0980368984025, 387.5853838361354),
            end = Point(486.0476425340348, 351.778389940191),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(382.2960291124364, 335.5675928528492),
            firstControl = Point(370.41409366476535, 370.845949740462),
            secondControl = Point(402.03174182196125, 441.30516989916543),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val expectedIntersections = listOf(
            ExpectedIntersection(
                // This is a reasonable approximation of the intersection point
                point = Point(413.8638152871538, 426.9971560440854),
                firstCoord = OpenCurve.Coord(t = 0.438232421875),
                secondCoord = OpenCurve.Coord(t = 0.5462646484375),
            ),
        )

        testBezierIntersectionsVariousSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedDefaultIntersections = expectedIntersections,
            expectedSubdivisionIntersections = expectedIntersections,
            expectedEquationSolvingIntersections = emptyList(),
        )
    }

    @Test
    @Ignore // FIXME: Fix various issues with self-intersection
    fun testFindIntersections_BezierCurve_oneSelfIntersection() {
        // A loop
        val bezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(863.426829231712, 303.18800785949134),
            secondControl = Point(53.73076075494464, 164.97814335091425),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        // Correct values
        val expectedIntersectionPoint = Point(413.87430209404283, 426.9901974419915)
        val expectedTValue1 = 0.131531503613082
        val expectedTValue2 = 0.8639172755496

        // FIXME: Multum intersection points are found, as a curve has infinite
        //        common points with itself (maybe it makes sense?)
        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = bezierCurve, secondCurve = bezierCurve, expectedIntersection = listOf(
                ExpectedIntersection(
                    point = expectedIntersectionPoint,
                    firstCoord = OpenCurve.Coord(t = expectedTValue1),
                    secondCoord = OpenCurve.Coord(t = expectedTValue2),
                ),
            )
        )

        // FIXME: One intersection is found, it's the point on curve, but
        //        otherwise its position is seemingly random
        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = bezierCurve,
            secondCurve = bezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = expectedIntersectionPoint,
                    firstCoord = OpenCurve.Coord(t = expectedTValue1),
                    secondCoord = OpenCurve.Coord(t = expectedTValue2),
                ),
            ),
        )
    }

    @Test
    fun testTrim() {
        val bezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(863.426829231712, 303.18800785949134),
            secondControl = Point(53.73076075494464, 164.97814335091425),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val startCoord = OpenCurve.Coord.of(t = 0.2)!!
        val endCoord = OpenCurve.Coord.of(t = 0.8)!!

        val subCurve = bezierCurve.trim(
            coordRange = startCoord..endCoord,
        )

        assertEqualsWithTolerance(
            expected = bezierCurve.evaluate(startCoord),
            actual = subCurve.start,
        )

        assertEqualsWithTolerance(
            expected = bezierCurve.evaluate(endCoord),
            actual = subCurve.end,
        )

        subCurve.basisFunction.sample(16).forEach { sample ->
            assertTrue(
                bezierCurve.containsPoint(
                    Point(pointVector = sample.point)
                ),
            )
        }
    }

    @Test
    fun testArcLength_1() {
        val bezierCurve = BezierCurve(
            start = Point(135.79737730246416, 439.6728622250703),
            firstControl = Point(978.1268209204347, 41.5872294779947),
            secondControl = Point(172.74402314583494, 556.2205326485109),
            end = Point(816.1805894519998, 252.34111123735875),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_2() {
        val bezierCurve = BezierCurve(
            start = Point(214.4750734145391, 580.1582697318154),
            firstControl = Point(459.06654332802054, 410.5514167308793),
            secondControl = Point(494.8961346271062, 709.426164755474),
            end = Point(493.69262277805865, 493.46849289090096),
        )


        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_3() {
        val bezierCurve = BezierCurve(
            start = Point(115.117470341419, 240.87622627478413),
            firstControl = Point(394.08377235035323, 288.222150751084),
            secondControl = Point(386.29593303733736, 113.4945184882381),
            end = Point(121.65293879167893, 286.28303378710916),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_4() {
        val bezierCurve = BezierCurve(
            start = Point(530.4940814557458, 241.67258884541297),
            firstControl = Point(961.5421048139829, 329.8998894073666),
            secondControl = Point(236.94848087916398, 6.644895194057426),
            end = Point(476.141458980871, 199.36056001431643),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_5() {
        val bezierCurve = BezierCurve(
            start = Point(545.4055741418306, 371.4355422090739),
            firstControl = Point(1039.2189040443727, 552.7224200932396),
            secondControl = Point(204.85189832277592, 301.5071262030401),
            end = Point(622.1634621546991, 544.4475351119727),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_6() {
        val bezierCurve = BezierCurve(
            start = Point(770.4512185977392, 659.8671607807919),
            firstControl = Point(423.98515008070535, 350.565207789301),
            secondControl = Point(748.4946922271342, 426.77022558430326),
            end = Point(424.87641671667825, 682.1364161784095),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    private val arcLengthVerificationTolerance = NumericObject.Tolerance.Relative(
        // 0.5 %
        relativeTolerance = 0.005,
    )

    private val arcLengthLocationTolerance = NumericObject.Tolerance.Absolute(
        absoluteTolerance = 1e-3,
    )

    private fun testPrimaryArcLength(
        bezierCurve: BezierCurve,
    ) {
        val expectedArcLength = bezierCurve.basisFunction.primaryArcLengthNearlyExact

        val actualArcLength = bezierCurve.totalArcLength

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = actualArcLength,
            tolerance = arcLengthVerificationTolerance,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = bezierCurve.calculateArcLengthUpTo(
                endCoord = OpenCurve.Coord.end,
            ),
            tolerance = arcLengthVerificationTolerance,
        )

        val locatedCoord = assertNotNull(
            bezierCurve.locateArcLength(
                arcLength = actualArcLength,
                tolerance = NumericObject.Tolerance.Absolute(
                    absoluteTolerance = 1e-2,
                ),
            ),
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.end,
            actual = locatedCoord,
            tolerance = arcLengthLocationTolerance,
        )
    }

    private fun testPartialArcLength(
        bezierCurve: BezierCurve,
        endCoord: OpenCurve.Coord,
    ) {
        val expectedArcLength = bezierCurve.basisFunction.calculatePrimaryArcLengthBruteForce(
            range = 0.0..endCoord.t,
        )

        val actualArcLength = bezierCurve.calculateArcLengthUpTo(
            endCoord = endCoord,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = actualArcLength,
            tolerance = arcLengthVerificationTolerance,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = bezierCurve.trimTo(endCoord = endCoord).totalArcLength,
            tolerance = arcLengthVerificationTolerance,
        )

        val locatedCoord = assertNotNull(
            bezierCurve.locateArcLength(
                arcLength = actualArcLength,
                tolerance = arcLengthLocationTolerance,
            ),
        )

        assertEqualsWithTolerance(
            expected = endCoord,
            actual = locatedCoord,
            tolerance = arcLengthLocationTolerance,
        )
    }

    internal fun testBezierIntersectionsConsistentSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedIntersection: List<ExpectedIntersection>,
    ) {
        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            expectedIntersection = expectedIntersection,
        )

        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            expectedIntersection = expectedIntersection,
        )
    }

    private fun testBezierIntersectionsVariousSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedDefaultIntersections: List<ExpectedIntersection>,
        expectedEquationSolvingIntersections: List<ExpectedIntersection>,
        expectedSubdivisionIntersections: List<ExpectedIntersection>,
    ) {
        val numericObjectToleranceAbsolute = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-4,
        )

        val spatialTolerance = SpatialObject.SpatialTolerance(
            spanTolerance = Span.of(value = 0.1),
        )

        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersections(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                    tolerance = spatialTolerance,
                )
            },
            expectedIntersections = expectedDefaultIntersections,
            tolerance = numericObjectToleranceAbsolute,
        )

        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsByEquationSolving(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                )
            },
            expectedIntersections = expectedEquationSolvingIntersections,
            tolerance = numericObjectToleranceAbsolute,
        )


        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsBySubdivision(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                    tolerance = spatialTolerance,
                )
            },
            expectedIntersections = expectedSubdivisionIntersections,
            tolerance = numericObjectToleranceAbsolute,
        )
    }

    private fun testBezierIntersectionsByEquationSolvingSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedIntersection: List<ExpectedIntersection>,
    ) {
        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsByEquationSolving(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                )
            },
            expectedIntersections = expectedIntersection,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 1e-4,
            ),
        )
    }

    private fun testBezierIntersectionsBySubdivisionSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedIntersection: List<ExpectedIntersection>,
    ) {
        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsBySubdivision(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                    tolerance = SpatialObject.SpatialTolerance(
                        spanTolerance = Span.of(value = 0.1),
                    ),
                )
            },
            expectedIntersections = expectedIntersection,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 0.1,
            ),
        )
    }
}
