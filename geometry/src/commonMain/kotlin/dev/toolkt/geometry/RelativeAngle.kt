package dev.toolkt.geometry

import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.core.iterable.LinSpace
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * A relative angle between some reference arm and some other arm, in the range
 * -PI <= fi < PI.
 */
sealed class RelativeAngle : RadialObject {
    data class RadialTolerance(
        val fiTolerance: Double,
    ) {
        companion object {
            val zero = RadialTolerance(
                fiTolerance = 0.0,
            )

            val default = RadialTolerance(
                fiTolerance = 1e-3,
            )
        }

        init {
            require(fiTolerance < PI / 8)
        }

        val cosFiThreshold = cos(fiTolerance)
    }

    /**
     * 0° (0)
     */
    data object Zero : RelativeAngle() {
        override fun isZeroWithRadialTolerance(
            tolerance: RadialTolerance,
        ): Boolean = true

        override val isAcute: Boolean
            get() = true

        override val fi: Double
            get() = 0.0

        override val cosFi: Double
            get() = 1.0

        override val sinFi: Double
            get() = 0.0

        override val absolute: RelativeAngle
            get() = Zero

        override val minor: AbsoluteAngle
            get() = AbsoluteAngle.Zero

        override fun unaryMinus(): RelativeAngle = Zero

        override fun minus(
            other: RelativeAngle,
        ): RelativeAngle = -other

        override fun differenceFromRadial(
            minuend: Radial,
        ): RelativeAngle = minuend

        override val differenceFromRight: RelativeAngle
            get() = Right

        override val differenceFromStraight: RelativeAngle
            get() = Straight

        override val differenceFromCake: RelativeAngle
            get() = NegativeStraight
    }

    /**
     * 90° (PI / 2)
     */
    data object Right : RelativeAngle() {
        override fun isZeroWithRadialTolerance(
            tolerance: RadialTolerance,
        ): Boolean = false

        override val isAcute: Boolean
            get() = false

        override val fi: Double
            get() = PI / 2

        override val cosFi: Double
            get() = 0.0

        override val sinFi: Double
            get() = 1.0

        override val absolute: RelativeAngle
            get() = Right

        override val minor: AbsoluteAngle
            get() = AbsoluteAngle.Right

        override fun unaryMinus(): RelativeAngle = Straight

        override fun minus(
            other: RelativeAngle,
        ): RelativeAngle = other.differenceFromRight

        override fun differenceFromRadial(
            minuend: Radial,
        ): RelativeAngle = Radial.normalize(
            fi = minuend.fi - PI / 2,
        )

        override val differenceFromRight: RelativeAngle
            get() = Zero

        override val differenceFromStraight: RelativeAngle
            get() = Straight

        override val differenceFromCake: RelativeAngle
            get() = NegativeStraight
    }

    /**
     * 180° (PI)
     */
    data object Straight : RelativeAngle() {
        override fun isZeroWithRadialTolerance(
            tolerance: RadialTolerance,
        ): Boolean = false

        override val isAcute: Boolean
            get() = false

        override val fi: Double
            get() = PI

        override val cosFi: Double
            get() = -1.0

        override val sinFi: Double
            get() = 0.0

        override val absolute: RelativeAngle
            get() = Straight

        override val minor: AbsoluteAngle
            get() = TODO("Not yet implemented")

        override fun unaryMinus(): RelativeAngle = NegativeStraight

        override fun minus(
            other: RelativeAngle,
        ): RelativeAngle = other.differenceFromStraight

        override fun differenceFromRadial(
            minuend: Radial,
        ): RelativeAngle = Radial.normalize(
            fi = minuend.fi - PI,
        )

        override val differenceFromRight: RelativeAngle
            get() = Right

        override val differenceFromStraight: RelativeAngle
            get() = Zero

        override val differenceFromCake: RelativeAngle
            get() = Straight
    }

    /**
     * -90° (-PI / 2)
     */
    internal data object NegativeStraight : RelativeAngle() {
        override fun isZeroWithRadialTolerance(
            tolerance: RadialTolerance,
        ): Boolean = false

