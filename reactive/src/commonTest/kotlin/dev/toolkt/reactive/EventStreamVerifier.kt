package dev.toolkt.reactive

import dev.toolkt.reactive.event_stream.ActiveEventStream
import dev.toolkt.reactive.event_stream.EventStream

class EventStreamVerifier<E>(
    eventStream: EventStream<E>,
) {
    private val mutableReceivedEvents = mutableListOf<E>()

    init {
        eventStream.subscribe {
            mutableReceivedEvents.add(it)
        }
    }

    fun removeReceivedEvents(): List<E> {
        val receivedEvents = mutableReceivedEvents.toList()

        mutableReceivedEvents.clear()

        return receivedEvents
    }
}

private fun <E> EventStream<E>.subscribe(
    listener: RawListener<E>,
) {
    (this as? ActiveEventStream<E>)?.let {
        this.vertex.subscribeStrongRaw(listener = listener)
    }
}
