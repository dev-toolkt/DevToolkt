package dev.toolkt.core.iterable

import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class LinSpaceTests {
    @Test
    fun testGenerate_oneSample() {
        assertIs<IllegalArgumentException>(
            assertFails {
                LinSpace(
                    range = 1.1..2.2,
                    sampleCount = 1,
                ).generate()
            },
        )
    }

    @Test
    fun testGenerate_twoSamples() {
        val values = LinSpace(
            range = 1.1..2.2,
            sampleCount = 2,
        ).generate()

        assertEquals(
            expected = listOf(1.1, 2.2),
            actual = values.toList(),
        )
    }

    @Test
    fun testGenerate_manySamples() {
        val x0 = 1.1
        val x1 = 2.2
        val w = x1 - x0

        val values = LinSpace(
            range = x0..x1,
            sampleCount = 8,
        ).generate()

        assertEqualsWithTolerance(
            expected = listOf(
                1.1,
                x0 + (1 * w) / 7,
                x0 + (2 * w) / 7,
                x0 + (3 * w) / 7,
                x0 + (4 * w) / 7,
                x0 + (5 * w) / 7,
                x0 + (6 * w) / 7,
                2.2,
            ),
            actual = values.toList(),
        )
    }

    @Test
    fun testGenerateSubRanges_manySamples() {
        val x0 = 1.1
        val x1 = 2.2
        val w = x1 - x0

        val subRanges = LinSpace(
            range = x0..x1,
            sampleCount = 8,
        ).generateSubRanges()

        val i0 = x0 + (1 * w) / 7
        val i1 = x0 + (2 * w) / 7
        val i2 = x0 + (3 * w) / 7
        val i3 = x0 + (4 * w) / 7
        val i4 = x0 + (5 * w) / 7
        val i5 = x0 + (6 * w) / 7

        val expectedRanges = listOf(
            x0..i0,
            i0..i1,
            i1..i2,
            i2..i3,
            i3..i4,
            i4..i5,
            i5..x1,
        )

        val expectedStartValues = expectedRanges.map { it.start }

        assertEqualsWithTolerance(
            expected = expectedStartValues,
            actual = subRanges.map { it.start }.toList(),
        )

        val expectedEndValues = expectedRanges.map { it.endInclusive }

        assertEqualsWithTolerance(
            expected = expectedEndValues,
            actual = subRanges.map { it.endInclusive }.toList(),
        )
    }
}
