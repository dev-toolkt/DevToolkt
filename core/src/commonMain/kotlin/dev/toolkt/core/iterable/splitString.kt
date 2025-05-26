package dev.toolkt.core.iterable

data class SplitString2(
    val leadingElements: String,
    val trailingElements: String,
) {
    val pair: Pair<String, String>
        get() = leadingElements to trailingElements
}

/**
 * Split the list before the [index].
 */
fun String.splitBefore(index: Int): SplitString2 = SplitString2(
    leadingElements = take(index),
    trailingElements = drop(index),
)
