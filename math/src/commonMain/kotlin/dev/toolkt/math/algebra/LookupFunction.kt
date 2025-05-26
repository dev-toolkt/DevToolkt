package dev.toolkt.math.algebra

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.iterable.mapCarrying
import dev.toolkt.core.range.rescaleTo

data class LookupFunction(
    /**
     * The domain of this function (x0, x1)
     */
    private val range: ClosedFloatingPointRange<Double>,
    /**
     * f(x_i)
     */
    private val intermediateValues: List<Double>,
    /**
     * f(x1)
     */
    private val finalValue: Double,
    /**
     * A function calculating f(x_i + t) - f(x_i)
     */
    private val calculateDelta: (xRange: ClosedFloatingPointRange<Double>) -> Double,
) : RealFunction<Double> {
    companion object {
        fun build(
            linSpace: LinSpace,
            /**
             * f(x0)
             */
            initialValue: Double,
            /**
             * The interpolation function
             */
            calculateDelta: (xRange: ClosedFloatingPointRange<Double>) -> Double,
        ): LookupFunction {
            val (intermediateValues, finalValue) = linSpace.generateSubRanges().toList().mapCarrying(
                initialCarry = initialValue,
            ) { accValue, subRange ->
                val deltaValue = calculateDelta(subRange)

                Pair(
                    deltaValue,
                    accValue + deltaValue,
                )
            }

            return LookupFunction(
                range = linSpace.range,
                intermediateValues = intermediateValues,
                finalValue = finalValue,
                calculateDelta = calculateDelta,
            )
        }
    }

    init {
        require(intermediateValues.isNotEmpty())
    }

    private val bucketRange: ClosedFloatingPointRange<Double>
        get() = 0.0..intermediateValues.size.toDouble()

    override fun apply(a: Double): Double {
        val xj = a

        // A floating point number like (index).(t)
        val indexNumber = range.rescaleTo(bucketRange, x = xj)

        when {
            indexNumber < 0.0 -> return intermediateValues.first()
            indexNumber >= intermediateValues.indices.last -> return finalValue
        }

        val index = indexNumber.toInt()

        val intermediateValue = intermediateValues[index]

        val xi = bucketRange.rescaleTo(range, index.toDouble())
        val deltaValue = calculateDelta(xi..xj)

        return intermediateValue + deltaValue
    }
}
