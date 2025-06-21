package dev.toolkt.dom.pure.collections

interface DomList<out E> : BasicList<E> {
    override val size: Int

    override fun isEmpty(): Boolean {
        return !isNotEmpty()
    }

    fun isNotEmpty(): Boolean {
        return size > 0
    }

    override fun get(index: Int): E = getOrNull(index = index) ?: throw IndexOutOfBoundsException(
        "Index $index is out of bounds for size $size",
    )

    fun getOrNull(index: Int): E?

    val firstElement: E?
        get() = getOrNull(0)
}
