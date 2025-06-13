package dev.toolkt.core

import kotlin.test.assertTrue

/**
 * A helper function indicating that the condition is not verified as part of
 * the test, but rather is a assumption held by the test itself.
 */
fun assertHolds(
    precondition: Boolean,
    message: String? = null,
) {
    assertTrue(
        actual = precondition,
        message = message,
    )
}
