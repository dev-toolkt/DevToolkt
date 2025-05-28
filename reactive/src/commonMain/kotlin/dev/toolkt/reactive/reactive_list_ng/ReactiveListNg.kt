package dev.toolkt.reactive.reactive_list_ng

import dev.toolkt.core.iterable.allUniquePairs
import dev.toolkt.core.iterable.updateRange
import dev.toolkt.core.range.empty
import dev.toolkt.core.range.overlaps
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream_ng.EventStreamNg

abstract class ReactiveListNg<out E> {
    data class Change<out E>(
        val updates: Set<Update<E>>,
    ) {
        data class Update<out E>(
            val indexRange: IntRange,
            val updatedElements: List<E>,
        ) {
            companion object {
                fun <E> change(
                    indexRange: IntRange,
                    changedElements: List<E>,
                ): Update<E> = Update(
                    indexRange = indexRange,
                    updatedElements = changedElements,
                )

                fun <E> remove(
                    indexRange: IntRange,
                ): Update<E> = Update(
                    indexRange = indexRange,
                    updatedElements = emptyList(),
                )

                fun <E> insert(
                    index: Int,
                    newElements: List<E>,
                ): Update<E> = Update(
                    indexRange = IntRange.empty(index),
                    updatedElements = newElements,
                )
            }

            fun <Er> map(
                transform: (E) -> Er,
            ): Update<Er> = Update(
                indexRange = indexRange,
                updatedElements = updatedElements.map(transform),
            )

            fun toChange(): Change<E> = Change(
                updates = setOf(this),
            )

            init {
                require(!indexRange.isEmpty() || updatedElements.isNotEmpty())
            }
        }

        companion object {
            fun <E> single(
                update: Update<E>,
            ): Change<E> = Change(
                updates = setOf(update),
            )
        }

        val updatesInOrder: List<Update<E>>
            get() = updates.sortedBy { it.indexRange.first }

        fun <Er> map(
            transform: (E) -> Er,
        ): Change<Er> = Change(
            updates = updates.map { update ->
                update.map(transform)
            }.toSet(),
        )

        init {
            updates.allUniquePairs().none { (updateA, updateB) ->
                updateA.indexRange.overlaps(updateB.indexRange)
            }
        }
    }

    object Empty : ReactiveListNg<Nothing>() {
        override val currentElements: List<Nothing> = emptyList()

        override val changes: EventStreamNg<Change<Nothing>> = EventStreamNg.Never

        override fun <Er> map(
            transform: (Nothing) -> Er,
        ): ReactiveListNg<Er> = Empty

        override fun <T : Any> bind(
            target: T,
            extract: (T) -> MutableList<in Nothing>,
        ) {
            extract(target).clear()
        }
    }

    companion object {
        fun <E> of(
            vararg children: E,
        ): ReactiveListNg<E> = ConstReactiveListNg(
            constElements = children.toList(),
        )
    }

    abstract val currentElements: List<E>

    abstract val changes: EventStreamNg<Change<E>>

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveListNg<Er>

    fun get(inex: Int): Cell<E?> {
        TODO()
    }

    abstract fun <T : Any> bind(
        target: T,
        extract: (T) -> MutableList<in E>,
    )
}

internal fun <E> ReactiveListNg<E>.copyNow(
    mutableList: MutableList<E>,
) {
    mutableList.clear()
    mutableList.addAll(currentElements)
}

fun <E> ReactiveListNg.Change.Update<E>.applyTo(
    mutableList: MutableList<E>,
) {
    mutableList.updateRange(
        indexRange = indexRange,
        elements = updatedElements,
    )
}

fun <E> ReactiveListNg.Change<E>.applyTo(
    mutableList: MutableList<E>,
) {
    updatesInOrder.reversed().forEach { update ->
        update.applyTo(mutableList = mutableList)
    }
}
