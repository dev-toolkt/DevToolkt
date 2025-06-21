package dev.toolkt.core.iterable

data class WithPrevious<L, M>(
    val prevElement: L,
    val element: M,
) where M : L

fun <L, M> Iterable<M>.withPrevious(
    outerLeft: L,
): List<WithPrevious<L, M>> where M : L {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithPrevious<L, M>>()
    var prev: L = outerLeft
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()

        result.add(
            WithPrevious(
                prevElement = prev,
                element = current,
            ),
        )

        prev = current
        current = next
    }

    result.add(
        WithPrevious(
            prevElement = prev,
            element = current,
        ),
    )

    return result
}

fun <L, M> Iterable<M>.withPreviousBy(
    outerLeft: L,
    selector: (M) -> L,
): List<WithPrevious<L, M>> where M : L {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithPrevious<L, M>>()
    var prev: L = outerLeft
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()

        result.add(
            WithPrevious(
                prevElement = prev,
                element = current,
            ),
        )

        prev = selector(current)
        current = next
    }

    result.add(
        WithPrevious(
            prevElement = prev,
            element = current,
        ),
    )

    return result
}

fun <T : Any> List<T>.withPreviousCyclic(): List<WithPrevious<T, T>> = when {
    isEmpty() -> emptyList()

    else -> withPrevious(
        outerLeft = last(),
    )
}

fun <T : Any> Iterable<T>.withPreviousOrNull(): List<WithPrevious<T?, T>> = withPrevious(
    outerLeft = null,
)
