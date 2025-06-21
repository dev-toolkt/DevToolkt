package dev.toolkt.core.collections

import kotlin.jvm.JvmInline

abstract class HandleIterator<E, HandleT : Any>(
    firstElementHandle: HandleT?,
) : MutableIterator<E> {
    /**
     * The iterator advancement, i.e. the relative difference between the internal
     * and the externally perceived state.
     */
    sealed interface Advancement<HandleT : Any> {
        /**
         * The iterator's state after a successful next() call. The iterator
         * is unconditionally ready for a remove() call. The iterator is ready
         * for a next() call, but it might throw if it turns out that there's
         * no next element in the iteration.
         */
        class Abreast<HandleT : Any>(
            val lastHandle: HandleT,
        ) : Advancement<HandleT> {
            /**
             * The cached ahead state
             */
            private var peekedAhead: Ahead<HandleT>? = null

            fun peekAhead(
                getNext: (HandleT) -> HandleT?,
            ): Ahead<HandleT> = when (val peekedAhead = this.peekedAhead) {
                null -> {
                    val nextHandle = getNext(lastHandle)
                    val freshPeekedAhead = Ahead(nextHandle)

                    this.peekedAhead = freshPeekedAhead

                    freshPeekedAhead
                }

                else -> peekedAhead
            }

            override fun getAhead(
                getNext: (HandleT) -> HandleT?,
            ): Ahead<HandleT> = peekAhead(getNext = getNext)
        }

        /**
         * The iterator's initial state or the state after a remove() call. The
         * iterator is _not_ ready for another remove() call. The iterator is ready
         * for a next() call, but it will throw if there's no next element in
         * the iteration (we already know if that's the case, as we're "ahead").
         */
        @JvmInline
        value class Ahead<HandleT : Any>(
            /**
             * The pre-fetched handle to the next element in the iteration or null if the iteration is about to end
             */
            val nextHandle: HandleT?,
        ) : Advancement<HandleT> {
            override fun getAhead(
                getNext: (HandleT) -> HandleT?,
            ): Ahead<HandleT> = this
        }

        /**
         * Get the ahead state of the iterator, without changing the internal state (other than for caching purposes)
         */
        fun getAhead(
            /**
             * The function that might be called to retrieve the handle's successor
             */
            getNext: (HandleT) -> HandleT?,
        ): Ahead<HandleT>
    }

    private var advancement: Advancement<HandleT> = Advancement.Ahead(
        nextHandle = firstElementHandle,
    )

    final override fun remove() {
        when (val currentAdvancement = this.advancement) {
            is Advancement.Ahead<HandleT> -> {
                // Possible cases:
                // - Initial iteration state (next was never called at all)
                // - Iteration ended (previous next() call returned the last element, hasNext() == false)
                // - The iteration is ongoing, but remove() was already called
                throw IllegalStateException("`next` has not been called yet, or the most recent `next` call has already been followed by a remove call.")
            }

            is Advancement.Abreast<HandleT> -> {
                val currentHandle = currentAdvancement.lastHandle
                val ahead = currentAdvancement.peekAhead(this::getNext)

                remove(handle = currentHandle)

                advancement = ahead
            }
        }
    }

    final override fun next(): E {
        val ahead = advancement.getAhead(this::getNext)

        val nextHandle = ahead.nextHandle ?: throw NoSuchElementException("The iteration has no next element.")

        val element = resolve(handle = nextHandle)

        advancement = Advancement.Abreast(
            lastHandle = nextHandle,
        )

        return element
    }

    final override fun hasNext(): Boolean = advancement.getAhead(this::getNext).nextHandle != null

    protected abstract fun resolve(handle: HandleT): E

    protected abstract fun getNext(handle: HandleT): HandleT?

    protected abstract fun remove(handle: HandleT)
}
