package dev.toolkt.reactive.reactive_list_ng

import dev.toolkt.reactive.event_stream_ng.EventStreamNg

data class ConstReactiveListNg<out E>(
    private val constElements: List<E>,
) : ReactiveListNg<E>() {
    override val currentElements: List<E>
        get() = constElements

    override val changes: EventStreamNg<Change<E>> = EventStreamNg.Never

    override fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveListNg<Er> = ConstReactiveListNg(
        constElements = constElements.map(transform),
    )

    override fun <T : Any> bind(
        target: T,
        extract: (T) -> MutableList<in E>,
    ) {
        copyNow(mutableList = extract(target))
    }
}
