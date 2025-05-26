package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class TakeWhileIsInstanceTests {
    sealed class Animal

    data class Dog(val dogName: String) : Animal()

    data class Cat(val catName: String) : Animal()

    @Test
    fun testTakeWhileIsInstanceEmpty() {
        val animals: List<Animal> = emptyList()

        val dogs: List<Dog> = animals.takeWhileIsInstance()
        val (dogs2, reminder) = animals.takeWhileIsInstanceWithReminder()

        assertEquals(
            expected = emptyList(),
            actual = dogs,
        )

        assertEquals(
            expected = emptyList(),
            actual = dogs2,
        )

        assertEquals(
            expected = emptyList(),
            actual = reminder,
        )
    }

    @Test
    fun testTakeWhileIsInstanceNoMatching() {
        val animals: List<Animal> = listOf(
            Cat(catName = "Tom"),
            Cat(catName = "Jerry"),
            Cat(catName = "Felix"),
        )

        val dogs: List<Dog> = animals.takeWhileIsInstance()
        val (dogs2, reminder) = animals.takeWhileIsInstanceWithReminder<Animal, Dog>()

        assertEquals(
            expected = emptyList(),
            actual = dogs,
        )

        assertEquals(
            expected = emptyList(),
            actual = dogs2,
        )

        assertEquals(
            expected = animals,
            actual = reminder,
        )
    }

    @Test
    fun testTakeWhileIsInstanceNoInitialMatching() {
        val animals: List<Animal> = listOf(
            Cat(catName = "Tom"),
            Cat(catName = "Jerry"),
            Cat(catName = "Felix"),
            Dog(dogName = "Rex"),
            Dog(dogName = "Rex2"),
            Dog(dogName = "Rex3"),
        )

        val dogs: List<Dog> = animals.takeWhileIsInstance()
        val (dogs2, reminder) = animals.takeWhileIsInstanceWithReminder<Animal, Dog>()

        assertEquals(
            expected = emptyList(),
            actual = dogs,
        )

        assertEquals(
            expected = emptyList(),
            actual = dogs2,
        )

        assertEquals(
            expected = animals,
            actual = reminder,
        )
    }

    @Test
    fun testTakeWhileIsInstanceSomeMatching() {
        val animals: List<Animal> = listOf(
            Dog(dogName = "Rex"),
            Dog(dogName = "Rex2"),
            Dog(dogName = "Rex3"),
            Cat(catName = "Tom"),
            Dog(dogName = "Buddy"),
            Cat(catName = "Jerry"),
        )

        val dogs: List<Dog> = animals.takeWhileIsInstance()
        val (dogs2, reminder) = animals.takeWhileIsInstanceWithReminder<Animal, Dog>()

        val expectedDogs = listOf(
            Dog(dogName = "Rex"),
            Dog(dogName = "Rex2"),
            Dog(dogName = "Rex3"),
        )

        assertEquals(
            expected = expectedDogs,
            actual = dogs,
        )

        assertEquals(
            expected = expectedDogs,
            actual = dogs2,
        )

        assertEquals(
            expected = listOf(
                Cat(catName = "Tom"),
                Dog(dogName = "Buddy"),
                Cat(catName = "Jerry"),
            ),
            actual = reminder,
        )
    }

    @Test
    fun testTakeWhileIsInstanceAllMatching() {
        val animals = listOf(
            Dog(dogName = "Rex"),
            Dog(dogName = "Rex2"),
            Dog(dogName = "Rex3"),
        )

        val dogs: List<Dog> = animals.takeWhileIsInstance()
        val (dogs2, reminder) = animals.takeWhileIsInstanceWithReminder<Animal, Dog>()

        assertEquals(
            expected = animals,
            actual = dogs,
        )

        assertEquals(
            expected = animals,
            actual = dogs2,
        )

        assertEquals(
            expected = emptyList(),
            actual = reminder,
        )
    }
}
