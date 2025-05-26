package dev.toolkt.core.iterable

/**
 * Transforms a list while carrying state between transformations.
 *
 * @param initialCarry The initial carry value.
 * @param transform A function that takes the current carry and an element of the list,
 * and returns a pair of the transformed element and the updated carry.
 * @return A pair of the transformed list and the final carry value.
 */
fun <T, R, C> Iterable<T>.mapCarrying(
    initialCarry: C,
    transform: (C, T) -> Pair<R, C>,
): Pair<List<R>, C> {
    val result = mutableListOf<R>()
    var carry = initialCarry

    for (item in this) {
        val (transformedItem, newCarry) = transform(carry, item)
        result.add(transformedItem)
        carry = newCarry
    }

    return Pair(result, carry)
}

/**
 * Transforms a sequence while carrying state between transformations.
 *
 * @param initialCarry The initial carry value.
 * @param transform A function that takes the current carry and an element of the list,
 * and returns a pair of the transformed element and the updated carry.
 * @return A transformed sequence.
 */

fun <T, R, C> Sequence<T>.mapCarrying(
    initialCarry: C,
    transform: (C, T) -> Pair<R, C>,
): Sequence<R> = sequence {
    var carry = initialCarry

    for (item in this@mapCarrying) {
        val (transformedItem, newCarry) = transform(carry, item)
        yield(transformedItem)
        carry = newCarry
    }
}
