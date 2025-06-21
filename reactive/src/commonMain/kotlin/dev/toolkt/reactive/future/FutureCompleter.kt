package dev.toolkt.reactive.future

class FutureCompleter<V> : ManagedFuture<V>() {
    fun complete(
        result: V,
    ) {
        completeInternally(result = result)
    }
}
