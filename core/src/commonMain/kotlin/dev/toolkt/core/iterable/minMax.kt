package dev.toolkt.core.iterable

inline fun <T, R : Comparable<R>> Sequence<T>.minByWithSelecteeOrNull(
    crossinline selector: (T) -> R,
): Pair<T, R>? = map { it to selector(it) }.minByOrNull { (_, result) -> result }

