package dev.toolkt.reactive.cell_ng

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream_ng.EventStreamNg

abstract class ActiveCellNg<out V> : CellNg<V>() {
    final override val changes: EventStreamNg<Change<V>>
        get() = newValues.map { newValue ->
            Change(
                oldValue = currentValue,
                newValue = newValue,
            )
        }

    final override fun <Vr> map(
        transform: (V) -> Vr,
    ): CellNg<Vr> = MapCellNg(
        source = this,
        transform = transform,
    )

    final override fun <T : Any> form(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): Pair<T, Subscription> {
        val target = create(currentValue)

        val subscription = newValues.pipe(
            target = target,
            forward = update,
        )

        return Pair(target, subscription)
    }

    final override fun <T : Any> bind(
        target: T,
        update: (T, V) -> Unit,
    ): Subscription {
        update(target, currentValue)

        val subscription = newValues.pipe(
            target = target,
            forward = update,
        )

        return subscription
    }
}
