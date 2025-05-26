/*
 * A set of conventional utils that give the two-dimensional algebraic vector
 * a geometric interpretation, connecting the first component (a0) to the X axis
 * and the second component (a1) to the Y axis.
 */
package dev.toolkt.geometry

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.divideWithTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2

fun Vector2(
    x: Double,
    y: Double,
): Vector2 = Vector2(
    a0 = x,
    a1 = y,
)

val Vector2.x
    get() = a0

val Vector2.y
    get() = a1

/**
 * Find the projection scale of this vector onto another vector
 *
 * @param other - the vector to project onto, must not be a zero vector
 */
fun Vector2.findProjectionScale(
    other: Vector2,
    tolerance: NumericObject.Tolerance,
): Double? = this.dot(other).divideWithTolerance(
    other.magnitudeSquared,
    tolerance = tolerance,
)

fun Vector2.rotate(
    angle: RelativeAngle,
): Vector2 = Vector2(
    x = x * angle.cosFi - y * angle.sinFi,
    y = x * angle.sinFi + y * angle.cosFi,
)
