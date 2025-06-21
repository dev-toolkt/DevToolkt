package dev.toolkt.math.algebra

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsZeroWithTolerance
import dev.toolkt.core.range.copy
import dev.toolkt.core.range.extend
import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.range.mid
import dev.toolkt.core.range.width

interface RealFunction<out B> : Function<Double, B> {
    data class Sample<out B>(
        val a: Double,
        val b: B,
    )
}

fun <B> RealFunction<B>.sample(
    linSpace: LinSpace,
): List<RealFunction.Sample<B>> = linSpace.generate().map { a ->
    val b = apply(a)

    RealFunction.Sample(
        a = a,
        b = b,
    )
}.toList()

fun <B, C> RealFunction<B>.map(
    transform: (B) -> C,
): RealFunction<C> = MappedRealFunction(
    function = this,
    transform = transform,
)

private class MappedRealFunction<B, out C>(
    private val function: RealFunction<B>,
    private val transform: (B) -> C,
) : RealFunction<C> {
    override fun apply(a: Double): C = transform(function.apply(a))
}

/**
 * Assuming this function is monotonic, solves f(x) = y for x.
 *
 * @return The value of x such that f(x) = y, or null if f(x) != y in the given
 * range.
 */
fun RealFunction<Double>.solveEqualityByBisection(
    y: Double,
    range: ClosedFloatingPointRange<Double>,
    maxIterationCount: Int = 1000,
    tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
): Double? = object : RealFunction<Double> {
    override fun apply(
        a: Double,
    ): Double = this@solveEqualityByBisection.apply(a) - y
}.findRootByBisection(
    range = range,
    maxIterationCount = maxIterationCount,
    tolerance = tolerance,
)

/**
 * Assuming this function is monotonic and changes sign over the interval,
 * finds a root of the function using the bisection method.
 *
 * @return The root of the function, or null if the function does not change
 * sign over the interval.
 */
fun RealFunction<Double>.findRootByBisection(
    range: ClosedFloatingPointRange<Double>,
    maxIterationCount: Int = 1000,
    tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
): Double? {
    // If a solution is on the edge of the range (within tolerance), we want
    // to find it
    val range = range.extend(tolerance.absoluteTolerance)

    if (apply(range.start) * apply(range.endInclusive) > 0) {
        // Function does not change sign over the interval.
        return null
    }

    tailrec fun findRootByBisectionRecursively(
        narrowedRange: ClosedFloatingPointRange<Double>,
        iterationCount: Int,
    ): Double? {
        val start = narrowedRange.start
        val mid = narrowedRange.mid

        if (narrowedRange.width.equalsZeroWithTolerance(tolerance = tolerance) || iterationCount >= maxIterationCount) {
            return mid
        }

        val fMid = apply(mid)

        if (fMid.equalsZeroWithTolerance(tolerance = tolerance)) {
            return mid
        }

        return when {
            apply(start) * fMid < 0 -> findRootByBisectionRecursively(
                narrowedRange = narrowedRange.copy(endInclusive = mid),
                iterationCount = iterationCount + 1,
            )

            else -> findRootByBisectionRecursively(
                narrowedRange = narrowedRange.copy(start = mid),
                iterationCount = iterationCount + 1,
            )
        }
    }

    return findRootByBisectionRecursively(
        narrowedRange = range,
        iterationCount = 0,
    )
}
