package dev.toolkt.dom.pure.input

sealed class PureInputType(val type: String) {
    data object Text : PureInputType("text")
    data object Password : PureInputType("password")
    data object Email : PureInputType("email")
    data object Number : PureInputType("number")
    data object Date : PureInputType("date")
    data object Time : PureInputType("time")
    data object Checkbox : PureInputType("checkbox")
    data object Radio : PureInputType("radio")
    data object File : PureInputType("file")
    data object Hidden : PureInputType("hidden")
    data object Button : PureInputType("button")
    data object Submit : PureInputType("submit")
    data object Reset : PureInputType("reset")
    data object Color : PureInputType("color")
}
