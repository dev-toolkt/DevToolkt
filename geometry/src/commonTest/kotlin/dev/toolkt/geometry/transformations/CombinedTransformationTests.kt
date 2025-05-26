package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.RelativeAngle
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test

class CombinedTransformationTests {
    @Test
    fun testToUniversal() {
        val point = Point(12.345, 56.789)

        val combinedTransformation = Transformation.combine(
            transformations = listOf(
                PrimitiveTransformation.Translation(
                    tx = 1.234,
                    ty = 5.678,
                ),
                PrimitiveTransformation.Rotation.relative(
                    angle = RelativeAngle.Radial(
                        fi = 0.123,
                    ),
                ),
                PrimitiveTransformation.Translation(
                    tx = 2.234,
                    ty = 6.678,
                ),
                PrimitiveTransformation.Translation(
                    tx = 3.234,
                    ty = 7.678,
                ),
                PrimitiveTransformation.Rotation.relative(
                    angle = -RelativeAngle.Radial(
                        fi = 1.123,
                    ),
                ),
            ),
        )

        val universalTransformation = combinedTransformation.toUniversal

        assertEqualsWithTolerance(
            expected = combinedTransformation.transform(point),
            actual = universalTransformation.transform(point),
        )
    }
}
