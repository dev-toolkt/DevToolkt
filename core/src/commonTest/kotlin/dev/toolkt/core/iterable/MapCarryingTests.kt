package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class MapCarryingTests {
    @Test
    fun testMapCarrying_empty() {
        val list = emptyList<String>()

        val (result, finalCarry) = list.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair(value.uppercase(), value.last().toString())
        }

        assertEquals(
            expected = emptyList(),
            actual = result,
        )

        assertEquals(
            expected = "x",
            actual = finalCarry,
        )
    }

    @Test
    fun testMapCarrying_single() {
        val list = listOf("ab")

        val (result, finalCarry) = list.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair("$carry${value.uppercase()}", value.last().toString())
        }

        assertEquals(
            expected = listOf("xAB"),
            actual = result,
        )

        assertEquals(
            expected = "b",
            actual = finalCarry,
        )
    }

    @Test
    fun testMapCarrying_simple_sameType() {
        val list = listOf("ab", "cd", "ef")
        val (result, finalCarry) = list.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair("$carry${value.uppercase()}", value.last().toString())
        }

        assertEquals(
            expected = listOf("xAB", "bCD", "dEF"),
            actual = result,
        )

        assertEquals(
            expected = "f",
            actual = finalCarry,
        )
    }

    @Test
    fun testMapCarrying_simple_differentType() {
        val list = listOf(0, 1, 2, 3)
        val (result, finalCarry) = list.mapCarrying(
            initialCarry = 'x',
        ) { carry: Char, value: Int ->
            Pair("$carry:${value * -2}", '0' + value)
        }

        assertEquals(
            expected = listOf("x:0", "0:-2", "1:-4", "2:-6"),
            actual = result,
        )

        assertEquals(
            expected = '3',
            actual = finalCarry,
        )
    }

    @Test
    fun testSequenceMapCarrying_empty() {
        val sequence = emptySequence<String>()

        val result = sequence.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair(value.uppercase(), value.last().toString())
        }.toList()

        assertEquals(
            expected = emptyList(),
            actual = result,
        )
    }

    @Test
    fun testSequenceMapCarrying_single() {
        val sequence = sequenceOf("ab")

        val result = sequence.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair("$carry${value.uppercase()}", value.last().toString())
        }.toList()

        assertEquals(
            expected = listOf("xAB"),
            actual = result,
        )
    }

    @Test
    fun testSequenceMapCarrying_simple_sameType() {
        val sequence = sequenceOf("ab", "cd", "ef")
        val result = sequence.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair("$carry${value.uppercase()}", value.last().toString())
        }.toList()

        assertEquals(
            expected = listOf("xAB", "bCD", "dEF"),
            actual = result,
        )
    }

    @Test
    fun testSequenceMapCarrying_simple_differentType() {
        val sequence = sequenceOf(0, 1, 2, 3)
        val result = sequence.mapCarrying(
            initialCarry = 'x',
        ) { carry: Char, value: Int ->
            Pair("$carry:${value * -2}", '0' + value)
        }.toList()

        assertEquals(
            expected = listOf("x:0", "0:-2", "1:-4", "2:-6"),
            actual = result,
        )
    }
}
