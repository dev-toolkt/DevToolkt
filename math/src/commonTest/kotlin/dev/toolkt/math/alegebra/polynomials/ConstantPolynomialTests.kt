package dev.toolkt.math.alegebra.polynomials

import dev.toolkt.math.algebra.polynomials.ConstantPolynomial
import dev.toolkt.math.algebra.polynomials.times
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test

class ConstantPolynomialTests {
    @Test
    fun testPlus_constant() {
        val pa = ConstantPolynomial(
            a0 = 3.0,
        )

        val pb = ConstantPolynomial(
            a0 = 2.0,
        )

        val sum = pa + pb

        assertEqualsWithTolerance(
            expected = ConstantPolynomial(
                a0 = 5.0,
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
        val pa = ConstantPolynomial(
            a0 = 3.0,
        )

        val pb = ConstantPolynomial(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = ConstantPolynomial(
                a0 = 6.0,
            ),
            actual = product,
        )

        assertEqualsWithTolerance(
            expected = product,
            actual = pb * pa,
        )
    }
}
