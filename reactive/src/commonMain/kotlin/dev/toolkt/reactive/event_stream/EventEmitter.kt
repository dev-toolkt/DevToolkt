package dev.toolkt.reactive.event_stream

class EventEmitter<E> : ManagedEventStream<E>() {
    fun emit(event: E) {
        notify(event)
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }
}
