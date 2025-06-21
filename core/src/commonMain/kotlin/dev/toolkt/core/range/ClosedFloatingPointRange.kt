package dev.toolkt.core.range

import dev.toolkt.core.math.avgOf
import dev.toolkt.core.math.linearlyInterpolate

/**
 * Normalizes the value to the range [start, end].
 *
 * @return value within 0..1
 */
fun ClosedFloatingPointRange<Double>.normalize(x: Double): Double {
    val x0 = start
    val x1 = endInclusive

    require(x0 != x1) { "x0 and x1 must be different to avoid division by zero." }

    return (x - x0) / (x1 - x0)
}

/**
 * @param t The value 0..1
 * @return The interpolated value in the range [start, endInclusive].
 */
fun ClosedFloatingPointRange<Double>.linearlyInterpolate(t: Double): Double = linearlyInterpolate(
    t = t,
    x0 = start,
    x1 = endInclusive,
)

fun ClosedFloatingPointRange<Double>.rescaleTo(
    targetRange: ClosedFloatingPointRange<Double>,
    x: Double,
): Double {
    val normalized = this.normalize(x)
    return targetRange.linearlyInterpolate(normalized)
}

val ClosedFloatingPointRange<Double>.width: Double
    get() = endInclusive - start

val ClosedFloatingPointRange<Double>.mid: Double
    get() = avgOf(start, endInclusive)

fun ClosedFloatingPointRange<Double>.split(): Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>> {
    val mid = this.mid
    return start..mid to mid..endInclusive
}

fun ClosedFloatingPointRange<Double>.extend(
    bleed: Double,
): ClosedFloatingPointRange<Double> = copy(
    start = start - bleed,
    endInclusive = endInclusive + bleed,
)

fun ClosedFloatingPointRange<Double>.copy(
    start: Double = this.start,
    endInclusive: Double = this.endInclusive,
): ClosedFloatingPointRange<Double> = start..endInclusive

