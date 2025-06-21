package dev.toolkt.core.iterator

class PeekingIterator<T : Any>(
    private val iterator: Iterator<T>,
) : Iterator<T> {
    private var peekedElement: T? = null

    fun peekOrNull(): T? = when (val currentPeekedElement = peekedElement) {
        null -> {
            val freshlyPeekedElement = iterator.nextOrNull()
            peekedElement = freshlyPeekedElement
            freshlyPeekedElement
        }

        else -> currentPeekedElement
    }

    fun peek(): T = peekOrNull() ?: throw NoSuchElementException()

    override fun next(): T = when (val currentPeekedElement = peekedElement) {
        null -> iterator.next()

        else -> {
            peekedElement = null
            currentPeekedElement
        }
    }

    override fun hasNext(): Boolean = when (peekedElement) {
        null -> iterator.hasNext()
        else -> true
    }
}

fun <T : Any> Iterator<T>.peeking(): PeekingIterator<T> = PeekingIterator(
    iterator = this,
)
