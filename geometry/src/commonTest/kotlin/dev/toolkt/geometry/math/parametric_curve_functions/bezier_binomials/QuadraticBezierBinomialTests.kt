package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertNotNull

class QuadraticBezierBinomialTests {
    @Test
    fun testPrimaryArcLength_1() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(100.0, 200.0),
            point2 = Vector2(200.0, 0.0),
        )

        testPrimaryArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
        )

        testPartialArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
            t = 0.2,
        )
    }

    @Test
    fun testPrimaryArcLength_1b() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.123, 1.234),
            point1 = Vector2(123.45, 211.123),
            point2 = Vector2(200.123, 0.123),
        )

        testPrimaryArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
        )

        testPartialArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
            t = 0.4,
        )
    }

    @Test
    fun testPrimaryArcLength_1c() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.01, 0.02),
            point1 = Vector2(103.03, 213.14),
            point2 = Vector2(200.125, 0.06),
        )

        testPrimaryArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
        )

        testPartialArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
            t = 0.9,
        )
    }

    @Test
    fun testPrimaryArcLength_2() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(a0 = 233.93577665116857, a1 = 500.8149820195659),
            point1 = Vector2(a0 = 274.92255777243605, a1 = 487.914717032481),
            point2 = Vector2(a0 = 308.0861294612102, a1 = 475.40978102407877),
        )

        testPrimaryArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
        )

        testPartialArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
            t = 0.1,
        )
    }

    @Test
    fun testPrimaryArcLength_3() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(a0 = 417.93830801380375, a1 = 308.1645617010124),
            point1 = Vector2(a0 = 450.04438645548777, a1 = 309.51100644858667),
            point2 = Vector2(a0 = 403.130758788483, a1 = 312.7706856768071),
        )

        testPrimaryArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
        )

        testPartialArcLength(
            quadraticBezierBinomial = quadraticBezierBinomial,
            t = 0.6,
        )
    }

    @Test
    fun testPrimaryArcLengthApproximate() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(100.0, 200.0),
            point2 = Vector2(200.0, 0.0),
        )

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.primaryArcLengthNearlyExact,
            actual = quadraticBezierBinomial.primaryArcLengthApproximate,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 1e-2,
            ),
        )
    }

    private val arcLengthLocationTolerance = NumericObject.Tolerance.Absolute(
        absoluteTolerance = 1e-2,
    )

    private fun testPrimaryArcLength(
        quadraticBezierBinomial: QuadraticBezierBinomial,
    ) {
        val expectedArcLength = quadraticBezierBinomial.primaryArcLengthNearlyExact

        val actualArcLength = quadraticBezierBinomial.primaryArcLength

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = quadraticBezierBinomial.primaryArcLength,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = quadraticBezierBinomial.calculateArcLengthUpTo(1.0),
        )

        val locatedTValue = assertNotNull(
            quadraticBezierBinomial.locateArcLength(
                arcLength = actualArcLength,
                tolerance = arcLengthLocationTolerance,
            ),
        )

        assertEqualsWithTolerance(
            expected = 1.0,
            actual = locatedTValue,
            tolerance = arcLengthLocationTolerance,
        )
    }

    private fun testPartialArcLength(
        quadraticBezierBinomial: QuadraticBezierBinomial,
        t: Double,
    ) {
        val expectedArcLength = quadraticBezierBinomial.calculatePrimaryArcLengthBruteForce(
            range = 0.0..t,
        )

        val actualArcLength = quadraticBezierBinomial.calculateArcLengthUpTo(t)

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = actualArcLength,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = actualArcLength,
        )

        val locatedTValue = assertNotNull(
            quadraticBezierBinomial.locateArcLength(
                arcLength = actualArcLength,
                tolerance = arcLengthLocationTolerance,
            ),
        )

        assertEqualsWithTolerance(
            expected = t,
            actual = locatedTValue,
            tolerance = arcLengthLocationTolerance,
        )
    }

    @Test
    fun testRaise() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(300.0, 300.0),
            point2 = Vector2(700.0, 100.0),
        )

        val cubicBezierBinomial = quadraticBezierBinomial.raise()

        if (!cubicBezierBinomial.equalsWithTolerance(
                CubicBezierBinomial(
                    point0 = Vector2(0.0, 0.0),
                    point1 = Vector2(200.0, 200.0),
                    point2 = Vector2(433.3333333333333, 233.33333333333331),
                    point3 = Vector2(700.0, 100.0),
                ),
            )
        ) {
            throw AssertionError()
        }

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.point0,
            actual = cubicBezierBinomial.point0,
        )

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.point2,
            actual = cubicBezierBinomial.point3,
        )

        cubicBezierBinomial.sample(16).forEach { (t, point) ->
            assertEqualsWithTolerance(
                expected = quadraticBezierBinomial.apply(t),
                actual = point,
            )
        }

        val loweredQuadraticBezierBinomial = cubicBezierBinomial.lower()

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial,
            actual = loweredQuadraticBezierBinomial,
        )
    }

    @Test
    fun testLower_close() {
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(200.123, 200.123),
            point2 = Vector2(433.345, 233.345),
            point3 = Vector2(700.11, 100.22),
        )

        val loweredQuadraticBezierBinomial = cubicBezierBinomial.lower()

        val expectedQuadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(300.0, 300.0),
            point2 = Vector2(700.0, 100.0),
        )

        assertEqualsWithTolerance(
            expected = expectedQuadraticBezierBinomial,
            actual = loweredQuadraticBezierBinomial,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 0.5,
            ),
        )
    }
}