        override val isAcute: Boolean
            get() = false

        override val fi: Double
            get() = -PI / 2

        override val cosFi: Double
            get() = 0.0

        override val sinFi: Double
            get() = -1.0

        override val absolute: RelativeAngle
            get() = Straight

        override val minor: AbsoluteAngle
            get() = AbsoluteAngle.Right

        override fun unaryMinus(): RelativeAngle = Right

        override fun minus(
            other: RelativeAngle,
        ): RelativeAngle = other.differenceFromCake

        override fun differenceFromRadial(
            minuend: Radial,
        ): RelativeAngle = Radial.normalize(
            fi = minuend.fi - 3 * PI / 2,
        )

        override val differenceFromRight: RelativeAngle
            get() = Straight

        override val differenceFromStraight: RelativeAngle
            get() = NegativeStraight

        override val differenceFromCake: RelativeAngle
            get() = Zero
    }

    data class Radial(
        override val fi: Double
    ) : RelativeAngle() {
        override fun isZeroWithRadialTolerance(
            tolerance: RadialTolerance,
        ): Boolean = fi.absoluteValue < tolerance.fiTolerance

        override val isAcute: Boolean
            get() = fi >= 0 && fi < PI / 2

        companion object {
            /**
             * @param fi The relative angle value in radians, unconstrained
             */
            fun normalize(
                fi: Double,
            ): Radial {
                val normalized = fi % (2 * PI)

                return Radial(
                    fi = when {
                        normalized < -PI -> normalized + 2 * PI
                        normalized > PI -> normalized - 2 * PI
                        else -> normalized
                    },
                )
            }
        }

        init {
            require(fi in -PI..PI) {
                "Angle must be in the range [0, 2*PI], but was $fi"
            }
        }

        override val cosFi: Double by lazy { cos(fi) }

        override val sinFi: Double by lazy { sin(fi) }

        override val absolute: RelativeAngle
            get() = Radial(
                fi = fi.absoluteValue,
            )

        override val minor: AbsoluteAngle
            get() = AbsoluteAngle.Radial(
                fi = fi.absoluteValue,
            )

        override fun minus(
            other: RelativeAngle,
        ): RelativeAngle = other.differenceFromRadial(
            minuend = this,
        )

        override fun differenceFromRadial(
            other: Radial,
        ): RelativeAngle = normalize(
            fi = other.fi - fi,
        )

        override fun unaryMinus(): RelativeAngle = normalize(
            fi = -fi,
        )

        override val differenceFromRight: RelativeAngle
            get() = normalize(
                fi = PI / 2 - fi,
            )

        override val differenceFromStraight: RelativeAngle
            get() = normalize(
                fi = PI - fi,
            )

        override val differenceFromCake: RelativeAngle
            get() = normalize(
                fi = 3 * PI / 2 - fi,
            )
    }

    data class Trigonometric(
        override val cosFi: Double,
        override val sinFi: Double,
    ) : RelativeAngle() {
        companion object {
            fun of(
                normalizedVector: Vector2,
            ): Trigonometric {
                require(normalizedVector.isNormalized())

                val cosFi = normalizedVector.x / normalizedVector.magnitude
                val sinFi = normalizedVector.y / normalizedVector.magnitude

                return Trigonometric(
                    cosFi = cosFi,
                    sinFi = sinFi,
                )
            }
        }

        override fun isZeroWithRadialTolerance(
            tolerance: RadialTolerance,
        ): Boolean = when {
            isAcute -> cosFi > tolerance.cosFiThreshold
            else -> false
        }

        override val isAcute: Boolean
            get() = sinFi >= 0 && cosFi >= 0

        override val fi: Double
            get() = atan2(sinFi, cosFi)

        override val absolute: RelativeAngle
            get() = when {
                sinFi >= 0 -> this
                else -> -this
            }

        override val minor: AbsoluteAngle
            get() = AbsoluteAngle.Trigonometric(
                cosFi = cosFi,
            )

        override fun unaryMinus(): RelativeAngle = Trigonometric(
            cosFi = cosFi,
            sinFi = -sinFi,
        )

        override fun minus(other: RelativeAngle): RelativeAngle = Trigonometric(
            cosFi = this.cosFi * other.cosFi + this.sinFi * other.sinFi,
            sinFi = this.sinFi * other.cosFi - this.cosFi * other.sinFi,
        )

        override fun differenceFromRadial(minuend: Radial): RelativeAngle {
            val baseSinFi = minuend.sinFi
            val baseCosFi = minuend.cosFi

            return Trigonometric(
                cosFi = baseCosFi * this.cosFi + baseSinFi * this.sinFi,
                sinFi = baseSinFi * this.cosFi - baseCosFi * this.sinFi,
            )
        }

        override val differenceFromRight: RelativeAngle
            get() = Trigonometric(
                cosFi = sinFi,
                sinFi = -cosFi,
            )

        override val differenceFromStraight: RelativeAngle
            get() = Trigonometric(
                cosFi = -cosFi,
                sinFi = -sinFi,
            )

        override val differenceFromCake: RelativeAngle
            get() = Trigonometric(
                cosFi = -sinFi,
                sinFi = cosFi,
            )
    }

