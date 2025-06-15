package dev.toolkt.core.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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


private class GarbageCollectionTest {
    private val key1: Key = Key.create()

    private var key2: Key? = Key.create()

    private val key3: Key = Key.create()

    private var key4: Key? = Key.create()

    suspend fun run() {
        println("Starting the PlatformWeakMap system test...")

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
    fun testGarbageCollection() = runTest(
        timeout = 15.seconds,
    ) {
        withContext(Dispatchers.Default) {
            GarbageCollectionTest().run()
        }
    }
}

private enum class WaitUntilResult {
    Timeout, Success,
}

private suspend fun <T> assertEqualsEventually(
    pauseDuration: Duration,
    timeoutDuration: Duration,
    expected: T,
    actual: () -> T,
) {
    val result = waitUntil(
        pauseDuration = pauseDuration,
        timeoutDuration = timeoutDuration,
    ) {
        actual() == expected
    }

    when (result) {
        WaitUntilResult.Timeout -> {
            throw AssertionError("Expected value to eventually become $expected, but it is at ${actual()} after checking every $pauseDuration for $timeoutDuration")
        }

        WaitUntilResult.Success -> {}
    }
}

private suspend fun waitUntil(
    pauseDuration: Duration,
    timeoutDuration: Duration,
    predicate: () -> Boolean,
): WaitUntilResult {
    val tryCount = (timeoutDuration / pauseDuration).roundToInt()

    (tryCount downTo 0).forEach { tryIndex ->
        delay(pauseDuration)
        PlatformSystem.collectGarbage()

        if (predicate()) {
            return WaitUntilResult.Success
        }
    }

    return WaitUntilResult.Timeout
}
