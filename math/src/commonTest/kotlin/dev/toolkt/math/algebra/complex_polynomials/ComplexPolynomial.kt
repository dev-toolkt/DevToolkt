package dev.toolkt.math.algebra.complex_polynomials

import dev.toolkt.math.algebra.Complex
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.toComplex
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test

class ComplexPolynomialTests {
    @Test
    fun testFindRoots_1() {
        val complexPolynomial = ComplexPolynomial(
            7.0.toComplex(),
            Complex(5.0, -1.0),
            Complex(2.0, 3.0),
            1.0.toComplex(),
        )

        val actualRoots = complexPolynomial.findRoots()

        assertEqualsWithTolerance(
            expected = listOf(
                Complex(-0.869, -0.600),
                Complex(0.372, 1.53),
                Complex(-1.50, -3.93),
            ),
            actual = actualRoots,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 0.01,
            ),
        )
    }
}
