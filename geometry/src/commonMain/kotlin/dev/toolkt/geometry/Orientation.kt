package dev.toolkt.geometry

import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.jvm.JvmInline

/**
 * An orientation in 2D space, represented by one of its two directions. An
 * infinite family of parallel lines with a specific meaning.
 */
@JvmInline
value class Orientation(
    /**
     * One of two directions having this orientation
     */
    val representativeDirection: Direction,
) : RadialObject {
    companion object {
        /**
         * A horizontal direction, i.e. the one determined by the X axis
         */
        val Horizontal = Orientation(
            representativeDirection = Direction.Companion.XAxisPlus,
        )

        /**
         * A vertical direction, i.e. the one determined by the Y axis
         */
        val Vertical = Orientation(
            representativeDirection = Direction.Companion.YAxisPlus,
        )
    }

    val directionVector: Vector2
        get() = representativeDirection.normalizedDirectionVector

    override fun equalsWithRadialTolerance(
        other: RadialObject,
        tolerance: RelativeAngle.RadialTolerance,
    ): Boolean = when {
        other !is Orientation -> false

        representativeDirection.equalsWithRadialTolerance(
            other.representativeDirection,
            tolerance = tolerance,
        ) -> true

        representativeDirection.opposite.equalsWithRadialTolerance(
            other.representativeDirection,
            tolerance = tolerance,
        ) -> true

        else -> false
    }
}
