package dev.toolkt.core

data class Sample<A, B>(
    val argument: A,
    val value: B,
)

fun <A, B> Function1<A, B>.invokeSampling(p1: A): Sample<A, B> = Sample(
    p1,
    this(p1),
)
