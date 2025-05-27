package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventStream

data class ConstReactiveList<out E>(
    private val constElements: List<E>,
) : ReactiveList<E>() {
    override val currentElements: List<E>
        get() = constElements

    override val changes: EventStream<Change<E>> = EventStream.Never

    override fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveList<Er> = ConstReactiveList(
        constElements = constElements.map(transform),
    )

    override fun <T : Any> bind(
        target: T,
        mutableList: MutableList<in E>,
    ) {
        copyNow(mutableList = mutableList)
    }
}
