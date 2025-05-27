package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.vertices.cell.CellVertex
import dev.toolkt.reactive.vertices.cell.MapCellVertex

abstract class ActiveCell<V> : Cell<V>() {
    final override val currentValue: V
        get() = vertex.currentValue

    final override val changes: EventStream<Change<V>>
        get() = DependentEventStream(
            vertex = vertex,
        )

    final override fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr> = DependentCell(
        vertex = MapCellVertex(
            source = this.vertex,
            transform = transform,
        ),
    )

    final override val newValues: EventStream<V>
        get() = changes.map { it.newValue }

    final override fun <T : Any> form(
        create: (V) -> T,
        update: (T, V) -> Unit
    ): T {
        val target = create(currentValue)

        newValues.pipe(
            target = target,
        ) { newValue ->
            update(target, newValue)
        }

        return target
    }

    override fun <T : Any> bind(
        target: T,
        update: (T, V) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    internal abstract val vertex: CellVertex<V>
}
