package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.vertices.Vertex

internal class DependentEventStream<E>(
    override val vertex: Vertex<E>,
) : ActiveEventStream<E>()
