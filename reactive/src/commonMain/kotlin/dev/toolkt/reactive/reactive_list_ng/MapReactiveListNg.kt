package dev.toolkt.reactive.reactive_list_ng

import dev.toolkt.reactive.event_stream_ng.EventStreamNg

class MapReactiveListNg<E, Er>(
    source: ReactiveListNg<E>,
    transform: (E) -> Er,
) : DependentReactiveListNg<Er>(
    initialContent = source.currentElements.map(transform),
) {
    override val changes: EventStreamNg<Change<Er>> = source.changes.map {
        it.map(transform)
    }
}
