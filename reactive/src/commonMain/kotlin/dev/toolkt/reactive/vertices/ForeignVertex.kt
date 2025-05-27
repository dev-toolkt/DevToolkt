package dev.toolkt.reactive.vertices

abstract class ForeignVertex<T> : Vertex<T>() {
    final override fun onSubscribed() {
    }

    final override fun onUnsubscribed() {
    }
}
