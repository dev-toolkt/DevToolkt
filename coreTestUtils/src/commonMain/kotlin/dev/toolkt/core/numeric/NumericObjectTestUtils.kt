package dev.toolkt.core.numeric

import kotlin.jvm.JvmName
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun assertEqualsWithTolerance(
    expected: Double,
    actual: Double,
    tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
) {
    assertTrue(
        actual = expected.equalsWithTolerance(actual, tolerance = tolerance),
        message = "Expected $expected, but got $actual (tolerance: $tolerance)",
    )
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: T,
    actual: T,
    tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
    message: String = "Expected $expected, but got $actual (tolerance: $tolerance)",
) {
    assertTrue(
        actual = expected.equalsWithTolerance(actual, tolerance = tolerance),
        message = message,
    )
}

@JvmName("assertEqualsWithToleranceListNumericObject")
fun <T : NumericObject> assertEqualsWithTolerance(
    expected: List<T>,
    actual: List<T>,
    tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
) {
    assertEquals(
        expected = expected.size,
        actual = actual.size,
        message = "Expected list size ${expected.size}, but got ${actual.size}",
    )

    for (i in expected.indices) {
        assertEqualsWithTolerance(
            expected = expected[i],
            actual = actual[i],
            tolerance = tolerance,
            message = "At index $i: expected ${expected[i]}, but got ${actual[i]} (tolerance: $tolerance)",
        )
    }
}

@JvmName("assertEqualsWithToleranceListDouble")
fun assertEqualsWithTolerance(
    expected: List<Double>,
    actual: List<Double>,
    tolerance: NumericObject.Tolerance = NumericObject.Tolerance.Default,
) {
    assertEquals(
        expected = expected.size,
        actual = actual.size,
        message = "Expected list size ${expected.size}, but got ${actual.size}",
    )

    for (i in expected.indices) {
        assertEqualsWithTolerance(
            expected = expected[i],
            actual = actual[i],
            tolerance = tolerance,
        )
    }
}
