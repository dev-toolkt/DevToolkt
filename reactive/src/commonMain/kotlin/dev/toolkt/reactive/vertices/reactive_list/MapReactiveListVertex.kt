package dev.toolkt.reactive.vertices.reactive_list

class MapReactiveListVertex<E, Er>(
    private val source: ReactiveListVertex<E>,
    private val transform: (E) -> Er,
) : DependentReactiveListVertex<Er>(
    initialElements = source.currentElements.map(transform),
) {
    override val kind: String = "MapL"

    override fun buildHybridSubscription() = source.subscribeHybridRaw { change ->
        update(
            change = change.map(transform),
        )
    }

    init {
        init()
    }
}
