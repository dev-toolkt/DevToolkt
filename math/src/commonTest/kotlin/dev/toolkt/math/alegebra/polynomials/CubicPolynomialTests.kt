package dev.toolkt.math.alegebra.polynomials

import dev.toolkt.math.algebra.polynomials.ConstantPolynomial
import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.HighPolynomial
import dev.toolkt.math.algebra.polynomials.LinearPolynomial
import dev.toolkt.math.algebra.polynomials.QuadraticPolynomial
import dev.toolkt.math.algebra.polynomials.times
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.math.algebra.polynomials.plus
import kotlin.test.Test
import kotlin.test.assertEquals

class CubicPolynomialTests {
    @Test
    fun testNormalized() {
        assertEquals(
            expected = ConstantPolynomial(
                a0 = -11.9,
            ),
            actual = CubicPolynomial.normalized(
                a0 = -11.9,
                a1 = 0.0,
                a2 = 0.0,
                a3 = 0.0,
            ),
        )

        assertEquals(
            expected = LinearPolynomial(
                a0 = -11.9,
                a1 = 2.0,
            ),
            actual = CubicPolynomial.normalized(
                a0 = -11.9,
                a1 = 2.0,
                a2 = 0.0,
                a3 = 0.0,
            ),
        )

        assertEquals(
            expected = QuadraticPolynomial(
                a0 = -11.9,
                a1 = 2.0,
                a2 = 3.0,
            ),
            actual = CubicPolynomial.normalized(
                a0 = -11.9,
                a1 = 2.0,
                a2 = 3.0,
                a3 = 0.0,
            ),
        )
    }

    @Test
    fun testPlus_constant() {
        val pa = CubicPolynomial(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
            a3 = 5.7,
        )

        val pb = ConstantPolynomial(
            a0 = 3.5,
        )

        val sum = pa + pb

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = 15.5,
                a1 = 2.5,
                a2 = 3.4,
                a3 = 5.7,
            ),
            actual = sum,
        )

        assertEqualsWithTolerance(
            expected = sum,
            actual = pb + pa,
        )
    }

    @Test
    fun testPlus_cubic() {
        val pa = CubicPolynomial(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
            a3 = 5.7,
        )

        val pb = CubicPolynomial(
            a0 = 2.0,
            a1 = 21.5,
            a2 = 13.4,
            a3 = 7.2,
        )

        val sum = pa + pb

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = 14.0,
                a1 = 24.0,
                a2 = 16.8,
                a3 = 12.9,
            ),
            actual = sum,
        )

        assertEqualsWithTolerance(
            expected = sum,
            actual = pb + pa,
        )
    }

    @Test
    fun testTimes_constant() {
        val pa = CubicPolynomial(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )
        val pb = ConstantPolynomial(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = -2.0,
                a1 = 4.0,
                a2 = -6.0,
                a3 = 2.0,
            ),
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_linear() {
        val pa = CubicPolynomial(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )

        val pb = LinearPolynomial(
            a0 = -1.0,
            a1 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                a0 = 1.0,
                -4.0,
                7.0,
                -7.0,
                2.0,
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
        val pa = CubicPolynomial(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )

        val pb = QuadraticPolynomial(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                a0 = -2.0,
                7.0,
                -13.0,
                13.0,
                -6.0,
                1.0,
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
        val pa = CubicPolynomial(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )
        val pb = CubicPolynomial(
            a0 = -3.0,
            a1 = 4.0,
            a2 = -1.0,
            a3 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                3.0,
                -10.0,
                18.0,
                -19.0,
                11.0,
                -7.0,
                2.0,
            ),
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testFindRoots_singleRoot() {
        val polynomial = CubicPolynomial(
            a0 = -1.0,
            a1 = 3.0,
            a2 = -3.0,
            a3 = 1.0,
        )

        val roots = polynomial.findRoots().toSet()

        assertEquals(
            expected = setOf(1.0),
            actual = roots,
        )
    }

    @Test
    fun testFindRoots_twoRoots() {
        val polynomial = CubicPolynomial(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 0.0,
            a3 = 1.0,
        )

        val roots = polynomial.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(-2.0, 1.0),
            actual = roots,
        )
    }

    @Test
    fun testFindRoots_threeRoots() {
        val polynomial = CubicPolynomial(
            a0 = -6.0,
            a1 = 11.0,
            a2 = -6.0,
            a3 = 1.0,
        )

        val roots = polynomial.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(1.0, 2.0, 3.0),
            actual = roots,
        )
    }
}
