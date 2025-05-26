package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

fun Polynomial.findRootsNumerically(
    maxDepth: Int,
    guessedRoot: Double,
    tolerance: NumericObject.Tolerance.Absolute,
    areClose: (x0: Double, x1: Double) -> Boolean,
): List<Double> {
    val primaryRoot = findPrimaryRootNumerically(
        maxDepth = maxDepth,
        tolerance = tolerance,
        guessedRoot = guessedRoot,
        areClose = areClose,
    ) ?: return emptyList()

    // TODO:
//        ?: return toComplexPolynomial().findRealRoots(
//            maxDepth = maxDepth,
//            guessedRoot = guessedRoot,
//            tolerance = tolerance,
//        )

    val deflatedPolynomial = this.deflate(
        x0 = primaryRoot,
    )

    val lowerDegreeRoots = deflatedPolynomial.findRoots(
        maxDepth = maxDepth,
        guessedRoot = guessedRoot,
        tolerance = tolerance,
        areClose = areClose,
    )

    return listOf(primaryRoot) + lowerDegreeRoots
}

/**
 * @param tolerance - when p(x0) equals zero within the tolerance, x0 is
 * considered a root
 * @param areClose - when p(x0)) and p(x1) have different signs and x0 and x1
 * "are close" then avg(x0, x1) is considered a root
 *
 * @return a root of the polynomial or null if the root is (potentially) complex
 */
private fun Polynomial.findPrimaryRootNumerically(
    maxDepth: Int,
    guessedRoot: Double,
    tolerance: NumericObject.Tolerance,
    areClose: (x0: Double, x1: Double) -> Boolean,
): Double? {
    val n = degree.toDouble()

    val firstDerivative = derivative
    val secondDerivative = firstDerivative.derivative

    tailrec fun improveRoot(
        approximatedRoot: Double,
        depth: Int,
    ): Double? {
        if (depth > maxDepth) {
            return approximatedRoot
        }

        val p0 = apply(approximatedRoot)

        if (p0.equalsWithTolerance(0.0, tolerance = tolerance)) {
            return approximatedRoot
        }

        val p1 = firstDerivative.apply(approximatedRoot)
        val p2 = secondDerivative.apply(approximatedRoot)

        val g = p1 / p0
        val g2 = g * g
        val h = g2 - p2 / p0

        val i = (n - 1) * (n * h - g2)

        if (i < 0.0) {
            // Entering the complex domain is not supported
            return null
        }

        val d = sqrt(i)

        val gd = listOf(
            g + d,
            g - d,
        ).maxBy(::abs)

        val a = n / gd

        val improvedRoot = approximatedRoot - a

        val improvedP0 = apply(improvedRoot)

        val areSignsDifferent = p0.sign != improvedP0.sign

        if (areSignsDifferent && areClose(approximatedRoot, improvedRoot)) {
            return (approximatedRoot + improvedRoot) / 2.0
        }

        return improveRoot(
            approximatedRoot = improvedRoot,
            depth = depth + 1,
        )
    }

    return improveRoot(
        approximatedRoot = guessedRoot,
        depth = 0,
    )
}
