package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.vertices.Vertex
import dev.toolkt.reactive.vertices.event_stream.EmitterVertex

class EventEmitter<E> : ActiveEventStream<E>() {
    override val vertex: Vertex<E> = EmitterVertex()

    fun emit(event: E) {
        vertex.notify(event)
    }
}
