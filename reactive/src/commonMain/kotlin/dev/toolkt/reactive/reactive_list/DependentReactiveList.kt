package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.vertices.reactive_list.DependentReactiveListVertex

class DependentReactiveList<E>(
    override val vertex: DependentReactiveListVertex<E>,
) : ActiveReactiveList<E>()
