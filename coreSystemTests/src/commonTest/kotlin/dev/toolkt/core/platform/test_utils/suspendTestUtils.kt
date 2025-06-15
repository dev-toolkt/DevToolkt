package dev.toolkt.core.platform.test_utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.time.Duration

suspend fun <T> assertEqualsEventually(
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

/**
 * Executes [testBody] as a test in a new coroutine, using the default dispatcher. This disables the delay-skipping
 * behavior.
 */
fun <T> runTestDefault(
    duration: Duration,
    testBody: suspend CoroutineScope.() -> T
): TestResult = runTest(
    timeout = duration,
) {
    // Although `runTest` accepts a `context` parameter, it throws an exception when `Dispatchers.Default` is passed
    withContext(Dispatchers.Default) {
        testBody()
    }
}
