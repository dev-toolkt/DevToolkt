package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.LoopedEventStream

class LoopedReactiveList<E>() : ActiveReactiveList<E>() {
    private var loopedReactiveList: ReactiveList<E>? = null

    private val changesLooped = LoopedEventStream<Change<E>>()

    override val changes: EventStream<Change<E>>
        get() = changesLooped

    override val currentElements: List<E>
        get() = this.loopedReactiveList?.currentElements ?: emptyList()

    fun loop(
        reactiveList: ReactiveList<E>,
    ) {
        if (loopedReactiveList != null) {
            throw IllegalStateException("The reactive list is already looped")
        }

        loopedReactiveList = reactiveList

        changesLooped.loop(
            eventStream = reactiveList.changes,
            initialEvent = Change.fill(
                elements = reactiveList.currentElements,
            ),
        )
    }
}
