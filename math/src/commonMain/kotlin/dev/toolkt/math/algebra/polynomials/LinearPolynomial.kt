package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsZeroWithTolerance

data class LinearPolynomial internal constructor(
    override val a0: Double,
    override val a1: Double,
) : SubQuadraticPolynomial(), SuperConstantPolynomial {
    companion object {
        fun normalized(
            a0: Double,
            a1: Double,
            tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
        ): SubQuadraticPolynomial = when {
            a1.equalsZeroWithTolerance(tolerance = tolerance) -> ConstantPolynomial(
                a0 = a0,
            )

            else -> LinearPolynomial(
                a0 = a0,
                a1 = a1,
            )
        }
    }

    override val isNormalized: Boolean
        get() = when {
            !a1.equalsWithTolerance(1.0) -> false
            else -> true
        }

    override val coefficients: List<Double>
        get() = listOf(
            a0,
            a1,
        )

    override fun findRootsAnalytically(): List<Double> = listOf(findRoot())

    override fun substituteDirectly(p: LinearPolynomial): LowPolynomial {
        val result = a0 + a1 * p
        return result as LinearPolynomial
    }

    fun findRoot(): Double = -a0 / a1

    override val symmetryAxis: Double
        get() = 0.0

    override fun normalizeSymmetric(): Pair<LowPolynomial, Dilation> = Pair(
        LinearPolynomial(
            a1 = 1.0,
            a0 = a0,
        ),
        Dilation(
            dilation = 1 / a1,
        ),
    )

    override fun apply(
        x: Double,
    ): Double = a0 + a1 * x

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is LinearPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        else -> true
    }
}
