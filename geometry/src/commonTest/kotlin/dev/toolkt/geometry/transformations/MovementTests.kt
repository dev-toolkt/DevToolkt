package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.RelativeAngle
import dev.toolkt.geometry.Vector2
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test

class MovementTests {
    val transformation = Movement(
        rotation = PrimitiveTransformation.Rotation.relative(
            angle = RelativeAngle.ofDegrees(value = 20.0),
        ),
        translation = PrimitiveTransformation.Translation(
            translationVector = Vector2(10.23, 40.56),
        ),
    )

    @Test
    fun testTransformation() {
        val point = Point(12.345, 56.789)

        val transformedPoint = transformation.transform(point)

        assertEqualsWithTolerance(
            expected = Point(
                x = 12.345 * 0.9396926207859084 - 56.789 * 0.3420201433256687 + 10.23,
                y = 12.345 * 0.3420201433256687 + 56.789 * 0.9396926207859084 + 40.56,
            ),
            actual = transformedPoint,
        )
    }

    @Test
    fun testInverse() {
        val originalPoint = Point(12.345, 56.789)

        val invertedTransformation = transformation.invert()
        val transformedPoint = transformation.transform(originalPoint)

        assertEqualsWithTolerance(
            expected = originalPoint,
            actual = invertedTransformation.transform(transformedPoint),
        )
    }
}
