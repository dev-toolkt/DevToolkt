package dev.toolkt.core.platform.test_utils

import dev.toolkt.core.platform.PlatformSystem
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration

enum class WaitUntilResult {
    Timeout, Success,
}

suspend fun waitUntil(
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
