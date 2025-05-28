package dev.toolkt.reactive.cell_ng

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream_ng.EventStreamNg
import dev.toolkt.reactive.event_stream_ng.NeverEventStreamNg

class ConstCellNg<V>(
    val constValue: V,
) : CellNg<V>() {
    override val currentValue: V
        get() = constValue

    override val newValues: NeverEventStreamNg
        get() = NeverEventStreamNg

    override val changes: EventStreamNg<Change<V>>
        get() = NeverEventStreamNg

    override fun <Vr> map(
        transform: (V) -> Vr,
    ): CellNg<Vr> = ConstCellNg(
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
