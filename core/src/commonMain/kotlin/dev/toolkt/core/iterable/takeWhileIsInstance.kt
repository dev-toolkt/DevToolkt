package dev.toolkt.core.iterable

inline fun <T, reified R : T> Iterable<T>.takeWhileIsInstance(): List<R> {
    val result = mutableListOf<R>()

    for (item in this) {
        if (item is R) {
            result.add(item)
        } else {
            break
        }
    }

    return result
}


inline fun <T, reified R : T> List<T>.takeWhileIsInstanceWithReminder(): Pair<List<R>, List<T>> {
    val result = mutableListOf<R>()

    var index = 0

    while (true) {
        val element = this.getOrNull(index)

        if (element is R) {
            result.add(element)
            ++index
        } else {
            break
        }
    }

    return Pair(result, drop(index))
}
