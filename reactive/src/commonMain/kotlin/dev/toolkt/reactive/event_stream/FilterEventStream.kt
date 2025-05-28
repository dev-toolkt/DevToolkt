package dev.toolkt.reactive.event_stream

class FilterEventStream<E>(
    source: EventStream<E>,
    private val predicate: (E) -> Boolean,
) : TransformingEventStream<E, E>(
    source = source,
) {
    override fun transformEvent(event: E) {
        if (predicate(event)) {
            notify(event)
        }
    }
}
