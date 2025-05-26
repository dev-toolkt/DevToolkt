package dev.toolkt.geometry

/**
 * An object that is solely based on the concept of rotation (can be reduced to
 * an angle) and can be compared to another object with some radial tolerance.
 */
interface RadialObject : GeometricObject {
    override fun equalsWithGeometricTolerance(
        other: GeometricObject,
        tolerance: GeometricObject.GeometricTolerance,
    ): Boolean = when {
        other !is RadialObject -> false

        else -> equalsWithRadialTolerance(
            other,
            tolerance = tolerance.radialTolerance,
        )
    }

    fun equalsWithRadialTolerance(
        other: RadialObject,
        tolerance: RelativeAngle.RadialTolerance = RelativeAngle.RadialTolerance.default,
    ): Boolean
}
