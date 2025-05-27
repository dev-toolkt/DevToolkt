package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.vertices.event_stream.EmitterVertex

class EventEmitter<E> : ActiveEventStream<E>() {
    override val vertex: EmitterVertex<E> = EmitterVertex()

    fun emit(event: E) {
        vertex.emit(event)
    }
}
