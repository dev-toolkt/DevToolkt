package dev.toolkt.core.platform

import dev.toolkt.core.platform.test_utils.assertEqualsEventually
import dev.toolkt.core.platform.test_utils.runTestDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private class GarbageCollectionTest {
    // A key of random data that weights roughly 32 KiB
    private class Key(
        @Suppress("unused") val data: List<Int>,
    ) {
        companion object {
            private val random = Random(seed = 0)

            fun create(): Key = Key(
                data = List(8000) { random.nextInt() },
            )
        }

        override fun toString(): String = "Key#${hashCode()}"
    }

    private val key1: Key = Key.create()

    private var key2: Key? = Key.create()

    private val key3: Key = Key.create()

    private var key4: Key? = Key.create()

    suspend fun run() {
        val weakMap = mutableWeakMapOf<Key, String>()

        weakMap[key1] = "value1"
        weakMap[key2!!] = "value2"
        weakMap[key3] = "value3"
        weakMap[key4!!] = "value4"

        assertEquals(
            expected = 4,
            actual = weakMap.size,
        )

        assertEquals(
            expected = setOf(key1, key2, key3, key4),
            actual = weakMap.keys.toSet(),
        )

        key2 = null
        key4 = null

        assertEqualsEventually(
            pauseDuration = 100.milliseconds,
            timeoutDuration = 10.seconds,
            expected = listOf(key1, key3).sortedBy { it.hashCode() },
            actual = { weakMap.keys.toList().sortedBy { it.hashCode() } },
        )
    }
}

class PlatformWeakMapSystemTests {
    @Test
    fun testGarbageCollection() = runTestDefault(
        duration = 15.seconds,
    ) {
        GarbageCollectionTest().run()
    }
}
