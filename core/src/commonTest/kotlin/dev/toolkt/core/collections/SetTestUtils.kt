package dev.toolkt.core.collections

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <E> Set<E>.verifyContent(
    elements: Collection<E>,
    controlElements: Collection<E>,
) {
    assertEquals(
        expected = elements.size,
        actual = size,
        message = "Actual size does not match expected size: expected ${elements.size}, got $size",
    )

    val expectedListElements = elements.sortedBy { it.hashCode() }
    val listElements = toList().sortedBy { it.hashCode() }

    assertEquals(
        expected = expectedListElements,
        actual = listElements,
        message = "Actual elements do not match expected elements: expected $expectedListElements, got $listElements",
    )

    assertTrue(
        actual = controlElements.none { listElements.contains(it) },
    )

    assertTrue(
        actual = controlElements.none { contains(it) },
    )
}
