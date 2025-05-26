package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance

data class ConstantPolynomial(
    override val a0: Double,
) : SubLinearPolynomial() {
    companion object {
        val zero: ConstantPolynomial = ConstantPolynomial(
            a0 = 0.0,
        )
    }

    override val coefficients: List<Double>
        get() = listOf(
            a0,
        )

    override fun divide(
        x0: Double,
    ): Pair<Polynomial, Double> = Pair(zero, a0)

    override val symmetryAxis: Nothing?
        get() = null

    override fun normalizeSymmetric(): Pair<LowPolynomial, Dilation> {
        throw UnsupportedOperationException()
    }

    override val isNormalized: Boolean
        get() = true

    override fun findRootsAnalytically(): List<Double> = emptyList()

    override fun substituteDirectly(p: LinearPolynomial): LowPolynomial = p

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ConstantPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        else -> true
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun apply(x: Double): Double = a0
}
