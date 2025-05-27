package dev.toolkt.reactive.vertices.reactive_list

import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.Listener

class MapReactiveListVertex<E, Er>(
    private val source: ReactiveListVertex<E>,
    private val transform: (E) -> Er,
) : DependentReactiveListVertex<Er>(
    initialElements = source.currentElements.map(transform),
) {
    override val kind: String = "MapL"

    override fun buildHybridSubscription() = source.subscribeHybrid(
        listener = object : Listener<ReactiveList.Change<E>> {
            override fun handle(change: ReactiveList.Change<E>) {
                update(
                    change = ReactiveList.Change(
                        updates = change.updates.map { update ->
                            ReactiveList.Change.Update(
                                indexRange = update.indexRange,
                                updatedElements = update.updatedElements.map(transform),
                            )
                        }.toSet(),
                    ),
                )
            }
        },
    )

    init {
        init()
    }
}