    companion object {
        /**
         * The full angle in radians, i.e. 2*PI.
         */
        val fiFull = 2 * PI

        fun betweenVectors(
            first: Vector2,
            second: Vector2,
        ): RelativeAngle {
            val lengthProduct = first.magnitude * second.magnitude

            return Trigonometric(
                cosFi = first.dot(second) / lengthProduct,
                sinFi = first.cross(second) / lengthProduct,
            )
        }

        /**
         * Generates a list of [n] evenly spaced angles in the range [0, 2*PI).
         */
        fun spectrum(
            n: Int,
        ): List<RelativeAngle> {
            require(n >= 3)

            val firstFi = 0.0
            val lastFi = fiFull - (fiFull / n)

            return LinSpace(
                firstFi..lastFi,
                sampleCount = n,
            ).generate().map { fi ->
                Radial.normalize(fi = fi)
            }.toList()
        }

        /**
         * @param f a fractional value in the range [0, 1)
         */
        fun fractional(
            f: Double,
        ): RelativeAngle = Radial.normalize(
            fi = f * fiFull,
        )

        fun ofDegrees(value: Double): RelativeAngle = Radial.normalize(
            fi = value * PI / 180.0,
        )
    }

    val fiInDegrees: Double
        get() = fi * 180.0 / PI

    abstract fun isZeroWithRadialTolerance(
        tolerance: RadialTolerance = RadialTolerance.default,
    ): Boolean

    override fun equalsWithRadialTolerance(
        other: RadialObject,
        tolerance: RadialTolerance,
    ): Boolean = when {
        other !is RelativeAngle -> false
        else -> {
            val difference = this - other
            difference.isZeroWithRadialTolerance(tolerance = tolerance)
        }
    }

    /**
     * The angle is acute if it is in the range [0, PI / 2), i.e. lies in the
     * first quadrant.
     */
    abstract val isAcute: Boolean

    /**
     * @param fi The angle value in radians
     */
    abstract val fi: Double

    /**
     * Cosine of the angle (-1 <= cosFi <= 1)
     */
    abstract val cosFi: Double

    /**
     * Sine of the angle (-1 <= sinFi <= 1)
     */
    abstract val sinFi: Double

    abstract val absolute: RelativeAngle

    abstract val minor: AbsoluteAngle

    /**
     * -fi
     */
    abstract operator fun unaryMinus(): RelativeAngle

    /**
     * fi - other.fi
     */
    abstract operator fun minus(other: RelativeAngle): RelativeAngle

    /**
     * other.fi - fi
     */
    abstract fun differenceFromRadial(minuend: Radial): RelativeAngle

    /**
     * 90° - fi
     */
    abstract val differenceFromRight: RelativeAngle

    /**
     * 180° - fi
     */
    abstract val differenceFromStraight: RelativeAngle

    /**
     * 270° - fi
     */
    abstract val differenceFromCake: RelativeAngle
}
