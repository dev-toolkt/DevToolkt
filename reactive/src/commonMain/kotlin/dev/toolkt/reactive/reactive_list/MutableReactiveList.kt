package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.vertices.reactive_list.MutableReactiveListVertex

class MutableReactiveList<E>(
    initialElements: List<E>,
) : ActiveReactiveList<E>() {
    override val vertex = MutableReactiveListVertex(
        initialElements = initialElements,
    )

    fun set(
        index: Int,
        element: E,
    ) {
        vertex.set(
            index = index,
            element = element,
        )
    }
}
