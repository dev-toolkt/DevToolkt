package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class WithPreviousTests {
    @Test
    fun testWithPreviousBy_standardCase() {
        val actual = listOf(
            "foo!",
            "bar?",
            "baz!",
            "xyz*",
        ).withPreviousBy(
            outerLeft = '%',
            selector = String::last,
        )

        assertEquals(
            expected = listOf(
                WithPrevious(
                    prevElement = '%',
                    element = "foo!",
                ),
                WithPrevious(
                    prevElement = '!',
                    element = "bar?",
                ),
                WithPrevious(
                    prevElement = '?',
                    element = "baz!",
                ),
                WithPrevious(
                    prevElement = '!',
                    element = "xyz*",
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithPreviousBy_singleElement() {
        val actual = listOf(
            "foo",
        ).withPreviousBy(
            outerLeft = '&',
            selector = String::first,
        )

        assertEquals(
            expected = listOf(
                WithPrevious(
                    element = "foo",
                    prevElement = '&'
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithPreviousBy_emptyList() {
        val actual = emptyList<String>().withPreviousBy(
            outerLeft = '&',
            selector = String::first,
        )

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }
}
