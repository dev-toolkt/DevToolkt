package dev.toolkt.core.platform

import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

private class Key(
    val data: List<Int>,
) {
    companion object {
        private val random = Random(seed = 0)

        fun create(): Key = Key(
            data = List(8000) { random.nextInt() },
        )
    }

    override fun toString(): String = "KeyJvm#${hashCode()}"
}

class PlatformWeakMapSystemTest {
    val tryCount = 128

    private var key1: Key? = Key.create()

    private var key2: Key? = Key.create()

    private var key3: Key? = Key.create()

    private var key4: Key? = Key.create()

    suspend fun runTest() {
        println("Starting the PlatformWeakMap system test...")

        val weakMap = mutableWeakMapOf<Key, String>()

        weakMap[key1!!] = "value1"
        weakMap[key2!!] = "value2"
        weakMap[key3!!] = "value3"
        weakMap[key4!!] = "value4"

        if (weakMap.size != 4) {
            throw AssertionError("Unexpected initial map size: ${weakMap.size}")
        }

        if (weakMap.keys.toSet() != setOf(key1, key2, key3, key4)) {
            throw AssertionError("Unexpected initial keys in the map: ${weakMap.keys}")
        }

        key2 = null
        key4 = null

        waitUntilExpected(
            weakMap = weakMap,
            expectedKeys = listOf(key1!!, key3!!).sortedBy { it.hashCode() },
        )

        println("Success!")
    }

    private suspend fun waitUntilExpected(
        weakMap: MutableMap<Key, String>,
        expectedKeys: List<Key>
    ) {
        (tryCount downTo 0).forEach { tryIndex ->
            if (tryIndex == 0) {
                throw AssertionError("Failed to collect garbage after $tryCount attempts")
            }

            if (tryIndex % 10 == 0) {
                println("Waiting for garbage collection... (tries left: ${tryIndex})")
            }

            delay(50.milliseconds)
            PlatformSystem.collectGarbage()

            val actualKeys = weakMap.keys.toList().sortedBy { it.hashCode() }

            if (actualKeys == expectedKeys) {
                return
            }
        }
    }
}
