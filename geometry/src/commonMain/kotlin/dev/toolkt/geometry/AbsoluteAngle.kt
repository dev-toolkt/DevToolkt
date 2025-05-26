package dev.toolkt.geometry

import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Angular measure or simply "angle" (0 <= fi <= PI).
 */
sealed class AbsoluteAngle {
    data object Zero : AbsoluteAngle() {
        override val fi: Double
            get() = 0.0

        override val cosFi: Double
            get() = 1.0

        override val sinFi: Double
            get() = 0.0
    }

    data object Right : AbsoluteAngle() {
        override val fi: Double
            get() = PI / 2

        override val cosFi: Double
            get() = 0.0

        override val sinFi: Double
            get() = 1.0
    }

    data object Straight : AbsoluteAngle() {
        override val fi: Double
            get() = PI

        override val cosFi: Double
            get() = -1.0

        override val sinFi: Double
            get() = 0.0
    }

    data class Radial(
        override val fi: Double
    ) : AbsoluteAngle() {
        companion object {
            /**
             * @param fi The angle value in radians, unconstrained
             */
            fun normalize(
                fi: Double,
            ): AbsoluteAngle.Radial {
                val normalized = fi % (2 * PI)

                return Radial(
                    fi = when {
                        normalized < 0.0 -> normalized + 2 * PI
                        normalized > PI -> normalized - 2 * PI
                        else -> normalized
                    },
                )
            }
        }

        init {
            require(fi in 0.0..PI) {
                "Angle must be in the range [0, PI], but was $fi"
            }
        }

        override val cosFi: Double
            get() = cos(fi)

        override val sinFi: Double
            get() = sin(fi)
    }

    data class Trigonometric(
        override val cosFi: Double,
    ) : AbsoluteAngle() {
        override val fi: Double
            get() = acos(cosFi)

        override val sinFi: Double
            get() = sqrt(1.0 - cosFi * cosFi)
    }

    companion object {
        fun betweenVectors(
            first: Vector2,
            second: Vector2,
        ): AbsoluteAngle {
            val lengthProduct = first.magnitude * second.magnitude

            return AbsoluteAngle.Trigonometric(
                cosFi = first.dot(second) / lengthProduct,
            )
        }
    }

    /**
     * @param fi The angle value in radians (0 <= fi <= PI).
     */
    abstract val fi: Double

    /**
     * Cosine of the angle (0 <= cosFi <= 1, injective)
     */
    abstract val cosFi: Double

    /**
     * Sine of the angle (0 <= sinFi <= 1, non-injective)
     */
    abstract val sinFi: Double
}
