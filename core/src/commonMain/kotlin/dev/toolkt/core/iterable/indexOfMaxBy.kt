package dev.toolkt.core.iterable

fun <T, R : Comparable<R>> List<T>.indexOfMaxBy(
    fromIndex: Int = 0,
    toIndex: Int = size,
    selector: (T) -> R,
): Int {
    val (index, _) = withIndex().toList().subList(
        fromIndex = fromIndex,
        toIndex = toIndex,
    ).maxBy { (_, v) -> selector(v) }

    return index
}
