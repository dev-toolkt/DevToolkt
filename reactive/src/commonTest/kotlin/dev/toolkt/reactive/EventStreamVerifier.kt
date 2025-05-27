package dev.toolkt.reactive

import dev.toolkt.reactive.event_stream.ActiveEventStream
import dev.toolkt.reactive.event_stream.EventStream

class EventStreamVerifier<E>(
    eventStream: EventStream<E>,
) {
    private val mutableReceivedEvents = mutableListOf<E>()

    init {
        eventStream.subscribe(
            listener = object : Listener<E> {
                override fun handle(event: E) {
                    mutableReceivedEvents.add(event)
                }
            },
        )
    }

    fun removeReceivedEvents(): List<E> {
        val receivedEvents = mutableReceivedEvents.toList()

        mutableReceivedEvents.clear()

        return receivedEvents
    }
}

private fun <E> EventStream<E>.subscribe(
    listener: Listener<E>,
) {
    (this as? ActiveEventStream<E>)?.let {
        this.vertex.subscribeStrong(
            listener = listener,
        )
    }
}
