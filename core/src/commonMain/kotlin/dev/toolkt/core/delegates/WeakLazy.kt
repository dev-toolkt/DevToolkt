package dev.toolkt.core.delegates

import dev.toolkt.core.platform.PlatformWeakReference
import kotlin.reflect.KProperty

class WeakLazy<T : Any>(
    private val initializer: () -> T,
) {
    private var cachedValue: PlatformWeakReference<T>? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val cachedValue = this.cachedValue?.get()

        when (cachedValue) {
            null -> {
                val newCachedValue = initializer()
                this.cachedValue = PlatformWeakReference(newCachedValue)
                return newCachedValue
            }

            else -> return cachedValue
        }
    }

    fun isInitialized(): Boolean = cachedValue !== null
}

fun <T : Any> weakLazy(
    initializer: () -> T,
): WeakLazy<T> = WeakLazy(
    initializer = initializer,
)
