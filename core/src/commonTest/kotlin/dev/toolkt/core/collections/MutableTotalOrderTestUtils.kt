package dev.toolkt.core.collections

import dev.toolkt.core.order.OrderRelation
import kotlin.test.assertEquals

fun <E> MutableTotalOrder.Companion.of(
    vararg elements: E,
): Pair<MutableTotalOrder<E>, List<MutableTotalOrder.Handle<E>>> {

    val mutableTotalOrder = MutableTotalOrder<E>()

    val handles = elements.map { element ->
        mutableTotalOrder.addExtremal(
            relation = OrderRelation.Greater,
            element = element,
        )
    }

    return Pair(mutableTotalOrder, handles)
}


fun <E> MutableTotalOrder<E>.findHandle(
    element: E,
): MutableTotalOrder.Handle<E> = traverse().first { get(it) == element }

fun <E> MutableTotalOrder<E>.verifyOrder(
    vararg pairs: Pair<MutableTotalOrder.Handle<E>, E>,
) {
    verifyOrder(
        pairs = pairs.toList(),
    )
}

fun <E> MutableTotalOrder<E>.verifyOrder(
    pairs: List<Pair<MutableTotalOrder.Handle<E>, E>>,
) {
    val traversedHandles = traverse().toList()

    assertEquals(
        expected = pairs.size,
        actual = traversedHandles.size,
        message = "Number of traversed handles does not match expected size: expected ${pairs.size}, got ${traversedHandles.size}",
    )

    pairs.forEachIndexed { index, (handle, element) ->
        val traversedHandle = traversedHandles[index]

        assertEquals(
            expected = handle,
            actual = traversedHandle,
            message = "Traversed handle at index $index does not match expected handle: expected $handle, got $traversedHandle",
        )

        val gotElement = get(handle = handle)

        assertEquals(
            expected = element,
            actual = gotElement,
            message = "Got element at handle $handle does not match expected element: expected $element, got $gotElement",
        )

        val selectedHandle = select(index)

        assertEquals(
            expected = handle,
            actual = selectedHandle,
            message = "Selected handle at index $index does not match expected handle: expected $handle, got $selectedHandle",
        )

        val rankedIndex = rank(handle)

        assertEquals(
            expected = index,
            actual = rankedIndex,
            message = "Rank of handle at index $index does not match expected index: expected $index, got $rankedIndex",
        )
    }
}
