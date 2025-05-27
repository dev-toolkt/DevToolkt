package dev.toolkt.core.collection

import java.util.Collections
import java.util.WeakHashMap

object CollectionUtils {
    fun <E : Any> newWeakSet(): MutableSet<E> = Collections.newSetFromMap<E>(
        WeakHashMap<E, Boolean?>(),
    )
}
