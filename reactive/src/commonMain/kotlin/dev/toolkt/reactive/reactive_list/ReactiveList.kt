package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.iterable.allUniquePairs
import dev.toolkt.core.iterable.updateRange
import dev.toolkt.core.range.empty
import dev.toolkt.core.range.overlaps
import dev.toolkt.core.range.single
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream

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

                fun <E> set(
                    index: Int,
                    newValue: E,
                ): Update<E> = Update(
                    indexRange = IntRange.single(index),
                    updatedElements = listOf(newValue),
                )

                fun remove(
                    index: Int,
                ): Update<Nothing> = remove(
                    indexRange = IntRange.single(index),
                )

                fun remove(
                    indexRange: IntRange,
                ): Update<Nothing> = Update(
                    indexRange = indexRange,
                    updatedElements = emptyList(),
                )

                fun <E> insert(
                    index: Int,
                    newElement: E,
                ): Update<E> = insert(
                    index = index,
                    newElements = listOf(newElement),
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
                require(!indexRange.isEmpty() || updatedElements.isNotEmpty()) {
                    "Index range cannot be empty unless there are updated elements."
                }
            }
        }

        companion object {
            fun <E> single(
                update: Update<E>,
            ): Change<E> = Change(
                updates = setOf(update),
            )

            fun <E> fill(
                elements: List<E>,
            ): Change<E> = Change.single(
                update = Update.insert(
                    index = 0,
                    newElements = elements,
                ),
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
            extract: (T) -> MutableList<in Nothing>,
        ) {
            extract(target).clear()
        }
    }

    companion object {
        fun <E> of(
            vararg children: E,
        ): ReactiveList<E> = ConstReactiveList(
            constElements = children.toList(),
        )

        fun <E> single(
            element: Cell<E>,
        ): ReactiveList<E> = SingleReactiveList(
            element = element,
        )

        fun <E : Any> singleNotNull(
            element: Cell<E?>,
        ): ReactiveList<E> = SingleNotNullReactiveList(
            element = element,
        )

        fun <E, R> looped(
            block: (ReactiveList<E>) -> Pair<R, ReactiveList<E>>,
        ): R {
            val loopedReactiveList = LoopedReactiveList<E>()

            val (result, reactiveList) = block(loopedReactiveList)

            loopedReactiveList.loop(reactiveList)

            return result
        }
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
        extract: (T) -> MutableList<in E>,
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
