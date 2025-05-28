package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventStream

class MapReactiveList<E, Er>(
    source: ReactiveList<E>,
    transform: (E) -> Er,
) : DependentReactiveList<Er>(
    initialContent = source.currentElements.map(transform),
) {
    override val changes: EventStream<Change<Er>> = source.changes.map {
        it.map(transform)
    }

    init {
        init()
    }
}
