package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Vector2
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test

class ProjectionTests {
    val transformation = Projection(
        scaling = PrimitiveTransformation.Scaling(
            scaleVector = Vector2(2.34, 5.67),
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
                x = 2.34 * point.x + 10.23,
                y = 5.67 * point.y + 40.56,
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
