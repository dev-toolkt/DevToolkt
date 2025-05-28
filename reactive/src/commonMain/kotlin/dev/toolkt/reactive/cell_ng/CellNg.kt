package dev.toolkt.reactive.cell_ng

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream_ng.EventStreamNg

sealed class CellNg<out V> {
    data class Change<out V>(
        val oldValue: V,
        val newValue: V,
    )

    companion object {
        fun <V> switch(
            nestedCell: CellNg<CellNg<V>>,
        ): CellNg<V> = SwitchCellNg(
            nestedCell = nestedCell,
        )

        fun <V1, V2, Vr> map2(
            cell1: CellNg<V1>,
            cell2: CellNg<V2>,
            transform: (V1, V2) -> CellNg<Vr>,
        ): CellNg<Vr> = cell1.switchOf { value1 ->
            cell2.switchOf { value2 ->
                transform(value1, value2)
            }
        }

        fun <Vr1, Vr2> zip2(
            cell1: CellNg<Vr1>,
            cell2: CellNg<Vr2>,
        ): CellNg<Pair<Vr1, Vr2>> = cell1.switchOf { value1 ->
            cell2.map { value2 ->
                Pair(value1, value2)
            }
        }

        fun <V> of(
            value: V,
        ): CellNg<V> = ConstCellNg(constValue = value)
    }

    abstract val newValues: EventStreamNg<V>

    abstract val currentValue: V

    abstract val changes: EventStreamNg<Change<V>>

    abstract fun <Vr> map(
        transform: (V) -> Vr,
    ): CellNg<Vr>

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
        transform: (V) -> CellNg<Vr>,
    ): CellNg<Vr> = switch(
        nestedCell = map(transform),
    )

    fun <Er> divertOf(
        transform: (V) -> EventStreamNg<Er>,
    ): EventStreamNg<Er> = EventStreamNg.divert(
        nestedEventStream = map(transform),
    )
}

fun <V, T : Any> CellNg<V>.bindNested(
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
