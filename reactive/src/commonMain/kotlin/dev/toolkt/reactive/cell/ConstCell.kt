package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream

class ConstCell<V>(
    val constValue: V,
) : Cell<V>() {
    override val currentValue: V
        get() = constValue

    override val newValues: NeverEventStream
        get() = NeverEventStream

    override val changes: EventStream<Change<V>>
        get() = NeverEventStream

    override fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr> = ConstCell(
        constValue = transform(constValue),
    )

    override fun <T : Any> form(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): Pair<T, Subscription> {
        val target = create(constValue)

        return Pair(target, Subscription.Noop)
    }

    override fun <T : Any> bind(
        target: T,
        update: (T, V) -> Unit,
    ): Subscription {
        update(target, constValue)

        return Subscription.Noop
    }
}
