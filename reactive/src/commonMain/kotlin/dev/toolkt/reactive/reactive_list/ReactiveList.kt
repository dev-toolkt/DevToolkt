package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.allUniquePairs
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.core.range.empty
import dev.toolkt.core.iterable.updateRange
import dev.toolkt.core.range.overlaps

abstract class ReactiveList<out E> {
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

    object Empty : ReactiveList<Nothing>() {
        override val currentElements: List<Nothing> = emptyList()

        override val changes: EventStream<Change<Nothing>> = EventStream.Never

        override fun <Er> map(
            transform: (Nothing) -> Er,
        ): ReactiveList<Er> = Empty

        override fun <T : Any> bind(
            target: T,
            mutableList: MutableList<*>,
        ) {
            mutableList.clear()
        }
    }

    companion object {
        fun <E> of(
            vararg children: E,
        ): ReactiveList<E> = ConstReactiveList(
            constElements = children.toList(),
        )
    }

    abstract val currentElements: List<E>

    abstract val changes: EventStream<Change<E>>

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveList<Er>

    fun get(inex: Int): Cell<E?> {
        TODO()
    }

    abstract fun <T : Any> bind(
        target: T,
        mutableList: MutableList<in E>,
    )
}

internal fun <E> ReactiveList<E>.copyNow(
    mutableList: MutableList<E>,
) {
    mutableList.clear()
    mutableList.addAll(currentElements)
}

fun <E> ReactiveList.Change.Update<E>.applyTo(
    mutableList: MutableList<E>,
) {
    mutableList.updateRange(
        indexRange = indexRange,
        elements = updatedElements,
    )
}

fun <E> ReactiveList.Change<E>.applyTo(
    mutableList: MutableList<E>,
) {
    updatesInOrder.reversed().forEach { update ->
        update.applyTo(mutableList = mutableList)
    }
}

