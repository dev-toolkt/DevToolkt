package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.reactive.vertices.Vertex
import dev.toolkt.reactive.vertices.event_stream.FilterEventStreamVertex
import dev.toolkt.reactive.vertices.event_stream.MapEventStreamVertex

private val finalizationRegistry = PlatformFinalizationRegistry()

abstract class ActiveEventStream<E>() : EventStream<E>() {
    final override fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> = DependentEventStream(
        vertex = MapEventStreamVertex(
            source = this.vertex,
            transform = transform,
        ),
    )

    final override fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> = DependentEventStream(
        vertex = FilterEventStreamVertex(
            source = this.vertex,
            predicate = predicate,
        ),
    )

    final override fun <T : Any> pipe(
        target: T,
        consume: (E) -> Unit,
    ) {
        val subscription = vertex.subscribeStrong(
            listener = object : Listener<E> {
                override fun handle(event: E) {
                    consume(event)
                }
            },
        )

        finalizationRegistry.register(
            target = target,
        ) {
            subscription.cancel()
        }
    }

    internal abstract val vertex: Vertex<E>
}
