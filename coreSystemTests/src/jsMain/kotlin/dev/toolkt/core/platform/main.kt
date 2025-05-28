package dev.toolkt.core.platform

suspend fun main() {
    println("Hello, system test!")
    val test = PlatformWeakMapSystemTest()
    test.runTest()
}
