package dev.toolkt.reactive.vertices.reactive_list

import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.core.range.single

class MutableReactiveListVertex<E>(
    initialElements: List<E>,
) : ReactiveListVertex<E>(
    initialElements = initialElements,
) {
    override val kind: String = "MutableL"

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    fun set(
        index: Int,
        element: E,
    ) {
        this.update(
            change = ReactiveList.Change.Update(
                indexRange = IntRange.single(index),
                updatedElements = listOf(element),
            ).toChange(),
        )
    }
}
