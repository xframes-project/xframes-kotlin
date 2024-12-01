package dev.xframes

class XFramesWrapper {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    println(XFramesWrapper().greeting)
}
