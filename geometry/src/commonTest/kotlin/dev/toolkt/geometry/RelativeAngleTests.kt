package dev.toolkt.geometry

import dev.toolkt.geometry.RelativeAngle.Companion.fiFull
import kotlin.test.Test
import kotlin.test.assertEquals

class RelativeAngleTests {
    @Test
    fun testZeroMinus() {
        assertEquals(
            expected = RelativeAngle.Zero,
            actual = RelativeAngle.Zero - RelativeAngle.Zero,
        )

        assertEquals(
            expected = -RelativeAngle.Straight,
            actual = RelativeAngle.Zero - RelativeAngle.Straight,
        )
    }

    @Test
    fun testSpectrum() {
        assertEqualsWithGeometricTolerance(
            expected = listOf(
                RelativeAngle.Zero,
                RelativeAngle.Radial.normalize(fi = 1 * fiFull / 3),
                RelativeAngle.Radial.normalize(fi = 2 * fiFull / 3),
            ),
            actual = RelativeAngle.spectrum(n = 3),
        )
    }
}
