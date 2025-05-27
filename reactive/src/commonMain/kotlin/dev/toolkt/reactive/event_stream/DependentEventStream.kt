package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.vertices.ManagedVertex

internal class DependentEventStream<E>(
    override val vertex: ManagedVertex<E>,
) : ActiveEventStream<E>()
