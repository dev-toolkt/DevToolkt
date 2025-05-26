package dev.toolkt.geometry.math

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.LowPolynomial
import dev.toolkt.math.algebra.polynomials.modulate
import kotlin.test.Test

class ParametricPolynomialTests {
    @Test
    fun testNormalize() {
        val modulation = LowPolynomial.Modulation(
            shift = 1.1,
            dilation = 2.2,
        )

        val xPolynomial = CubicPolynomial.normalized(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        ) as CubicPolynomial

        val xPolynomialProjected = xPolynomial.modulate(modulation)

        if (!xPolynomialProjected.equalsWithTolerance(
                CubicPolynomial.normalized(
                    a0 = 0.35462499999999975,
                    a1 = 1.0519318181818185,
                    a2 = -0.7013429752066117,
                    a3 = 0.4289068369646882,
                ),
            )
        ) {
            throw AssertionError()
        }

        val yPolynomial = CubicPolynomial.normalized(
            a0 = 5.678,
            a1 = 6.789,
            a2 = 7.890,
            a3 = 8.901,
        ) as CubicPolynomial

        val yPolynomialProjected = yPolynomial.modulate(modulation)

        if (!yPolynomialProjected.equalsWithTolerance(
                CubicPolynomial.normalized(
                    a0 = 3.143375,
                    a1 = 2.5339772727272725,
                    a2 = -1.1284090909090907,
                    a3 = 0.8359316303531178,
                ),
            )
        ) {
            throw AssertionError()
        }

        val firstParametricPolynomial = ParametricPolynomial(
            xPolynomial = xPolynomial,
            yPolynomial = yPolynomial,
        )

        val secondParametricPolynomial = ParametricPolynomial(
            xPolynomial = xPolynomialProjected,
            yPolynomial = yPolynomialProjected,
        )

        val firstNormalized = firstParametricPolynomial.normalize()
        val secondNormalized = secondParametricPolynomial.normalize()

        assertEqualsWithTolerance(
            expected = firstNormalized,
            actual = secondNormalized,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 1e-3,
            )
        )
    }
}
