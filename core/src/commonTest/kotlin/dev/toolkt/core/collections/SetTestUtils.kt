package dev.toolkt.core.collections

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <E> Set<E>.verifyContent(
    elements: List<E>,
    controlElements: Set<E>,
) {
    assertEquals(
        expected = elements.size,
        actual = size,
        message = "Actual size does not match expected size: expected ${elements.size}, got $size",
    )

    // Actual list elements in the iteration order
    val actualElements = toList()

    assertEquals(
        expected = elements,
        actual = actualElements,
        message = "Actual elements do not match expected elements: expected $elements, got $actualElements",
    )

    assertTrue(
        actual = controlElements.none { actualElements.contains(it) },
    )

    assertTrue(
        actual = controlElements.none { contains(it) },
    )
}
