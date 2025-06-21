package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

class SingleNotNullReactiveList<E : Any>(
    element: Cell<E?>,
) : CachingReactiveList<E>(
    initialContent = listOfNotNull(element.currentValue),
) {
    override val changes: EventStream<Change<E>> = element.newValues.mapNotNull { newValue ->
        val isEmpty = currentElements.isEmpty()

        val update = when (newValue) {
            null -> when {
                isEmpty -> null

                else -> Change.Update.remove(
                    index = 0,
                )
            }

            else -> when {
                isEmpty -> Change.Update.insert(
                    index = 0,
                    newElement = newValue,
                )

                else -> Change.Update.set(
                    index = 0,
                    newValue = newValue,
                )
            }
        } ?: return@mapNotNull null

        Change.single(update = update)
    }

    init {
        init()
    }
}
