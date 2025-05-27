package dev.toolkt.reactive.vertices.reactive_list

import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.applyTo
import dev.toolkt.reactive.vertices.Vertex

abstract class ReactiveListVertex<E>(
    initialElements: List<E>,
) : Vertex<ReactiveList.Change<E>>() {
    private val mutableElements = initialElements.toMutableList()

    val currentElements: List<E>
        get() = mutableElements.toList()

    protected fun update(
        change: ReactiveList.Change<E>,
    ) {
        change.applyTo(
            mutableList = mutableElements,
        )

        notify(change)
    }
}
