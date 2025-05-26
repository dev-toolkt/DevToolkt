package dev.toolkt.core.iterable

/**
 * Generates all unique pairs of elements from the set (meaning that the order
 * does not matter and no element is paired with itself).
 */
fun <T> Set<T>.allUniquePairs(): Sequence<Pair<T, T>> {
    val list = toList()

    return sequence {
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                yield(Pair(list[i], list[j]))
            }
        }
    }
}
