package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

internal class ConstCell<V>(
    val constValue: V,
) : Cell<V>() {
    override val currentValue: V
        get() = constValue

    override val newValues: EventStream<V> = EventStream.Companion.Never

    override val changes: EventStream<Nothing> = EventStream.Companion.Never

    override fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr> = ConstCell(
        constValue = transform(constValue),
    )

    override fun <T : Any> form(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): T = create(constValue)

    override fun <T : Any> bind(target: T, update: (T, V) -> Unit) {
        update(target, constValue)
    }
}
