package dev.toolkt.reactive

interface Subscription {
    object Noop : Subscription {
        override fun cancel() {
        }
    }

    fun cancel()
}
