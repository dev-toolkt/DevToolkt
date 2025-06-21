package dev.toolkt.core.pairs

fun <T : Comparable<T>> Pair<T, T>.sorted(): Pair<T, T> = when {
    first <= second -> this
    else -> Pair(second, first)
}
