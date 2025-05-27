package dev.toolkt.dom.pure

import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class PureDimensionTests {
    @Test
    fun testParse() {
        assertEquals(
            expected = PureDimension(
                value = 123.0,
                unit = PureUnit.Pt,
            ),
            actual = PureDimension.parse("123pt"),
        )

        assertEquals(
            expected = PureDimension(
                value = 123.0,
                unit = PureUnit.Mm,
            ),
            actual = PureDimension.parse("123mm"),
        )

        assertEquals(
            expected = 123.2.px,
            actual = PureDimension.parse("123.2px"),
        )

        assertEquals(
            expected = PureDimension(
                value = 123.0,
                unit = PureUnit.Percent,
            ),
            actual = PureDimension.parse("123%"),
        )
    }

    @Test
    fun testMmInUnit() {
        assertEquals(
            expected = 1.mm,
            actual = 1.mm.inUnit(PureUnit.Mm)
        )

        assertEqualsWithTolerance(
            expected = 0.039370.inch,
            actual = 1.mm.inUnit(PureUnit.Inch)
        )

        assertEqualsWithTolerance(
            expected = 2.834645.pt,
            actual = 1.mm.inUnit(PureUnit.Pt)
        )

        assertEqualsWithTolerance(
            expected = 3.779527.px,
            actual = 1.mm.inUnit(PureUnit.Px)
        )

        assertEqualsWithTolerance(
            expected = 4.860236.inch,
            actual = 123.45.mm.inUnit(PureUnit.Inch)
        )
    }

    @Test
    fun testInchInUnit() {
        assertEquals(
            expected = 1.inch,
            actual = 1.inch.inUnit(PureUnit.Inch)
        )

        assertEquals(
            expected = 25.4.mm,
            actual = 1.inch.inUnit(PureUnit.Mm)
        )

        assertEquals(
            expected = 72.0.pt,
            actual = 1.inch.inUnit(PureUnit.Pt)
        )

        assertEquals(
            expected = 96.0.px,
            actual = 1.inch.inUnit(PureUnit.Px)
        )

        assertEquals(
            expected = 3135.63.mm,
            actual = 123.45.inch.inUnit(PureUnit.Mm)
        )
    }

    @Test
    fun testPtInUnit() {
        assertEquals(
            expected = 1.pt,
            actual = 1.pt.inUnit(PureUnit.Pt)
        )

        assertEqualsWithTolerance(
            expected = 0.013888.inch,
            actual = 1.pt.inUnit(PureUnit.Inch)
        )

        assertEqualsWithTolerance(
            expected = 0.352778.mm,
            actual = 1.pt.inUnit(PureUnit.Mm)
        )

        assertEqualsWithTolerance(
            expected = 1.333333.px,
            actual = 1.pt.inUnit(PureUnit.Px)
        )

        assertEqualsWithTolerance(
            expected = 43.550416.mm,
            actual = 123.45.pt.inUnit(PureUnit.Mm)
        )
    }

    @Test
    fun testPxInUnit() {
        assertEquals(
            expected = 1.px,
            actual = 1.px.inUnit(PureUnit.Px)
        )

        assertEqualsWithTolerance(
            expected = 0.010417.inch,
            actual = 1.px.inUnit(PureUnit.Inch)
        )

        assertEqualsWithTolerance(
            expected = 0.264583.mm,
            actual = 1.px.inUnit(PureUnit.Mm)
        )

        assertEqualsWithTolerance(
            expected = 0.75.pt,
            actual = 1.px.inUnit(PureUnit.Pt)
        )

        assertEqualsWithTolerance(
            expected = 32.662812.mm,
            actual = 123.45.px.inUnit(PureUnit.Mm)
        )
    }

    @Test
    fun testToDimensionString() {
        assertEquals("123.0mm", 123.0.mm.toDimensionString())
        assertEquals("72.0pt", 72.0.pt.toDimensionString())
        assertEquals("1.0in", 1.0.inch.toDimensionString())
        assertEquals("50.0%", 50.0.percent.toDimensionString())
    }

    @Test
    fun testParseInvalidFormat() {
        assertIs<IllegalArgumentException>(
            assertFails {
                PureDimension.parse("invalid")
            },
        )
    }
}
