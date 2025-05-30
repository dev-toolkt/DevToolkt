package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.LoopedCell

sealed class Cell<out V> {
    data class Change<out V>(
        val oldValue: V,
        val newValue: V,
    )

    companion object {
        fun <V, R> looped(
            placeholderValue: V,
            block: (Cell<V>) -> Pair<R, Cell<V>>,
        ): R {
            val loopedCell = LoopedCell(placeholderValue = placeholderValue)

            val (result, eventStream) = block(loopedCell)

            loopedCell.loop(eventStream)

            return result
        }

        fun <V> switch(
            nestedCell: Cell<Cell<V>>,
        ): Cell<V> = SwitchCell(
            nestedCell = nestedCell,
        )

        fun <V1, V2, Vr> map2(
            cell1: Cell<V1>,
            cell2: Cell<V2>,
            transform: (V1, V2) -> Cell<Vr>,
        ): Cell<Vr> = cell1.switchOf { value1 ->
            cell2.switchOf { value2 ->
                transform(value1, value2)
            }
        }

        fun <Vr1, Vr2> zip2(
            cell1: Cell<Vr1>,
            cell2: Cell<Vr2>,
        ): Cell<Pair<Vr1, Vr2>> = cell1.switchOf { value1 ->
            cell2.map { value2 ->
                Pair(value1, value2)
            }
        }

        fun <V> of(
            value: V,
        ): Cell<V> = ConstCell(constValue = value)
    }

    abstract val newValues: EventStream<V>

    abstract val currentValue: V

    abstract val changes: EventStream<Change<V>>

    abstract fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr>

    abstract fun <T : Any> form(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): Pair<T, Subscription>

    fun <T : Any> formAndForget(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): T {
        val (target, _) = form(create, update)

        // Forget the subscription, relying purely on garbage collection
        return target
    }

    abstract fun <T : Any> bind(
        target: T,
        update: (T, V) -> Unit,
    ): Subscription

    fun <T : Any> bindAndForget(
        target: T,
        update: (T, V) -> Unit,
    ) {
        bind(
            target = target,
            update = update,
        )
    }

    fun <Vr> switchOf(
        transform: (V) -> Cell<Vr>,
    ): Cell<Vr> = switch(
        nestedCell = map(transform),
    )

    fun <Er> divertOf(
        transform: (V) -> EventStream<Er>,
    ): EventStream<Er> = EventStream.divert(
        nestedEventStream = map(transform),
    )
}

fun <V, T : Any> Cell<V>.bindNested(
    target: T,
    bindInner: (T, V) -> Subscription,
): Subscription = object : Subscription {
    private var innerSubscription = bindInner(
        target,
        currentValue,
    )

    private val outerSubscription = bind(
        target = target,
        update = { it, newValue ->
            innerSubscription.cancel()
            innerSubscription = bindInner(it, newValue)
        },
    )

    override fun cancel() {
        outerSubscription.cancel()
        innerSubscription.cancel()
    }
}
