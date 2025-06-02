package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.hold

sealed class Future<out V> {
    sealed class State<out V>

    data object Pending : State<Nothing>()

    data class Fulfilled<out V>(
        val result: V,
    ) : State<V>()

    object Hang : Future<Nothing>() {
        override val state: Cell<State<Nothing>> = Cell.Companion.of(Pending)

        override val currentState: State<Nothing> = Pending

        override val onFulfilled: EventStream<Fulfilled<Nothing>> = EventStream.Never

        override fun <Vr> map(transform: (Nothing) -> Vr): Future<Vr> = Hang
    }

    data class Prefilled<V>(
        val constResult: V,
    ) : Future<V>() {
        private val fulfilledState: Fulfilled<V>
            get() = Fulfilled(result = constResult)

        override val state: Cell<State<V>> = Cell.of(fulfilledState)

        override val currentState: State<V> = fulfilledState

        override val onFulfilled: EventStream<Fulfilled<V>> = NeverEventStream

        override fun <Vr> map(transform: (V) -> Vr): Future<Vr> = Prefilled(
            constResult = transform(constResult),
        )
    }

    companion object {
        fun <V> of(
            constResult: V,
        ): Future<V> = Prefilled(constResult)

        fun <V, V1 : V, V2 : V> oscillate(
            initialValue: V1,
            switchPhase1: (V1) -> Future<V2>,
            switchPhase2: (V2) -> Future<V1>,
        ): Cell<V> = object {
            fun enterPhase1(
                value1: V1,
            ): Cell<V> = deflect(
                initialValue = value1,
                jump = switchPhase1,
                recurse = ::enterPhase2,
            )

            fun enterPhase2(
                value2: V2,
            ): Cell<V> = deflect(
                initialValue = value2,
                jump = switchPhase2,
                recurse = ::enterPhase1,
            )

            val result = enterPhase1(
                value1 = initialValue,
            )
        }.result

        fun <V, V1 : V, V2> deflect(
            initialValue: V1,
            jump: (V1) -> Future<V2>,
            recurse: (V2) -> Cell<V>,
        ): Cell<V> = jump(initialValue).map { value2 ->
            recurse(value2)
        }.switchHold(
            initialValue = initialValue,
        )
    }

    fun unit(): Future<Unit> = map { }

    @Suppress("FunctionName")
    fun null_(): Future<Nothing?> = map { null }

    val onResult: EventStream<V>
        get() = onFulfilled.map { it.result }

    abstract val state: Cell<State<V>>

    abstract val currentState: State<V>

    abstract val onFulfilled: EventStream<Fulfilled<V>>

    abstract fun <Vr> map(
        transform: (V) -> Vr,
    ): Future<Vr>
}

fun <V> Future<V>.hold(
    initialValue: V,
): Cell<V> = when (val foundState = currentState) {
    is Future.Fulfilled<V> -> Cell.of(foundState.result)

    Future.Pending -> onResult.hold(initialValue)
}

fun <V> Future<Cell<V>>.switchHold(
    initialCell: Cell<V>,
): Cell<V> = when (val state = currentState) {
    is Future.Fulfilled<Cell<V>> -> state.result

    Future.Pending -> Cell.switch(
        onResult.hold(initialCell),
    )
}

fun <V> Future<Cell<V>>.switchHold(
    initialValue: V,
): Cell<V> = switchHold(
    initialCell = Cell.of(initialValue),
)

fun <V> Future<EventStream<V>>.divertHold(
    initialEventStream: EventStream<V>,
): EventStream<V> = when (val foundState = currentState) {
    is Future.Fulfilled<EventStream<V>> -> foundState.result

    Future.Pending -> EventStream.divert(
        onResult.hold(initialEventStream),
    )
}
