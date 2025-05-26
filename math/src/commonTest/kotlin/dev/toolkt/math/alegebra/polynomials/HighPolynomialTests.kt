package dev.toolkt.math.alegebra.polynomials

import dev.toolkt.math.algebra.polynomials.ConstantPolynomial
import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.HighPolynomial
import dev.toolkt.math.algebra.polynomials.LinearPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.polynomials.QuadraticPolynomial
import dev.toolkt.math.algebra.polynomials.times
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HighPolynomialTests {
    @Test
    fun testNormalized() {
        assertEquals(
            expected = ConstantPolynomial(
                a0 = -11.9,
            ),
            actual = HighPolynomial.normalized(
                listOf(
                    -11.9,
                    0.0,
                    0.0,
                    0.0,
                ),
            ),
        )

        assertEquals(
            expected = LinearPolynomial(
                a0 = -11.9,
                a1 = 2.0,
            ),
            actual = HighPolynomial.normalized(
                listOf(
                    -11.9,
                    2.0,
                    0.0,
                    0.0,
                ),
            ),
        )

        assertEquals(
            expected = QuadraticPolynomial(
                a0 = -11.9,
                a1 = 2.0,
                a2 = 3.0,
            ),
            actual = HighPolynomial.normalized(
                listOf(
                    -11.9,
                    2.0,
                    3.0,
                    0.0,
                ),
            ),
        )

        assertEquals(
            expected = CubicPolynomial(
                a0 = -11.9,
                a1 = 2.0,
                a2 = 3.0,
                a3 = 4.0,
            ),
            actual = HighPolynomial.normalized(
                listOf(
                    -11.9,
                    2.0,
                    3.0,
                    4.0,
                ),
            ),
        )

        assertEquals(
            expected = CubicPolynomial(
                a0 = -11.9,
                a1 = 2.0,
                a2 = 3.0,
                a3 = 4.0,
            ),
            actual = HighPolynomial.normalized(
                listOf(
                    -11.9,
                    2.0,
                    3.0,
                    4.0,
                    0.0,
                ),
            ),
        )

        assertEquals(
            expected = HighPolynomial(
                a0 = -11.9,
                2.0,
                3.0,
                4.0,
                5.0,
            ),
            actual = HighPolynomial(
                a0 = -11.9,
                2.0,
                3.0,
                4.0,
                5.0,
            ),
        )
    }

    @Test
    fun testTimes_constant() {
        val pa = HighPolynomial(
            a0 = -4.0,
            3.0,
            -2.0,
            1.0,
            6.0,
            12.2,
        )

        val pb = Polynomial.constant(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                a0 = -8.0,
                6.0,
                -4.0,
                2.0,
                12.0,
                24.4,
            ),
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_quadratic() {
        val pa = HighPolynomial(
            a0 = -4.0,
            3.0,
            -2.0,
            1.0,
            17.9,
            -2.3,
        )

        val pb = Polynomial.quadratic(
            a0 = 4.0,
            a1 = -1.0,
            a2 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                a0 = -16.0,
                16.0,
                -19.0,
                12.0,
                66.6,
                -25.1,
                38.1,
                -4.6,
            ),
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_cubic() {
        val pa = HighPolynomial(
            a0 = -4.0, 3.0, -2.0, 1.0, 18.9
        )

        val pb = CubicPolynomial(
            a0 = -3.0,
            a1 = 4.0,
            a2 = -1.0,
            a3 = 2.0,
        )

        val product = pa * pb

        val expectedPolynomial = HighPolynomial(
            a0 = 12.0,
            -25.0,
            22.0,
            -22.0,
            -44.7,
            70.6,
            -16.9,
            37.8,
        )

        assertEqualsWithTolerance(
            expected = expectedPolynomial,
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_high() {
        val pa = HighPolynomial(
            a0 = -4.0,
            3.0,
            -2.0,
            1.0,
            18.9,
            -2.0,
        )

        val pb = HighPolynomial(
            a0 = -4.0,
            23.0,
            -2.2,
            1.0,
            77.2,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                a0 = 16.0,
                -104.0,
                85.8,
                -60.6,
                -354.0,
                670.1,
                -240.98,
                100.5,
                1457.08,
                -154.4,
            ),
            actual = product,
        )
    }

    @Test
    fun testDerivative() {
        val highPolynomial = HighPolynomial(
            a0 = 1.0,
            2.1,
            3.4,
            4.5,
            5.7,
            6.7,
        )

        val derivative = assertNotNull(
            highPolynomial.derivative,
        )

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                a0 = 2.1,
                6.8,
                13.5,
                22.8,
                33.5,
            ),
            actual = derivative,
        )
    }

    @Test
    fun testDivide() {
        val highPolynomial = HighPolynomial(
            a0 = -6.0,
            11.0,
            -6.0,
            1.0,
            2.0,
        )

        val (quotient, remainder) = assertNotNull(
            highPolynomial.divide(x0 = 1.0),
        )

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = 8.0,
                a1 = -3.0,
                a2 = 3.0,
                a3 = 2.0,
            ),
            actual = quotient,
        )

        assertEqualsWithTolerance(
            expected = 2.0,
            actual = remainder,
        )
    }

    @Test
    fun testFindRoots() {
        val highPolynomial = HighPolynomial(
            a0 = -4.05318211480636e+17,
            1.33720916235669e+19,
            -1.74033656459737e+20,
            1.18641205512086e+21,
            -4.72731353333192e+21,
            1.15564116811744e+22,
            -1.75176752296017e+22,
            1.60246744255751e+22,
            -8.09146929050218e+21,
            1.73006535868332e+21,
        )

        val expectedRoots = listOf(
            0.08321298331285831,
            0.1435234395326374,
            0.22787694791806082,
            0.40251769663008713,
            0.43011874465177913,
            0.6822325289916767,
            0.8142156752930875,
            0.9147383049567882,
            0.9785368635066114,
        )

        val roots = highPolynomial.findRoots()

        assertEqualsWithTolerance(
            expected = expectedRoots,
            actual = roots.sorted(),
        )
    }
}
