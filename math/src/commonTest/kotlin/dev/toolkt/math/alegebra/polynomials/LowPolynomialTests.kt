package dev.toolkt.math.alegebra.polynomials

import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.LinearPolynomial
import dev.toolkt.math.algebra.polynomials.LowPolynomial
import dev.toolkt.math.algebra.polynomials.LowPolynomial.Dilation
import dev.toolkt.math.algebra.polynomials.LowPolynomial.Modulation
import dev.toolkt.math.algebra.polynomials.LowPolynomial.Shift
import dev.toolkt.math.algebra.polynomials.QuadraticPolynomial
import dev.toolkt.math.algebra.polynomials.dilate
import dev.toolkt.math.algebra.polynomials.modulate
import dev.toolkt.math.algebra.polynomials.shift
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LowPolynomialTests {
    @Test
    fun testDilation() {
        val cubicPolynomial = CubicPolynomial(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        )

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = 1.234,
                a1 = 1.065909090909091,
                a2 = 0.7140495867768594,
                a3 = 0.4289068369646881,
            ), actual = cubicPolynomial.dilate(
                dilation = Dilation(
                    dilation = 2.2,
                ),
            )
        )
    }

    @Test
    fun testShift() {
        val cubicPolynomial = CubicPolynomial(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        )

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = -132.992939,
                a1 = 128.73928999999998,
                a2 = -41.757299999999994,
                a3 = 4.567,
            ),
            actual = cubicPolynomial.shift(
                shift = Shift(
                    shift = 3.3,
                ),
            ),
        )
    }

    @Test
    fun testProject() {
        val cubicPolynomial = CubicPolynomial(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        )

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = 0.35462499999999975,
                a1 = 1.0519318181818185,
                a2 = -0.7013429752066117,
                a3 = 0.4289068369646882,
            ),
            actual = cubicPolynomial.modulate(
                modulation = Modulation(
                    shift = 1.1,
                    dilation = 2.2,
                ),
            ),
        )
    }

    /**
     * Starting from a normal polynomial, project it in an arbitrary way,
     * then normalize. The result should be the same as the original, yielding
     * the used modulation.
     */
    private fun testNormalModulationForward(
        normalPolynomial: LowPolynomial,
        arbitraryModulation: Modulation,
    ) {
        if (!normalPolynomial.isNormalized) {
            throw AssertionError("The original polynomial is not normalized")
        }

        // Modulate the polynomial in an arbitrary way
        val modulatedPolynomial = normalPolynomial.modulate(arbitraryModulation)

        // Normalize the modulated polynomial
        val (normalizedPolynomial, normalModulation) = assertNotNull(
            modulatedPolynomial.normalize(),
        )

        // Ensure that the normal modulation is the original modulation
        assertEqualsWithTolerance(
            expected = arbitraryModulation,
            actual = normalModulation,
        )

        // Ensure that the normalized polynomial is the original polynomial
        assertEqualsWithTolerance(
            expected = normalPolynomial,
            actual = normalizedPolynomial,
        )

        // Modulate the normalized polynomial using the calculated modulation
        val remodulatedPolynomial = normalizedPolynomial.modulate(
            normalModulation,
        )

        // Ensure that it's the same as the originally modulated polynomial
        assertEqualsWithTolerance(
            expected = modulatedPolynomial,
            actual = remodulatedPolynomial,
        )

        // Now invert the modulation and apply it to the modulated polynomial
        val renormalizedPolynomial = modulatedPolynomial.modulate(
            normalModulation.invert(),
        )

        assertEqualsWithTolerance(
            expected = normalPolynomial,
            actual = renormalizedPolynomial,
        )
    }

    /**
     * Starting from an arbitrary polynomial, normalize it and ensure that
     * the result is a normal polynomial.
     */
    private fun testNormalModulationBackward(
        arbitraryPolynomial: LowPolynomial,
        expectedNormalModulation: Modulation,
    ) {
        // Normalize the polynomial
        val (normalizedPolynomial, normalModulation) = assertNotNull(
            arbitraryPolynomial.normalize(),
        )

        // Ensure it's actually normalized
        assertTrue(
            normalizedPolynomial.isNormalized,
        )

        // Check if the calculated normal modulation is as expected
        assertEqualsWithTolerance(
            expected = expectedNormalModulation,
            actual = normalModulation,
        )

        // Recover the original polynomial by applying the modulation
        val recoveredPolynomial = normalizedPolynomial.modulate(normalModulation)

        // Ensure that it's actually the same as the original
        assertEqualsWithTolerance(
            expected = arbitraryPolynomial,
            actual = recoveredPolynomial,
        )

        // Invert the modulation and apply it to the original polynomial
        val renormalizedPolynomial = arbitraryPolynomial.modulate(
            normalModulation.invert(),
        )

        // Ensure that it's the same as the normalized polynomial
        assertEqualsWithTolerance(
            expected = normalizedPolynomial,
            actual = renormalizedPolynomial,
        )
    }

    @Test
    fun testNormalModulationLinear() {
        testNormalModulationForward(
            normalPolynomial = LinearPolynomial(
                a0 = 0.0,
                a1 = 1.0,
            ),
            arbitraryModulation = Modulation(
                dilation = Dilation(dilation = 2.2),
                shift = Shift(shift = 0.0),
            ),
        )
    }

    @Test
    fun testReverseNormalModulationLinear() {
        testNormalModulationBackward(
            arbitraryPolynomial = LinearPolynomial(
                a0 = 1.234,
                a1 = 2.345,
            ),
            expectedNormalModulation = Modulation(
                dilation = Dilation(dilation = 0.42643923240938164),
                shift = Shift(shift = 0.0),
            ),
        )
    }

    @Test
    fun testNormalModulationQuadratic() {
        testNormalModulationForward(
            normalPolynomial = QuadraticPolynomial(
                a0 = 1.234,
                a1 = 0.0,
                a2 = 1.0,
            ),
            arbitraryModulation = Modulation(
                shift = 1.1,
                dilation = 2.2,
            ),
        )
    }

    @Test
    fun testReverseNormalModulationQuadratic() {
        testNormalModulationBackward(
            arbitraryPolynomial = QuadraticPolynomial(
                a0 = 1.234,
                a1 = 2.345,
                a2 = 3.456,
            ),
            expectedNormalModulation = Modulation(
                shift = -0.33926504629629634,
                dilation = 0.537914353639919,
            ),
        )
    }

    @Test
    fun testNormalUpModulationCubic() {
        testNormalModulationForward(
            normalPolynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = 1.0,
                a2 = 0.0,
                a3 = 1.0,
            ),
            arbitraryModulation = Modulation(
                shift = 1.1,
                dilation = 1.0,
            ),
        )
    }

    @Test
    fun testReverseNormalModulationCubicSimple() {
        testNormalModulationBackward(
            arbitraryPolynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = -2.345,
                a2 = 0.0,
                a3 = 4.567,
            ),
            expectedNormalModulation = Modulation(
                dilation = Dilation(dilation = 0.6027302605741011),
                shift = Shift(shift = -0.0),
            ),
        )
    }

    @Test
    fun testReverseNormalModulationCubic() {
        testNormalModulationBackward(
            arbitraryPolynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = 2.345,
                a2 = 3.456,
                a3 = 4.567,
            ),
            expectedNormalModulation = Modulation(
                dilation = Dilation(dilation = 0.6027302605741011),
                shift = Shift(shift = -0.2522443617254215),
            ),
        )
    }

    @Test
    fun findRoots() {
        val foo = CubicPolynomial(
            a0 = -1.270525623545265E14,
            a1 = 5.0792629340980425E14,
            a2 = -5.0707442416598675E14,
            a3 = -1.1358264710330945E12,
        )
    }
}
