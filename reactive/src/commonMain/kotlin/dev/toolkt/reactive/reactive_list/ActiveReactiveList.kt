package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.vertices.reactive_list.ReactiveListVertex
import dev.toolkt.reactive.vertices.reactive_list.MapReactiveListVertex

abstract class ActiveReactiveList<E>() : ReactiveList<E>() {
    final override val currentElements: List<E>
        get() = vertex.currentElements

    final override val changes: EventStream<Change<E>>
        get() = DependentEventStream(vertex = vertex)

    final override fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveList<Er> = DependentReactiveList(
        vertex = MapReactiveListVertex(
            source = this.vertex,
            transform = transform,
        ),
    )

    final override fun <T : Any> pipe(
        target: T,
        mutableList: MutableList<in E>,
    ) {
        copyNow(mutableList = mutableList)

        changes.pipe(
            target = target,
        ) { change ->
            change.applyTo(mutableList = mutableList)
        }
    }

    internal abstract val vertex: ReactiveListVertex<E>
}
