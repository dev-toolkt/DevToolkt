package dev.toolkt.dom.pure.collections

abstract class BasicListIterator<out E>(
    protected var index: Int,
) : ListIterator<E> {
    override fun next(): E {
        if (!hasNext()) {
            throw NoSuchElementException("No next element in the list")
        }

        return list[index++]
    }

    override fun hasNext(): Boolean {
        return index < list.size
    }

    override fun hasPrevious(): Boolean {
        return index > 0
    }

    override fun previous(): E {
        if (!hasPrevious()) {
            throw NoSuchElementException("No previous element in the list")
        }

        return list[--index]
    }

    override fun nextIndex(): Int = if (hasNext()) index else list.size

    override fun previousIndex(): Int = if (hasPrevious()) index - 1 else -1

    abstract val list: List<E>
}
