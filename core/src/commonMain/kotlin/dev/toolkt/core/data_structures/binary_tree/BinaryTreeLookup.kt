package dev.toolkt.core.data_structures.binary_tree

import kotlin.random.Random

interface Guide<in PayloadT> {
    sealed interface Instruction

    data class TurnInstruction(
        /**
         * The side of the tree to turn to
         */
        val side: BinaryTree.Side,
    ) : Instruction

    /**
     * An instruction to stop
     */
    data object StopInstruction : Instruction

    fun instruct(
        payload: PayloadT,
    ): Instruction
}

/**
 * A guide that's looking for a given payload
 */
private class ComparatorGuide<PayloadT>(
    private val comparator: Comparator<PayloadT>,
    private val locatedPayload: PayloadT,
) : Guide<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): Guide.Instruction {
        val result = comparator.compare(locatedPayload, payload)

        return when {
            result == 0 -> Guide.StopInstruction

            else -> Guide.TurnInstruction(
                side = when {
                    result < 0 -> BinaryTree.Side.Left
                    else -> BinaryTree.Side.Right
                },
            )
        }
    }
}

/**
 * A guide that's turning randomly
 */
private class RandomGuide<PayloadT>(
    private val random: Random,
) : Guide<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): Guide.Instruction = Guide.TurnInstruction(
        side = random.nextSide(),
    )
}

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.findLocationGuided(
    guide: Guide<PayloadT>,
): BinaryTree.Location<PayloadT, MetadataT> = this.findLocationGuided(
    location = BinaryTree.RootLocation,
    guide = guide,
)

private tailrec fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.findLocationGuided(
    location: BinaryTree.Location<PayloadT, MetadataT>,
    guide: Guide<PayloadT>,
): BinaryTree.Location<PayloadT, MetadataT> {
    val nodeHandle = resolve(
        location = location,
    ) ?: return location

    val payload = getPayload(
        nodeHandle = nodeHandle,
    )

    val instruction = guide.instruct(
        payload = payload,
    )

    when (instruction) {
        Guide.StopInstruction -> return location

        is Guide.TurnInstruction -> {
            val childLocation = nodeHandle.getChildLocation(
                side = instruction.side,
            )

            return findLocationGuided(
                location = childLocation,
                guide = guide,
            )
        }
    }
}

fun <PayloadT : Comparable<PayloadT>, MetadataT> BinaryTree<PayloadT, MetadataT>.find(
    payload: PayloadT,
): BinaryTree.Location<PayloadT, MetadataT> = findBy(
    payload = payload,
    comparator = naturalOrder(),
)

fun <PayloadT, MetadataT> BinaryTree<PayloadT, MetadataT>.findBy(
    payload: PayloadT,
    comparator: Comparator<PayloadT>,
): BinaryTree.Location<PayloadT, MetadataT> = findLocationGuided(
    guide = ComparatorGuide(
        comparator = comparator,
        locatedPayload = payload,
    ),
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getRandomFreeLocation(
    random: Random,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    guide = RandomGuide(
        random = random,
    ),
)

fun Random.nextSide(): BinaryTree.Side = when (nextBoolean()) {
    true -> BinaryTree.Side.Left
    false -> BinaryTree.Side.Right
}
