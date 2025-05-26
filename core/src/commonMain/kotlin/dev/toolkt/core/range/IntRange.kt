package dev.toolkt.core.range

fun IntRange.Companion.empty(value: Int): IntRange = value until value

fun IntRange.Companion.single(value: Int): IntRange = value..value
