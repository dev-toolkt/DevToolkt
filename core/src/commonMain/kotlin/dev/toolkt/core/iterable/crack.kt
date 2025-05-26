package dev.toolkt.core.iterable

typealias Crack<T> = (T) -> Pair<T, T>

/**
 * Crack the list at the [index] and apply the [crack] function to the separator.
 *
 * @param index The index to crack at
 * @param crack The function to apply to the separator
 *
 * @return The list split into two parts, with the separator cracked
 */
fun <T> List<T>.crackAt(
    index: Int,
    crack: Crack<T>,
): Split2<T> {
    val (leadingElements, separator, trailingElements) = separateAt(index = index)

    val (separatorFront, separatorBack) = crack(separator)

    return Split2(
        leadingElements = leadingElements + listOf(separatorFront),
        trailingElements = listOf(separatorBack) + trailingElements,
    )
}

/**
 * Crack the list at the [firstIndex] and [secondIndex] and apply the [crackFirst] and [crackSecond] functions to the separators.
 *
 * @param firstIndex The first index to crack at
 * @param crackFirst The function to apply to the first separator
 * @param secondIndex The second index to crack at
 * @param crackSecond The function to apply to the second separator
 *
 * @return The list split into three parts, with the separators cracked
 */
fun <T> List<T>.crackAt(
    firstIndex: Int,
    crackFirst: Crack<T>,
    secondIndex: Int,
    crackSecond: Crack<T>,
): Split3<T> {
    val (leadingElements, firstSeparator, innerElements, secondSeparator, trailingElements) = separateAt(
        firstIndex = firstIndex,
        secondIndex = secondIndex,
    )

    val (firstSeparatorFront, firstSeparatorBack) = crackFirst(firstSeparator)
    val (secondSeparatorFront, secondSeparatorBack) = crackSecond(secondSeparator)

    return Split3(
        leadingElements = leadingElements + listOf(firstSeparatorFront),
        innerElements = listOf(firstSeparatorBack) + innerElements + listOf(secondSeparatorFront),
        trailingElements = listOf(secondSeparatorBack) + trailingElements,
    )
}

/**
 * Assuming this list forms a cyclic structure, crack the list at the [firstIndex]
 * and [secondIndex] and apply the [crackFirst] and [crackSecond] functions to
 * the separators.
 *
 * @return The list split into two parts, with the first part containing the
 * elements between the first crack point and the second crack point, and the
 * second part containing the elements between the second crack point and the
 * first crack point.
 */
fun <T> List<T>.crackAtCyclic(
    firstIndex: Int,
    crackFirst: Crack<T>,
    secondIndex: Int,
    crackSecond: Crack<T>,
): Split2<T> {
    val (leadingElements, innerElements, trailingElements) = crackAt(
        firstIndex = firstIndex,
        crackFirst = crackFirst,
        secondIndex = secondIndex,
        crackSecond = crackSecond,
    )

    return Split2(
        leadingElements = innerElements,
        trailingElements = trailingElements + leadingElements,
    )
}
