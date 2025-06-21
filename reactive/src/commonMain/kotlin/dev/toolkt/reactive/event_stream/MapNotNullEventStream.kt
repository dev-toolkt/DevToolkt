package dev.toolkt.reactive.event_stream

class MapNotNullEventStream<E, Er : Any>(
    source: EventStream<E>,
    private val transform: (E) -> Er?,
) : TransformingEventStream<E, Er>(
    source = source,
) {
    override fun transformEvent(event: E) {
        val transformedEvent = transform(event) ?: return
        notify(transformedEvent)
    }
}
