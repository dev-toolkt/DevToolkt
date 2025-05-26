package dev.toolkt.core.iterable

data class LinSpace(
    val range: ClosedFloatingPointRange<Double> = 0.0..1.0,
    val sampleCount: Int,
) {
    companion object {
        fun generate(
            range: ClosedFloatingPointRange<Double> = 0.0..1.0,
            sampleCount: Int,
        ) = LinSpace(
            range = range,
            sampleCount = sampleCount,
        ).generate()

        fun generateSubRanges(
            range: ClosedFloatingPointRange<Double> = 0.0..1.0,
            sampleCount: Int,
        ) = LinSpace(
            range = range,
            sampleCount = sampleCount,
        ).generateSubRanges()
    }

    init {
        require(sampleCount >= 2) { "n must be at least 2 to include both boundaries" }
    }

    val x0: Double
        get() = range.start

    val x1: Double
        get() = range.endInclusive

    fun generate(): Sequence<Double> {
        val step = (x1 - x0) / (sampleCount - 1)
        return generateSequence(0) { it + 1 }.take(sampleCount).map { i -> x0 + i * step }
    }

    fun generateSubRanges(): Sequence<ClosedFloatingPointRange<Double>> = generate().zipWithNext { x0, x1 ->
        x0..x1
    }

    val step: Double
        get() = (x1 - x0) / (sampleCount - 1)

    fun asArray(): List<Double> = object : AbstractList<Double>() {
        override val size: Int = sampleCount

        override fun get(index: Int): Double = x0 + index * step
    }
}
