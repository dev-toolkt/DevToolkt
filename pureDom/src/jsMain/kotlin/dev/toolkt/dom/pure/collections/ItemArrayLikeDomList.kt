package dev.toolkt.dom.pure.collections

import org.w3c.dom.ItemArrayLike

interface ItemArrayLikeDomList<out E> : DomList<E> {
    override val size: Int
        get() = itemArrayLike.length

    override fun getOrNull(
        index: Int,
    ): E? = itemArrayLike.item(index)

    val itemArrayLike: ItemArrayLike<E>
}
