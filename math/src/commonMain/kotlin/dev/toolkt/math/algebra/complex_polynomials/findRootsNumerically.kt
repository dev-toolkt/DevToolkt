package dev.toolkt.math.algebra.complex_polynomials

import dev.toolkt.math.algebra.Complex
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.div
import dev.toolkt.math.algebra.sqrt
import dev.toolkt.math.algebra.times

/**
 * Finds all roots of a polynomial numerically using the Laguerre's Method.
 */
fun ComplexPolynomial.findRootsNumerically(
    maxDepth: Int,
    guessedRoot: Complex,
    tolerance: NumericObject.Tolerance.Absolute,
): List<Complex> {
    val primaryRoot = findPrimaryRootNumerically(
        maxDepth = maxDepth,
        tolerance = tolerance,
        guessedRoot = guessedRoot,
    ) ?: return emptyList()

    val deflatedPolynomial = this.deflate(
        x0 = primaryRoot,
    )

    val lowerDegreeRoots = deflatedPolynomial.findRoots(
        maxDepth = maxDepth,
        guessedRoot = guessedRoot,
        tolerance = tolerance,
    )

    return listOf(primaryRoot) + lowerDegreeRoots
}

/**
 * @param tolerance - when p(x0) equals zero within the tolerance, x0 is
 * considered a root
 */
private fun ComplexPolynomial.findPrimaryRootNumerically(
    maxDepth: Int,
    guessedRoot: Complex,
    tolerance: NumericObject.Tolerance.Absolute,
): Complex? {
    val n = degree.toDouble()

    val firstDerivative = derivative
    val secondDerivative = firstDerivative.derivative

    tailrec fun improveRoot(
        approximatedRoot: Complex,
        depth: Int,
    ): Complex? {
        if (depth > maxDepth) {
            return approximatedRoot
        }

        val p0 = apply(approximatedRoot)

        if (p0.equalsWithTolerance(Complex.Companion.Zero, tolerance = tolerance)) {
            return approximatedRoot
        }

        val p1 = firstDerivative.apply(approximatedRoot)
        val p2 = secondDerivative.apply(approximatedRoot)

        val g = p1 / p0
        val g2 = g * g
        val h = g2 - p2 / p0

        val i = (n - 1) * (n * h - g2)

        val d = sqrt(i)

        val gd = listOf(
            g + d,
            g - d,
        ).maxBy(Complex::magnitude)

        val a = n / gd

        val improvedRoot = approximatedRoot - a

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
