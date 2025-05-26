package dev.toolkt.core.iterable

/**
 * Performs the given action on each element, optionally removing it
 *
 * @param action A function that processes an element of the collection and
 * returns true if the element should be removed.
 */
fun <E> MutableCollection<E>.forEachRemoving(action: (E) -> Boolean) {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        if (action(iterator.next())) {
            iterator.remove()
        }
    }
}
