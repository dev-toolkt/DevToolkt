package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.vertices.ManagedVertex

class EmitterVertex<E> : ManagedVertex<E>() {
    override val kind: String = "Emitter"

    fun emit(event: E) {
        notify(event)
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }
}
