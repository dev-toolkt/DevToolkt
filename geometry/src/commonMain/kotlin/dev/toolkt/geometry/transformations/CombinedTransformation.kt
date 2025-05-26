package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.core.numeric.NumericObject

data class CombinedTransformation(
    override val standaloneTransformations: List<StandaloneTransformation>,
) : EffectiveTransformation() {
    override fun transform(
        point: Point,
    ): Point = standaloneTransformations.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    override fun combineWith(
        laterTransformations: List<StandaloneTransformation>,
    ): CombinedTransformation = CombinedTransformation(
        standaloneTransformations = standaloneTransformations + laterTransformations,
    )

    companion object;

    override fun toSvgTransformationString(): String =
        standaloneTransformations.reversed().joinToString(separator = " ") { transformation ->
            transformation.toSvgTransformationString()
        }

    override val toUniversal: PrimitiveTransformation.Universal
        get() = standaloneTransformations.fold(
            initial = Identity.toUniversal,
        ) { acc, transformation ->
            acc.mixWith(
                laterTransform = transformation.toUniversal,
            )
        }

    override val primitiveTransformations: List<PrimitiveTransformation>
        get() = standaloneTransformations.flatMap { it.primitiveTransformations }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
