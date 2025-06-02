package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.removeRange
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream

class MutableReactiveList<E>(
    initialContent: List<E>,
) : ActiveReactiveList<E>() {
    private val changeEmitter = EventEmitter<Change<E>>()

    private val mutableContent = initialContent.toMutableList()

    override val changes: EventStream<Change<E>>
        get() = changeEmitter

    override val currentElements: List<E>
        get() = mutableContent.toList()

    fun set(
        index: Int,
        newValue: E,
    ) {
        if (index !in mutableContent.indices) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.set(
            index = index,
            newValue = newValue,
        )

        changeEmitter.emit(
            Change.single(
                update = update,
            ),
        )

        mutableContent[index] = newValue
    }

    fun addAll(
        index: Int,
        elements: List<E>,
    ) {
        if (index !in 0..mutableContent.size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.insert(
            index = index,
            newElements = elements,
        )

        changeEmitter.emit(
            Change.single(
                update = update,
            ),
        )

        mutableContent.addAll(
            index = index,
            elements = elements,
        )
    }

    fun removeAt(index: Int) {
        if (index !in mutableContent.indices) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size ${mutableContent.size}.")
        }

        val update = Change.Update.remove(
            index = index,
        )

        changeEmitter.emit(
            Change.single(
                update = update,
            ),
        )

        mutableContent.removeAt(index = index)
    }
}
