package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.vertices.ManagedVertex

internal class FilterEventStreamVertex<E>(
    source: ManagedVertex<E>,
    private val predicate: (E) -> Boolean,
) : TransformingEventStreamVertex<E, E>(
    source = source,
) {
    override val kind: String = "Filter"

    override fun handleSourceEvent(event: E) {
        if (predicate(event)) {
            notify(event)
        }
    }
}
