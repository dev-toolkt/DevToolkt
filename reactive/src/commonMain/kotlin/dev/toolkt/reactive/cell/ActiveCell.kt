package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventStream

abstract class ActiveCell<out V> : Cell<V>() {
    final override val changes: EventStream<Change<V>>
        get() = newValues.map { newValue ->
            Change(
                oldValue = currentValue,
                newValue = newValue,
            )
        }

    final override fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr> = MapCell(
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
