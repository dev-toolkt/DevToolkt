package dev.toolkt.core.iterable

data class Separation2<T>(
    val leadingElements: List<T>,
    val separator: T,
    val trailingElements: List<T>,
)

fun <T> List<T>.separateAt(
    index: Int,
): Separation2<T> {
    if (index !in indices) {
        throw IndexOutOfBoundsException("index: $index, size: $size")
    }

    val leadingElements = take(index)
    val separator = this[index]
    val trailingElements = drop(index + 1)

    return Separation2(
        leadingElements = leadingElements,
        separator = separator,
        trailingElements = trailingElements,
    )
}

data class Separation3<T>(
    val leadingElements: List<T>,
    val firstSeparator: T,
    val innerElements: List<T>,
    val secondSeparator: T,
    val trailingElements: List<T>,
)

fun <T> List<T>.separateAt(
    firstIndex: Int,
    secondIndex: Int,
): Separation3<T> {
    if (firstIndex !in indices) {
        throw IndexOutOfBoundsException("firstIndex: $firstIndex, size: $size")
    }

    if (secondIndex !in indices) {
        throw IndexOutOfBoundsException("secondIndex: $secondIndex, size: $size")
    }

    if (firstIndex >= secondIndex) {
        throw IllegalArgumentException("firstIndex: $firstIndex > secondIndex: $secondIndex")
    }

    val leadingElements = take(firstIndex)
    val firstSeparator = this[firstIndex]
    val innerElements = subList(firstIndex + 1, secondIndex)
    val secondSeparator = this[secondIndex]
    val trailingElements = drop(secondIndex + 1)

    return Separation3(
        leadingElements = leadingElements,
        firstSeparator = firstSeparator,
        innerElements = innerElements,
        secondSeparator = secondSeparator,
        trailingElements = trailingElements,
    )
}
