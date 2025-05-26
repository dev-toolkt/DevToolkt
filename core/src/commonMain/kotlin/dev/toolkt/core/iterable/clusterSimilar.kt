package dev.toolkt.core.iterable

fun <T> Iterable<T>.clusterSimilarConsecutive(
    areSimilar: (prev: T, next: T) -> Boolean,
): List<List<T>> = this.clusterSimilar { group, element ->
    areSimilar(group.last(), element)
}

fun <T> Iterable<T>.clusterSimilar(
    fitsGroup: (group: List<T>, element: T) -> Boolean,
): List<List<T>> {
    val groups = mutableListOf<MutableList<T>>()
    val iterator = this.iterator()

    if (!iterator.hasNext()) return groups

    var currentGroup = mutableListOf(iterator.next()).also(groups::add)

    while (iterator.hasNext()) {
        val next = iterator.next()
        if (fitsGroup(currentGroup, next)) {
            currentGroup.add(next)
        } else {
            currentGroup = mutableListOf(next)
            groups.add(currentGroup)
        }
    }

    return groups
}
