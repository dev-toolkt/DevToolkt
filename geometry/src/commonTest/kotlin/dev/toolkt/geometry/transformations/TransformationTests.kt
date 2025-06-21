package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.RelativeAngle
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TransformationTests {
    @Test
    fun testToMovement() {
        val point = Point(12.345, 56.789)

        val transformations1 = listOf(
            PrimitiveTransformation.Translation(
                tx = 1.234,
                ty = 5.678,
            ),
            PrimitiveTransformation.Rotation.relative(
                angle = RelativeAngle.Radial(
                    fi = 0.123,
                ),
            ),
        )

        val transformations2 = listOf(
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
        )

        val combinedTransformation1 = Transformation.combine(
            transformations = transformations1 + transformations2,
        )

        val movementTransformation = assertNotNull(
            combinedTransformation1.toMovement,
        )

        assertEqualsWithTolerance(
            expected = combinedTransformation1.transform(point),
            actual = movementTransformation.transform(point),
        )

        val combinedTransformation2 = Transformation.combine(
            transformations = transformations1 + listOf(
                PrimitiveTransformation.Scaling(
                    sx = 2.234,
                    sy = 6.678,
                ),
            ) + transformations2,
        )

        assertNull(
            combinedTransformation2.toMovement,
        )
    }

    @Test
    fun testToProjection() {
        val point = Point(12.345, 56.789)

        val transformations1 = listOf(
            PrimitiveTransformation.Translation(
                tx = 1.234,
                ty = 5.678,
            ),
            PrimitiveTransformation.Scaling(
                sx = 2.34,
                sy = 5.67,
            ),
        )

        val transformations2 = listOf(
            PrimitiveTransformation.Translation(
                tx = 2.234,
                ty = 6.678,
            ),
            PrimitiveTransformation.Scaling(
                sx = 3.34,
                sy = 7.67,
            ),
            PrimitiveTransformation.Translation(
                tx = 3.234,
                ty = 7.678,
            ),
        )

        val combinedTransformation1 = Transformation.combine(
            transformations = transformations1 + transformations2,
        )

        val projectionTransformation = assertNotNull(
            combinedTransformation1.toProjection,
        )

        assertEqualsWithTolerance(
            expected = combinedTransformation1.transform(point),
            actual = projectionTransformation.transform(point),
        )

        val combinedTransformation2 = Transformation.combine(
            transformations = transformations1 + listOf(
                PrimitiveTransformation.Rotation.relative(
                    angle = RelativeAngle.Radial(
                        fi = 0.123,
                    ),
                ),
            ) + transformations2,
        )

        assertNull(
            combinedTransformation2.toProjection,
        )
    }
}
