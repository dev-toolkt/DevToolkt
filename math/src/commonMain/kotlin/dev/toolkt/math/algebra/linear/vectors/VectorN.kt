package dev.toolkt.math.algebra.linear.vectors

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericObject.Tolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.math.algebra.linear.matrices.matrix2.MatrixNx2
import kotlin.math.sqrt

data class VectorN(
    val a: List<Double>,
) : NumericObject {
    companion object {
        fun full(
            ai: Double,
            n: Double,
        ): VectorN = VectorN(List(n.toInt()) { ai })
    }

    init {
        require(a.isNotEmpty()) { "VectorN cannot be empty" }
    }

    val size: Int
        get() = a.size

    operator fun get(i: Int): Double = a[i]

    val magnitudeSquared: Double
        get() = a.sumOf { it * it }

    val magnitude: Double
        get() = sqrt(magnitudeSquared)

    fun isNormalized(): Boolean = magnitudeSquared.equalsWithTolerance(1.0)

    fun normalize(): VectorN = this / magnitude

    operator fun unaryMinus(): VectorN = VectorN(a.map { -it })

    operator fun plus(
        scalar: Double,
    ): VectorN = VectorN(a.map { it + scalar })

    operator fun minus(
        other: VectorN,
    ): VectorN {
        if (other.size != size) {
            throw IllegalArgumentException("Vectors must be of the same size")
        }
        return VectorN(a.zip(other.a) { x, y -> x - y })
    }

    operator fun times(
        scalar: Double,
    ): VectorN = VectorN(a.map { it * scalar })

    operator fun div(
        scalar: Double,
    ): VectorN = VectorN(a.map { it / scalar })

    fun normalizeOrNull(): VectorN? {
        val normalized = normalize()
        return normalized.takeIf { it.isNormalized() }
    }

    fun dot(
        other: VectorN,
    ): Double {
        if (other.size != size) {
            throw IllegalArgumentException("Vectors must be of the same size")
        }
        return a.zip(other.a).sumOf { (x, y) -> x * y }
    }

    fun applyT(
        other: MatrixNx2,
    ): Vector2 = other.transposed.apply(this)

    fun toList(): List<Double> = a

    override fun toString(): String = "[${a.joinToString(", ")}]"

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is VectorN -> false
        !a.equalsWithTolerance(
            other = other.a,
            tolerance = tolerance,
        ) -> false

        else -> true
    }
}

operator fun Double.times(
    vector: VectorN,
): VectorN = vector * this
