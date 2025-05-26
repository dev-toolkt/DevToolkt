package dev.toolkt.math.alegebra.polynomials

import dev.toolkt.math.algebra.polynomials.ConstantPolynomial
import dev.toolkt.math.algebra.polynomials.HighPolynomial
import dev.toolkt.math.algebra.polynomials.LinearPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.polynomials.QuadraticPolynomial
import dev.toolkt.math.algebra.polynomials.times
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class QuadraticPolynomialTests {
    @Test
    fun testNormalized() {
        assertEquals(
            expected = ConstantPolynomial(
                a0 = -11.9,
            ),
            actual = QuadraticPolynomial.normalized(
                a0 = -11.9,
                a1 = 0.0,
                a2 = 0.0,
            ),
        )

        assertEquals(
            expected = LinearPolynomial(
                a0 = -11.9,
                a1 = 2.0,
            ),
            actual = QuadraticPolynomial.normalized(
                a0 = -11.9,
                a1 = 2.0,
                a2 = 0.0,
            ),
        )
    }

    @Test
    fun testPlus_linear() {
        val pa = QuadraticPolynomial(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
        )

        val pb = LinearPolynomial(
            a0 = 2.0,
            a1 = 21.5,
        )

        val sum: QuadraticPolynomial = pa + pb

        assertEqualsWithTolerance(
            expected = QuadraticPolynomial(
                a0 = 14.0,
                a1 = 24.0,
                a2 = 3.4,
            ),
            actual = sum,
        )

        assertEqualsWithTolerance(
            expected = sum,
            actual = pb + pa,
        )
    }

    @Test
    fun testPlus_quadratic() {
        val pa = QuadraticPolynomial(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
        )

        val pb = QuadraticPolynomial(
            a0 = 2.0,
            a1 = 21.5,
            a2 = 13.4,
        )

        val sum = pa + pb

        assertEqualsWithTolerance(
            expected = QuadraticPolynomial(
                a0 = 14.0,
                a1 = 24.0,
                a2 = 16.8,
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
        val pa = QuadraticPolynomial(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )
        val pb = Polynomial.constant(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = QuadraticPolynomial(
                a0 = 4.0,
                a1 = -6.0,
                a2 = 2.0,
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
        val pa = QuadraticPolynomial(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val pb = Polynomial.linear(
            a0 = -1.0,
            a1 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = Polynomial.cubic(
                a0 = -2.0,
                a1 = 7.0,
                a2 = -7.0,
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
    fun testTimes_quadratic() {
        val pa = QuadraticPolynomial(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val pb = QuadraticPolynomial(
            a0 = 4.0,
            a1 = -1.0,
            a2 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial(
                a0 = 8.0,
                -14.0,
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
        val pa = QuadraticPolynomial(
            a0 = 1.0,
            a1 = -2.0,
            a2 = 1.0,
        )

        val roots: Pair<Double, Double> = assertNotNull(
            pa.findRoots(),
        )

        assertEqualsWithTolerance(
            expected = listOf(1.0, 1.0),
            actual = roots.toList(),
        )
    }

    @Test
    fun testFindRoots_twoRoots() {
        val pa = QuadraticPolynomial(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val roots = assertNotNull(
            pa.findRoots(),
        )

        assertEqualsWithTolerance(
            expected = listOf(1.0, 2.0),
            actual = roots.toList(),
        )
    }

    @Test
    fun testFindRoots_noRoots() {
        val pa = QuadraticPolynomial(
            a0 = 1.0,
            a1 = 0.0,
            a2 = 1.0,
        )

        assertNull(
            pa.findRoots(),
        )
    }
}
