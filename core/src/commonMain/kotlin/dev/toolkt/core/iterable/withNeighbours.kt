package dev.toolkt.core.iterable

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
data class WithNeighbours<L, M, R>(
    val prevElement: L,
    val element: M,
    val nextElement: R,
) where M : L, M : R

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
fun <L, M, R> Sequence<M>.withNeighbours(
    buildOuterLeft: (M) -> L,
    buildOuterRight: (M) -> R,
): Sequence<WithNeighbours<L, M, R>> where M : L, M : R = sequence {
    val iterator = iterator()
    if (!iterator.hasNext()) return@sequence

    var current = iterator.next()
    var prev: L = buildOuterLeft(current)

    while (iterator.hasNext()) {
        val next = iterator.next()
        yield(WithNeighbours(prev, current, next))
        prev = current
        current = next
    }

    yield(WithNeighbours(prev, current, buildOuterRight(current)))
}

fun <M : Any> Sequence<M>.withNeighboursOrNull(): Sequence<WithNeighbours<M?, M, M?>> = this.withNeighbours(
    outerLeft = null,
    outerRight = null,
)

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
fun <L, M, R> Sequence<M>.withNeighbours(
    outerLeft: L,
    outerRight: R,
): Sequence<WithNeighbours<L, M, R>> where M : L, M : R = this.withNeighbours(
    buildOuterLeft = { outerLeft },
    buildOuterRight = { outerRight },
)

fun <M> Sequence<M>.withNeighboursSaturated(): Sequence<WithNeighbours<M, M, M>> = this.withNeighbours(
    buildOuterLeft = { it },
    buildOuterRight = { it },
)

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
fun <L, M, R> Iterable<M>.withNeighbours(
    outerLeft: L,
    outerRight: R,
): List<WithNeighbours<L, M, R>> where M : L, M : R = this.asSequence().withNeighbours(
    outerLeft = outerLeft,
    outerRight = outerRight,
).toList()
