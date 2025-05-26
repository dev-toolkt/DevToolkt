package dev.toolkt.geometry

import dev.toolkt.core.ReprObject
import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.jvm.JvmInline

/**
 * A direction in 2D space, represented by a normalized vector. An infinite
 * family of parallel likewise-oriented vectors with a specific meaning.
 */
@JvmInline
value class Direction internal constructor(
    /**
     * Normalized direction vector
     */
    val normalizedDirectionVector: Vector2,
) : RadialObject, ReprObject {
    companion object {
        fun fromAngle(
            angle: RelativeAngle,
        ): Direction = Direction(
            normalizedDirectionVector = Vector2(
                x = angle.cosFi,
                y = angle.sinFi,
            ),
        )

        val XAxisPlus = Direction(
            normalizedDirectionVector = Vector2(
                x = 1.0,
                y = 0.0,
            ),
        )

        val XAxisMinus = Direction(
            normalizedDirectionVector = Vector2(
                x = -1.0,
                y = 0.0,
            ),
        )

        val YAxisPlus = Direction(
            normalizedDirectionVector = Vector2(
                x = 0.0,
                y = 1.0,
            ),
        )

        val YAxisMinus = Direction(
            normalizedDirectionVector = Vector2(
                x = 0.0,
                y = -1.0,
            ),
        )

        fun normalize(
            directionVector: Vector2,
        ): Direction? = directionVector.normalizeOrNull()?.let {
            Direction(normalizedDirectionVector = it)
        }
    }

    init {
        require(normalizedDirectionVector.isNormalized())
    }

    override fun equalsWithRadialTolerance(
        other: RadialObject,
        tolerance: RelativeAngle.RadialTolerance,
    ): Boolean = when {
        other !is Direction -> false

        else -> angle.equalsWithRadialTolerance(
            other.angle,
            tolerance = tolerance,
        )
    }

    val normal: Direction
        get() = Direction(
            normalizedDirectionVector = Vector2(
                x = -normalizedDirectionVector.y,
                y = normalizedDirectionVector.x,
            ),
        )

    /**
     * Angle from the positive X-axis to the direction vector
     */
    private val angle: RelativeAngle.Trigonometric
        get() = RelativeAngle.Trigonometric.of(
            normalizedVector = normalizedDirectionVector,
        )

    val opposite: Direction
        get() = Direction(
            normalizedDirectionVector = -normalizedDirectionVector,
        )

    override fun toReprString(): String {
        return """
            |Direction(
            |  normalizedDirectionVector = ${normalizedDirectionVector.toReprString()},
            |)
        """.trimMargin()
    }
}
