package dev.toolkt.dom.pure

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance

data class PureDimension<out U : PureUnit>(
    val value: Double,
    val unit: U,
) : NumericObject {
    companion object {
        private val regex = Regex("([0-9.]+)([a-zA-Z%]+)")

        fun parse(
            string: String,
        ): PureDimension<*> {
            val matchResult =
                regex.matchEntire(string) ?: throw IllegalArgumentException("Invalid dimension format: $string")

            val (valueString, unitString) = matchResult.destructured

            val value = valueString.toDouble()
            val unit = PureUnit.parse(unitString)

            return PureDimension(
                value = value,
                unit = unit,
            )
        }
    }

    fun toDimensionString(): String = "$value${unit.string}"

    val asAbsolute: PureDimension<PureUnit.Absolute>?
        get() {
            val absoluteUnit = unit as? PureUnit.Absolute ?: return null

            return PureDimension(
                value = value,
                unit = absoluteUnit,
            )
        }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is PureDimension<*> -> false
        !value.equalsWithTolerance(other.value, tolerance = tolerance) -> false
        unit != other.unit -> false
        else -> true
    }
}

fun <U : PureUnit.Absolute> PureDimension<PureUnit.Absolute>.inUnit(
    otherUnit: U,
): PureDimension<U> = PureDimension(
    value = value * otherUnit.per(unit),
    unit = otherUnit,
)

val Double.mm: PureDimension<PureUnit.Mm>
    get() = PureDimension(
        value = this,
        unit = PureUnit.Mm,
    )

val Int.mm: PureDimension<PureUnit.Mm>
    get() = this.toDouble().mm

val Double.inch: PureDimension<PureUnit.Inch>
    get() = PureDimension(
        value = this,
        unit = PureUnit.Inch,
    )

val Int.inch: PureDimension<PureUnit.Inch>
    get() = this.toDouble().inch

val Double.pt: PureDimension<PureUnit.Pt>
    get() = PureDimension(
        value = this,
        unit = PureUnit.Pt,
    )

val Int.pt: PureDimension<PureUnit.Pt>
    get() = this.toDouble().pt


val Double.px: PureDimension<PureUnit.Px>
    get() = PureDimension(
        value = this,
        unit = PureUnit.Px,
    )

val Int.px: PureDimension<PureUnit.Px>
    get() = this.toDouble().px

val Double.percent: PureDimension<PureUnit.Percent>
    get() = PureDimension(
        value = this,
        unit = PureUnit.Percent,
    )

val Int.percent: PureDimension<PureUnit.Percent>
    get() = this.toDouble().percent

val Double.vw: PureDimension<PureUnit.Vw>
    get() = PureDimension(
        value = this,
        unit = PureUnit.Vw,
    )

val Int.vw: PureDimension<PureUnit.Vw>
    get() = this.toDouble().vw

val Double.vh: PureDimension<PureUnit.Vh>
    get() = PureDimension(
        value = this,
        unit = PureUnit.Vh,
    )

val Int.vh: PureDimension<PureUnit.Vh>
    get() = this.toDouble().vh
