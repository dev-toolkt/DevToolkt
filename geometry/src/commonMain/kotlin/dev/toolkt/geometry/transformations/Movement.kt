package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject

data class Movement(
    val rotation: PrimitiveTransformation.Rotation,
    val translation: PrimitiveTransformation.Translation,
) : ComplexTransformation() {
    override fun invert(): Movement {
        val invertedRotation = rotation.invert()

        return Movement(
            rotation = invertedRotation,
            translation = translation.invert().rotate(invertedRotation),
        )
    }

    override val toUniversal: PrimitiveTransformation.Universal
        get() = rotation.toUniversal.copy(
            tx = translation.tx,
            ty = translation.ty,
        )

    override val primitiveTransformations: List<PrimitiveTransformation> = listOf(
        rotation,
        translation,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
