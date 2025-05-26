package dev.toolkt.core.iterable

data class WithNext<M, R>(
    val element: M,
    val nextElement: R,
)

fun <M, R> Iterable<M>.withNext(
    outerRight: R,
): List<WithNext<M, R>> where M : R {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithNext<M, R>>()
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(WithNext(current, next))
        current = next
    }

    result.add(WithNext(current, outerRight))
    return result
}


fun <M, R> Iterable<M>.withNextBy(
    outerRight: R,
    selector: (M) -> R,
): List<WithNext<M, R>> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithNext<M, R>>()
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(WithNext(current, selector(next)))
        current = next
    }

    result.add(WithNext(current, outerRight))
    return result
}

fun <T : Any> List<T>.withNextCyclic(): List<WithNext<T, T>> = when {
    isEmpty() -> emptyList()
    else -> withNext(first())
}

fun <T : Any> Iterable<T>.withNextOrNull(): List<WithNext<T, T?>> = withNext(null)
