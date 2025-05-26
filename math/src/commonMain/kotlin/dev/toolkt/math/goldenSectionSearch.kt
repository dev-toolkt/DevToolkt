package dev.toolkt.math

import dev.toolkt.core.Sample
import dev.toolkt.core.invokeSampling
import dev.toolkt.core.range.copy
import dev.toolkt.core.range.linearlyInterpolate
import dev.toolkt.core.range.mid
import dev.toolkt.core.range.width
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import kotlin.math.sqrt

private val invPhi = (sqrt(5.0) - 1.0) / 2.0  //  1 / phi
private val invPhi2 = (3 - sqrt(5.0)) / 2.0  // 1 / phi^2

/**
 * Finds the minimum value of a function within a specified range using the
 * golden section search method.
 *
 * @param function A function that might be unimodal in this range
 *
 * @return A pair containing the x-coordinate of the minimum and the corresponding
 * value of the function. If the function wasn't in fact unimodal, returns null.
 */
fun <T : Comparable<T>> ClosedFloatingPointRange<Double>.minByWithSelecteeOrNull(
    tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
    function: (Double) -> T,
): Pair<Double, T>? {
    val (minValue, minSelectee) = this.minByUnimodalWithSelectee(
        tolerance = tolerance,
        function = function,
    )

    return when {
        function(start) < minSelectee || function(endInclusive) < minSelectee -> null
        else -> Pair(minValue, minSelectee)
    }
}

/**
 * Finds the minimum value of a function within a specified range using the
 * golden section search method.
 *
 * @param function A function that is assumed to be unimodal in this range
 *
 * @return A pair containing the x-coordinate of the minimum and the corresponding
 * value of the function. If the function wasn't in fact unimodal, the x-value
 * might be arbitrary and the actual minimum within this range is either the
 * range start or its end.
 */
fun <T : Comparable<T>> ClosedFloatingPointRange<Double>.minByUnimodalWithSelectee(
    tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
    function: (Double) -> T,
): Pair<Double, T> = minByUnimodalWithSelecteeRecursive(
    tolerance = tolerance,
    function = function,
    searchRange = this,
)

private tailrec fun <T : Comparable<T>> minByUnimodalWithSelecteeRecursive(
    tolerance: NumericObject.Tolerance.Absolute,
    function: (Double) -> T,
    searchRange: ClosedFloatingPointRange<Double>,
    lowerPoint: Sample<Double, T> = function.invokeSampling(searchRange.linearlyInterpolate(t = invPhi2)),
    upperPoint: Sample<Double, T> = function.invokeSampling(searchRange.linearlyInterpolate(t = invPhi)),
): Pair<Double, T> {
    if (searchRange.width.equalsWithTolerance(0.0, tolerance = tolerance)) {
        return Pair(searchRange.mid, lowerPoint.value)
    }

    return when {
        lowerPoint.value < upperPoint.value -> minByUnimodalWithSelecteeRecursive(
            tolerance = tolerance,
            function = function,
            searchRange = searchRange.copy(endInclusive = upperPoint.argument),
            upperPoint = lowerPoint,
        )

        else -> minByUnimodalWithSelecteeRecursive(
            tolerance = tolerance,
            function = function,
            searchRange = searchRange.copy(start = lowerPoint.argument),
            lowerPoint = upperPoint,
        )
    }
}
