package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.vertices.Vertex

class EmitterVertex<E> : Vertex<E>() {
    override val kind: String = "Emitter"

    override fun onResumed() {
    }

    override fun onPaused() {
    }
}
