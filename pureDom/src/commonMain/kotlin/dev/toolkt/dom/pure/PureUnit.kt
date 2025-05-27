package dev.toolkt.dom.pure

sealed class PureUnit {
    sealed class Absolute : PureUnit() {
        companion object {
            fun parse(
                unitString: String,
            ): PureUnit = when (unitString) {
                Mm.string -> Mm
                Pt.string -> Pt
                Px.string -> Px
                else -> throw UnsupportedOperationException("Unsupported unit: $unitString")
            }
        }

        fun per(
            other: Absolute,
        ): Double = other.perInverse(this)

        /**
         * @return [unit] per this unit constant
         */
        protected abstract fun perInverse(unit: Absolute): Double

        abstract val perMm: Double

        abstract val perInch: Double

        abstract val perPt: Double

        abstract val perPx: Double
    }

    data object Mm : Absolute() {
        override fun perInverse(unit: Absolute): Double = unit.perMm

        override val perMm = 1.0

        override val perInch = 25.4

        override val perPt = Mm.perInch / Pt.perInch

        override val perPx = Mm.perInch / Px.perInch

        override val string: String = "mm"
    }

    data object Inch : Absolute() {
        override fun perInverse(unit: Absolute): Double = unit.perInch

        override val perInch = 1.0

        override val perMm = 1.0 / Mm.perInch

        override val perPt = 1.0 / Pt.perInch

        override val perPx = 1.0 / Px.perInch

        override val string: String = "in"
    }

    data object Pt : Absolute() {
        override fun perInverse(unit: Absolute): Double = unit.perPt

        override val perPt = 1.0

        // 1 inch equals 72 pt per Web standards
        override val perInch = 72.0

        override val perPx = Pt.perInch / Px.perInch

        override val perMm = Pt.perInch / Mm.perInch

        override val string: String = "pt"
    }

    data object Px : Absolute() {
        override fun perInverse(unit: Absolute): Double = unit.perPx

        override val perPx: Double = 1.0

        // 1 inch _typically_ equals 96 px, but this is much less obvious
        override val perInch = 96.0

        override val perMm = Px.perInch / Mm.perInch

        override val perPt = Px.perInch / Pt.perInch

        override val string: String = "px"
    }

    data object Percent : PureUnit() {
        override val string: String = "%"
    }

    companion object {
        fun parse(
            unitString: String,
        ): PureUnit = when (unitString) {
            Percent.string -> Percent

            else -> Absolute.parse(
                unitString = unitString,
            )
        }
    }

    abstract val string: String
}