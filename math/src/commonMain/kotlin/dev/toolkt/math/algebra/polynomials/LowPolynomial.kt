package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.iterable.uncons
import dev.toolkt.core.iterable.untrail

/**
 * A polynomial of a low degree (at most cubic)
 */
sealed class LowPolynomial : Polynomial {
    sealed class Transformation : NumericObject {
        abstract fun <P : LowPolynomial> transform(
            polynomial: P,
        ): P

        abstract fun invert(): Transformation
    }

    sealed class PrimitiveTransformation : Transformation() {
        abstract val transformationPolynomial: LinearPolynomial

        final override fun <P : LowPolynomial> transform(
            polynomial: P,
        ): P = polynomial.substitute(
            transformationPolynomial,
        )
    }

    data class Shift(
        val shift: Double,
    ) : PrimitiveTransformation() {
        override fun invert(): Shift = Shift(
            shift = -shift,
        )

        fun dilate(
            dilation: Dilation,
        ): Shift = Shift(
            shift = shift * dilation.dilation,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Shift -> false
            !shift.equalsWithTolerance(other.shift, tolerance = tolerance) -> false
            else -> true
        }

        override val transformationPolynomial: LinearPolynomial = LinearPolynomial(
            a0 = -shift,
            a1 = 1.0,
        )
    }

    data class Dilation(
        val dilation: Double,
    ) : PrimitiveTransformation() {
        init {
            require(dilation != 0.0) { "Dilation cannot be zero" }
        }

        override fun invert(): Dilation = Dilation(
            dilation = 1.0 / dilation,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Dilation -> false
            !dilation.equalsWithTolerance(other.dilation, tolerance = tolerance) -> false
            else -> true
        }

        override val transformationPolynomial: LinearPolynomial = LinearPolynomial(
            a0 = 0.0,
            a1 = 1.0 / dilation,
        )
    }

    data class Modulation(
        val dilation: Dilation,
        val shift: Shift,
    ) : Transformation() {
        constructor(
            shift: Double,
            dilation: Double,
        ) : this(
            shift = Shift(shift = shift),
            dilation = Dilation(dilation = dilation),
        )

        override fun <P : LowPolynomial> transform(
            polynomial: P,
        ): P = shift.transform(
            dilation.transform(polynomial),
        )

        override fun invert(): Modulation {
            val invertedDilation = dilation.invert()

            return Modulation(
                dilation = invertedDilation,
                shift = shift.invert().dilate(invertedDilation),
            )
        }

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Modulation -> false
            !shift.equalsWithTolerance(other.shift, tolerance = tolerance) -> false
            !dilation.equalsWithTolerance(other.dilation, tolerance = tolerance) -> false
            else -> true
        }
    }

    fun normalize(): Pair<LowPolynomial, Modulation>? {
        val symmetryAxis = this.symmetryAxis ?: return null

        // A shift from the symmetric form
        val symmetryShift = Shift(
            shift = symmetryAxis,
        )

        val symmetricPolynomial = this.shift(
            symmetryShift.invert(),
        )

        val (normalizedPolynomial, normalDilation) = symmetricPolynomial.normalizeSymmetric()

        val normalModulation = Modulation(
            dilation = normalDilation,
            shift = symmetryShift,
        )

        return Pair(
            normalizedPolynomial,
            normalModulation,
        )
    }


    fun shiftBy(
        dx: Double,
    ): LowPolynomial {
        // f(x - dx)
        return substitute(
            // (x - dx)
            LinearPolynomial(
                a0 = -dx,
                a1 = 1.0,
            ),
        )
    }

    @Suppress("unused")
    val prettyString: String
        get() = coefficients.mapIndexed { index, coefficient ->
            when (index) {
                0 -> coefficient.toString()
                else -> "$coefficient x^$index"
            }
        }.joinToString(separator = " + ")

    override fun findRoots(
        maxDepth: Int,
        guessedRoot: Double,
        tolerance: NumericObject.Tolerance.Absolute,
        areClose: (Double, Double) -> Boolean,
    ): List<Double> = findRootsAnalytically()

    abstract val symmetryAxis: Double?

    abstract fun normalizeSymmetric(): Pair<LowPolynomial, Dilation>

    abstract val isNormalized: Boolean

    abstract fun findRootsAnalytically(): List<Double>

    abstract fun substituteDirectly(
        p: LinearPolynomial,
    ): LowPolynomial
}

