package dev.toolkt.core.math

fun avgOf(
    a: Double,
    b: Double,
): Double = (a + b) / 2.0

fun Double.split(): Pair<Int, Double> {
    val integerPart = toInt()
    val fractionalPart = this - integerPart
    return Pair(integerPart, fractionalPart)
}

inline val Double.sq: Double
    get() = this * this

/**
 * Divides the number by the denominator and returns the quotient and remainder.
 *
 * @return A pair of the quotient and the remainder .
 */
fun Double.divideWithRemainder(denominator: Int): Pair<Int, Double> {
    require(denominator >= 1) { "Denominator must be a positive number" }

    val quotient = this / denominator
    val remainder = this % denominator

    return Pair(quotient.toInt(), remainder)
}

/**
 * @param t The value 0..1
 * @return The interpolated value in the range [[x0], [x1]].
 */
fun linearlyInterpolate(
    t: Double,
    x0: Double,
    x1: Double,
): Double {
    require(x0 != x1) { "x0 and x1 must be different to avoid division by zero." }

    return x0 + (t * (x1 - x0))
}
