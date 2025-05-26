package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.RelativeAngle
import kotlin.test.Test

class PrimitiveTransformationTests {
    @Test
    fun testTranslationToUniversal() {
        val point = Point(12.345, 56.789)

        val translation = PrimitiveTransformation.Translation(
            tx = 1.234,
            ty = 5.678,
        )

        val universalTransformation = translation.toUniversal

        assertEqualsWithTolerance(
            expected = translation.transform(point),
            actual = universalTransformation.transform(point),
        )
    }


    @Test
    fun testScalingToUniversal() {
        val point = Point(12.345, 56.789)

        val scaling = PrimitiveTransformation.Scaling(
            sx = 2.34,
            sy = 5.67,
        )

        val universalTransformation = scaling.toUniversal

        assertEqualsWithTolerance(
            expected = scaling.transform(point),
            actual = universalTransformation.transform(point),
        )
    }

    @Test
    fun testRotationToUniversal() {
        val point = Point(12.345, 56.789)

        val rotation = PrimitiveTransformation.Rotation.relative(
            angle = RelativeAngle.Radial(
                fi = 0.123,
            ),
        )

        val universalTransformation = rotation.toUniversal

        assertEqualsWithTolerance(
            expected = rotation.transform(point),
            actual = universalTransformation.transform(point),
        )
    }

    @Test
    fun testUniversalMixWith() {
        val point = Point(12.345, 56.789)

        val earlierUniversalTransformation = PrimitiveTransformation.Translation(
            tx = 1.234,
            ty = 5.678,
        ).toUniversal

        val laterUniversalTransformation = PrimitiveTransformation.Rotation.relative(
            angle = RelativeAngle.Radial(
                fi = 0.123,
            ),
        ).toUniversal

        val mixedUniversalTransformation = earlierUniversalTransformation.mixWith(
            laterTransform = laterUniversalTransformation,
        )

        val transformedPoint = mixedUniversalTransformation.transform(point)

        assertEqualsWithTolerance(
            expected = Point(
                x = 5.812329237814888,
                y = 63.66107255482931,
            ),
            actual = transformedPoint,
        )

        assertEqualsWithTolerance(
            expected = laterUniversalTransformation.transform(earlierUniversalTransformation.transform(point)),
            actual = transformedPoint,
        )
    }
}
