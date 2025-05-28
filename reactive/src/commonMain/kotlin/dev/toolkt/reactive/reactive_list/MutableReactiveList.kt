package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.range.single
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

        val update = Change.Update.change(
            indexRange = IntRange.single(index),
            changedElements = listOf(newValue),
        )

        changeEmitter.emit(
            Change.single(
                update,
            ),
        )

        update.applyTo(
            mutableList = mutableContent,
        )
    }
}
