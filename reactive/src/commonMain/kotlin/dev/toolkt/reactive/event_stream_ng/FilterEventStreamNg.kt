package dev.toolkt.reactive.event_stream_ng

class FilterEventStreamNg<E>(
    source: EventStreamNg<E>,
    private val predicate: (E) -> Boolean,
) : TransformingEventStreamNg<E, E>(
    source = source,
) {
    override fun transformEvent(event: E) {
        if (predicate(event)) {
            notify(event)
        }
    }
}
