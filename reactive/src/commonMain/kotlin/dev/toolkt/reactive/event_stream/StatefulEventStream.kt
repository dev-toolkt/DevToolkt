package dev.toolkt.reactive.event_stream

abstract class StatefulEventStream<out E>() : ManagedEventStream<E>() {
    final override fun onResumed() {
    }

    final override fun onPaused() {
    }
}
