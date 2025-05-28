package dev.toolkt.reactive.event_stream_ng

class EventEmitterNg<E> : ManagedEventStreamNg<E>() {
    fun emit(event: E) {
        notify(event)
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }
}
