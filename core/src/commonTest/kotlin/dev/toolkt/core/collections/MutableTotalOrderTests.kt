package dev.toolkt.core.collections


import dev.toolkt.core.order.OrderRelation
import kotlin.test.Test

class MutableTotalOrderTests {
    private enum class Fruit {
        Apple, Raspberry, Banana, Orange, Kiwi, Mango, Pineapple, Strawberry, Watermelon, Grape,
    }

    @Test
    fun testInitial() {
        val mutableTotalOrder = MutableTotalOrder<Fruit>()

        mutableTotalOrder.verifyContent(
            pairs = emptyList(),
        )
    }

    @Test
    fun testSet() {
        val (mutableTotalOrder, handles) = MutableTotalOrder.of(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Watermelon,
        )

        val (
            handleBanana,
            handleOrange,
            handleKiwi,
            handleBerry,
            handleWatermelon,
        ) = handles

        mutableTotalOrder.set(
            handle = handleBerry,
            element = Fruit.Raspberry,
        )

        mutableTotalOrder.verifyContent(
            handleBanana to Fruit.Banana,
            handleOrange to Fruit.Orange,
            handleKiwi to Fruit.Kiwi,
            handleBerry to Fruit.Raspberry,
            handleWatermelon to Fruit.Watermelon,
        )
    }

    @Test
    fun testAddExtremal_firstGreater() {
        val mutableTotalOrder = MutableTotalOrder<Fruit>()

        val handleBanana = mutableTotalOrder.addExtremal(
            relation = OrderRelation.Greater,
            element = Fruit.Banana,
        )

        mutableTotalOrder.verifyContent(
            handleBanana to Fruit.Banana,
        )
    }

    @Test
    fun testAddExtremal_firstSmaller() {
        val mutableTotalOrder = MutableTotalOrder<Fruit>()

        val handleGrape = mutableTotalOrder.addExtremal(
            relation = OrderRelation.Greater,
            element = Fruit.Grape,
        )

        mutableTotalOrder.verifyContent(
            handleGrape to Fruit.Grape,
        )
    }

    @Test
    fun testAddExtremal_multiple() {
        val mutableTotalOrder = MutableTotalOrder<Fruit>()

        val handleBanana = mutableTotalOrder.addExtremal(
            relation = OrderRelation.Greater,
            element = Fruit.Banana,
        )

        val handleStrawberry = mutableTotalOrder.addExtremal(
            relation = OrderRelation.Smaller,
            element = Fruit.Strawberry,
        )

        val handleWatermelon = mutableTotalOrder.addExtremal(
            relation = OrderRelation.Smaller,
            element = Fruit.Watermelon,
        )

        val handleMango = mutableTotalOrder.addExtremal(
            relation = OrderRelation.Greater,
            element = Fruit.Mango,
        )

        mutableTotalOrder.verifyContent(
            handleWatermelon to Fruit.Watermelon,
            handleStrawberry to Fruit.Strawberry,
            handleBanana to Fruit.Banana,
            handleMango to Fruit.Mango,
        )
    }

    @Test
    fun testAddRelative() {
        val (mutableTotalOrder, handles) = MutableTotalOrder.of(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Watermelon,
        )

        val (
            handleBanana,
            handleOrange,
            handleKiwi,
            handleStrawberry,
            handleWatermelon,
        ) = handles

        val handleMango = mutableTotalOrder.addRelative(
            handle = handleOrange,
            relation = OrderRelation.Smaller,
            element = Fruit.Mango,
        )

        mutableTotalOrder.verifyContent(
            handleBanana to Fruit.Banana,
            handleMango to Fruit.Mango,
            handleOrange to Fruit.Orange,
            handleKiwi to Fruit.Kiwi,
            handleStrawberry to Fruit.Strawberry,
            handleWatermelon to Fruit.Watermelon,
        )

        val handlePineapple = mutableTotalOrder.addRelative(
            handle = handleMango,
            relation = OrderRelation.Greater,
            element = Fruit.Pineapple,
        )

        mutableTotalOrder.verifyContent(
            handleBanana to Fruit.Banana,
            handleMango to Fruit.Mango,
            handlePineapple to Fruit.Pineapple,
            handleOrange to Fruit.Orange,
            handleKiwi to Fruit.Kiwi,
            handleStrawberry to Fruit.Strawberry,
            handleWatermelon to Fruit.Watermelon,
        )
    }

    @Test
    fun testAddRelative_duplicate() {
        val (mutableTotalOrder, handles) = MutableTotalOrder.of(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Watermelon,
        )

        val (
            handleBanana,
            handleOrange,
            handleKiwi1,
            handleStrawberry,
            handleWatermelon,
        ) = handles

        val handleKiwi2 = mutableTotalOrder.addRelative(
            handle = handleWatermelon,
            relation = OrderRelation.Smaller,
            element = Fruit.Kiwi,
        )

        mutableTotalOrder.verifyContent(
            handleBanana to Fruit.Banana,
            handleOrange to Fruit.Orange,
            handleKiwi1 to Fruit.Kiwi,
            handleStrawberry to Fruit.Strawberry,
            handleKiwi2 to Fruit.Kiwi,
            handleWatermelon to Fruit.Watermelon,
        )
    }

    @Test
    fun testRemove() {
        val (mutableTotalOrder, handles) = MutableTotalOrder.of(
            Fruit.Apple,
            Fruit.Orange,
            Fruit.Kiwi,
        )

        val (
            handleApple,
            handleOrange,
            handleKiwi,
        ) = handles

        mutableTotalOrder.remove(handle = handleOrange)

        mutableTotalOrder.verifyContent(
            handleApple to Fruit.Apple,
            handleKiwi to Fruit.Kiwi,
        )

        mutableTotalOrder.remove(handle = handleApple)

        mutableTotalOrder.verifyContent(
            handleKiwi to Fruit.Kiwi,
        )

        mutableTotalOrder.remove(handle = handleKiwi)

        mutableTotalOrder.verifyContent(
            pairs = emptyList(),
        )
    }
}
