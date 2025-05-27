package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.vertices.ManagedVertex

internal class MapEventStreamVertex<E, Er>(
    source: ManagedVertex<E>,
    private val transform: (E) -> Er,
) : TransformingEventStreamVertex<E, Er>(
    source = source,
) {
    override val kind: String = "MapE"

    override fun handleSourceEvent(event: E) {
        notify(transform(event))
    }
}
