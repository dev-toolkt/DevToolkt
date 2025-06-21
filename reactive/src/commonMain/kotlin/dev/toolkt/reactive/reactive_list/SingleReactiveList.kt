package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class SingleReactiveList<E>(
    element: Cell<E>,
) : CachingReactiveList<E>(
    initialContent = listOf(element.currentValue),
) {
    override val changes: EventStream<Change<E>> = element.newValues.map { newValue ->
        Change.single(
            update = Change.Update.set(
                index = 0,
                newValue = newValue,
            ),
        ) ?: throw AssertionError("The change is not effective")
    }

    init {
        init()
    }
}
