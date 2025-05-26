package dev.toolkt.geometry

interface GeometricObject {
    data class GeometricTolerance(
        val spatialTolerance: SpatialObject.SpatialTolerance,
        val radialTolerance: RelativeAngle.RadialTolerance,
    ) {
        companion object {
            val default = GeometricTolerance(
                spatialTolerance = SpatialObject.SpatialTolerance.default,
                radialTolerance = RelativeAngle.RadialTolerance.default,
            )
        }
    }

    fun equalsWithGeometricTolerance(
        other: GeometricObject,
        tolerance: GeometricTolerance,
    ): Boolean
}
