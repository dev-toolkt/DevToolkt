package dev.toolkt.geometry

data class Line(
    val representativePoint: Point,
    val orientation: Orientation,
) {
    companion object {
        fun throughPoints(
            point0: Point,
            point1: Point,
        ): Line? {
            TODO("Not yet implemented")
        }
    }
}
