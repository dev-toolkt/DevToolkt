package dev.toolkt.geometry.transformations

sealed class StandaloneTransformation : EffectiveTransformation() {
    final override val standaloneTransformations: List<StandaloneTransformation>
        get() = listOf(this)

    final override fun combineWith(
        laterTransformations: List<StandaloneTransformation>,
    ): CombinedTransformation = CombinedTransformation(
        standaloneTransformations = listOf(this) + laterTransformations,
    )

    abstract fun invert(): StandaloneTransformation
}
