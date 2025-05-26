package dev.toolkt.geometry

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <T : GeometricObject> assertEqualsWithGeometricTolerance(
    expected: T,
    actual: T,
    tolerance: GeometricObject.GeometricTolerance = GeometricObject.GeometricTolerance.default,
    message: String = "Expected $expected, but got $actual (tolerance: $tolerance)",
) {
    assertTrue(
        actual = expected.equalsWithGeometricTolerance(actual, tolerance = tolerance),
        message = message,
    )
}

fun <T : GeometricObject> assertEqualsWithGeometricTolerance(
    expected: List<T>,
    actual: List<T>,
    tolerance: GeometricObject.GeometricTolerance = GeometricObject.GeometricTolerance.default,
) {
    assertEquals(
        expected = expected.size,
        actual = actual.size,
        message = "Expected list size ${expected.size}, but got ${actual.size}",
    )

    for (i in expected.indices) {
        assertEqualsWithGeometricTolerance(
            expected = expected[i],
            actual = actual[i],
            tolerance = tolerance,
            message = "At index $i: expected ${expected[i]}, but got ${actual[i]} (tolerance: $tolerance)",
        )
    }
}
