package dev.toolkt.reactive.vertices.reactive_list

import dev.toolkt.reactive.HybridSubscription
import dev.toolkt.reactive.strengthen
import dev.toolkt.reactive.weaken

/**
 * A vertex of a dynamic list that depends on another vertices (dynamic lists,
 * cells and/or streams), installing a hybrid (weak/strong) subscription to
 * the dependencies. Analogical to [dev.toolkt.reactive.vertices.cell.DependentCellVertex].
 */
abstract class DependentReactiveListVertex<E>(
    initialElements: List<E>,
) : ReactiveListVertex<E>(
    initialElements = initialElements,
) {
    lateinit var subscription: HybridSubscription

    final override fun onResumed() {
        subscription.strengthen()
    }

    final override fun onPaused() {
        subscription.weaken()
    }

    protected fun init() {
        subscription = buildHybridSubscription()
    }

    protected abstract fun buildHybridSubscription(): HybridSubscription
}
