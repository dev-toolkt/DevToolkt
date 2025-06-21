package dev.toolkt.core.iterable

data class Uncons<T>(
    val firstElement: T,
    val trailingElement: List<T>,
)

fun <T> List<T>.uncons(): Uncons<T>? = firstOrNull()?.let { head ->
    Uncons(
        firstElement = head,
        trailingElement = drop(1),
    )
}

data class Uncons2<T>(
    val firstElement: T,
    val secondElement: T,
    val trailingElement: List<T>,
)


fun <T> List<T>.uncons2(): Uncons2<T>? = when {
    size < 2 -> null

    else -> Uncons2(
        firstElement = this[0],
        secondElement = this[1],
        trailingElement = drop(2),
    )
}

data class Untrail<T>(
    val leadingElements: List<T>,
    val lastElement: T,
)

fun <T> List<T>.untrail(): Untrail<T>? = lastOrNull()?.let { foot ->
    Untrail(
        leadingElements = dropLast(1),
        lastElement = foot,
    )
}
