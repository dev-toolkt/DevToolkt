package dev.toolkt.reactive

import dev.toolkt.reactive.vertices.AbstractVertex.ListenerStrength

interface Subscription {
    object Noop : Subscription {
        override fun cancel() {
        }
    }

    fun cancel()
}

interface HybridSubscription : Subscription {
    object Noop : HybridSubscription {
        override fun cancel() {
        }

        override fun updateStrength(newStrength: ListenerStrength) {
        }
    }

    fun updateStrength(
        newStrength: ListenerStrength,
    )
}

fun HybridSubscription.strengthen() {
    updateStrength(
        newStrength = ListenerStrength.Strong,
    )
}

fun HybridSubscription.weaken() {
    updateStrength(
        newStrength = ListenerStrength.Weak,
    )
}
