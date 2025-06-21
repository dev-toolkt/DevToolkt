package dev.toolkt.math.algebra

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsZeroWithTolerance
import kotlin.math.sqrt
import kotlin.math.hypot
import kotlin.math.atan2
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

data class Complex(
    val real: Double,
    val imaginary: Double,
) : NumericObject {
    constructor(
        real: Double,
    ) : this(
        real = real,
        imaginary = 0.0,
    )

    companion object {
        val Zero = Complex(
            real = 0.0,
            imaginary = 0.0,
        )

        val One = Complex(
            real = 1.0,
            imaginary = 0.0,
        )
    }

    fun toReal(
        tolerance: NumericObject.Tolerance.Absolute,
    ): Double? = when {
        imaginary.equalsZeroWithTolerance(tolerance = tolerance) -> real
        else -> null
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Complex -> false
        !real.equalsWithTolerance(other.real, tolerance) -> false
        !imaginary.equalsWithTolerance(other.imaginary, tolerance) -> false
        else -> true
    }

    operator fun unaryMinus(): Complex = Complex(
        real = -this.real,
        imaginary = -this.imaginary,
    )

    val conjugate: Complex
        get() = Complex(
            real = this.real,
            imaginary = -this.imaginary,
        )

    val magnitude: Double
        get() = hypot(real, imaginary)

    val argument: Double
        get() = atan2(imaginary, real)

    fun pow(
        exponent: Double,
    ): Complex = Complex(
        real = cos(exponent * argument) * magnitude.pow(exponent),
        imaginary = sin(exponent * argument) * magnitude.pow(exponent),
    )

    operator fun plus(other: Complex): Complex {
        return Complex(
            real = this.real + other.real, imaginary = this.imaginary + other.imaginary
        )
    }

    operator fun minus(other: Complex): Complex = Complex(
        real = this.real - other.real, imaginary = this.imaginary - other.imaginary
    )

    operator fun times(other: Complex): Complex = Complex(
        real = this.real * other.real - this.imaginary * other.imaginary,
        imaginary = this.real * other.imaginary + this.imaginary * other.real
    )

    operator fun times(other: Double): Complex = Complex(
        real = this.real * other, imaginary = this.imaginary * other
    )

    operator fun div(other: Complex): Complex {
        val denominator = other.real * other.real + other.imaginary * other.imaginary

        return Complex(
            real = (this.real * other.real + this.imaginary * other.imaginary) / denominator,
            imaginary = (this.imaginary * other.real - this.real * other.imaginary) / denominator
        )
    }
}

fun Int.toComplex(): Complex = toDouble().toComplex()

fun Double.toComplex(): Complex = Complex(
    real = this,
    imaginary = 0.0,
)

operator fun Double.times(other: Complex): Complex = Complex(
    real = this * other.real,
    imaginary = this * other.imaginary,
)

operator fun Double.div(other: Complex): Complex = toComplex() / other

operator fun Complex.div(other: Double): Complex = Complex(
    real = this.real / other, imaginary = this.imaginary / other
)

fun sqrt(complex: Complex): Complex {
    val magnitude = complex.magnitude
    val angle = atan2(complex.imaginary, complex.real) / 2

    return Complex(
        real = sqrt(magnitude) * cos(angle),
        imaginary = sqrt(magnitude) * sin(angle),
    )
}

fun cbrt(complex: Complex): Complex {
    val magnitude = complex.magnitude
    val angle = atan2(complex.imaginary, complex.real) / 3

    return Complex(
        real = cbrt(magnitude) * cos(angle),
        imaginary = cbrt(magnitude) * sin(angle),
    )
}

fun <T> Iterable<T>.sumOf(
    selector: (T) -> Complex,
): Complex = fold(Complex.Zero) { acc, element ->
    acc + selector(element)
}
