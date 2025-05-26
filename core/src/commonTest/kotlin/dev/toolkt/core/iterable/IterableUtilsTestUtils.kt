package dev.toolkt.core.iterable

sealed interface DominoBlockRed {
    val redNumber: Int
}

sealed interface DominoBlockBlue {
    val blueNumber: Int
}

data class RedHalfDominoBlock(
    override val redNumber: Int,
) : DominoBlockRed

data class FullDominoBlock(
    override val redNumber: Int,
    override val blueNumber: Int,
) : DominoBlockRed, DominoBlockBlue

data class BlueHalfDominoBlock(
    override val blueNumber: Int,
) : DominoBlockBlue
