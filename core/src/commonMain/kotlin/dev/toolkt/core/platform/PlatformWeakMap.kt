@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

expect class PlatformWeakMap<K : Any, V : Any>() : AbstractMutableMap<K, V> {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>

    override fun put(key: K, value: V): V?
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableWeakMapOf(): MutableMap<K, V> = PlatformWeakMap()
