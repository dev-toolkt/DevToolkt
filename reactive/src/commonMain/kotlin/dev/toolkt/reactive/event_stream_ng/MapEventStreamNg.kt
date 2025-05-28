package dev.toolkt.reactive.event_stream_ng

class MapEventStreamNg<E, Er>(
    source: EventStreamNg<E>,
    private val transform: (E) -> Er,
) : TransformingEventStreamNg<E, Er>(
    source = source,
) {
    override fun transformEvent(event: E) {
        notify(transform(event))
    }
}