fun <P : LowPolynomial> P.substitute(
    p: LinearPolynomial,
): P {
    val result = substituteDirectly(p = p)

    @Suppress("UNCHECKED_CAST") return result as P
}

fun <P : LowPolynomial> P.transform(
    transformation: LowPolynomial.Transformation,
): P = transformation.transform(this)

fun <P : LowPolynomial> P.dilate(
    dilation: LowPolynomial.Dilation,
): P = dilation.transform(this)

fun <P : LowPolynomial> P.shift(
    shift: LowPolynomial.Shift,
): P = shift.transform(this)

fun <P : LowPolynomial> P.modulate(
    modulation: LowPolynomial.Modulation,
): P = modulation.transform(this)

val LowPolynomial.derivativeSubCubic: SubCubicPolynomial
    get() = this.derivative as SubCubicPolynomial

/**
 * A polynomial of a degree smaller than cubic (at most quadratic)
 */
sealed class SubCubicPolynomial : LowPolynomial() {
    final override val a3: Nothing?
        get() = null

    operator fun plus(
        other: SubCubicPolynomial,
    ): SubCubicPolynomial = QuadraticPolynomial.normalized(
        a0 = a0 + other.a0,
        a1 = (a1 ?: 0.0) + (other.a1 ?: 0.0),
        a2 = (a2 ?: 0.0) + (other.a2 ?: 0.0),
    )
}

/**
 * A polynomial of a degree smaller than quadratic (at most linear)
 */
sealed class SubQuadraticPolynomial : SubCubicPolynomial() {
    final override val a2: Nothing?
        get() = null

    operator fun plus(
        other: SubQuadraticPolynomial,
    ): SubQuadraticPolynomial = LinearPolynomial.normalized(
        a0 = a0 + other.a0,
        a1 = (a1 ?: 0.0) + (other.a1 ?: 0.0),
    )
}

/**
 * A polynomial of a degree smaller than linear (effectively constant)
 */
sealed class SubLinearPolynomial : SubQuadraticPolynomial() {
    final override val a1: Nothing?
        get() = null

    operator fun plus(
        other: SubLinearPolynomial,
    ): ConstantPolynomial = ConstantPolynomial(
        a0 + other.a0,
    )
}

/**
 * A polynomial of a degree higher than constant (at least linear)
 */
sealed interface SuperConstantPolynomial : Polynomial {
    override fun divide(
        x0: Double,
    ): Pair<Polynomial, Double> {
        val (highestDegreeCoefficient, lowerDegreeCoefficients) = coefficients.reversed().uncons()!!

        val intermediateCoefficients = lowerDegreeCoefficients.scan(
            initial = highestDegreeCoefficient,
        ) { higherDegreeCoefficient, coefficient ->
            higherDegreeCoefficient * x0 + coefficient
        }

        val (quotientCoefficients, remainder) = intermediateCoefficients.untrail()!!

        val quotient = Polynomial.Companion.normalized(
            coefficients = quotientCoefficients.reversed(),
        )

        return Pair(quotient, remainder)
    }

    override val a1: Double
}

/**
 * A polynomial of a degree higher than linear (at least quadratic)
 */
sealed interface SuperLinearPolynomial : SuperConstantPolynomial {
    override val a2: Double
}

/**
 * A polynomial of a degree higher than quadratic (at least cubic)
 */
sealed interface SuperQuadraticPolynomial : SuperLinearPolynomial {
    override val a3: Double
}
