package dev.toolkt.core.iterable

data class Partitioned<T>(
    val previousElements: List<T>,
    val innerElement: T,
    val nextElements: List<T>,
)

fun <T> List<T>.partitionAtCenter(): Partitioned<T>? = partitionAt(
    index = size / 2,
)

fun <T> List<T>.partitionAt(index: Int): Partitioned<T>? {
    if (isEmpty() || index < 0 || index >= size) return null

    val medianValue = this[index]

    return Partitioned(
        previousElements = subList(0, index),
        innerElement = medianValue,
        nextElements = subList(index + 1, size),
    )
}
