package dev.xframes

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class Node(
    val id: Int,
    val type: String,
    val root: Boolean? = null,
    val text: String? = null
)

object MyCallbackHandler : AllCallbacks {
    lateinit var xframes: XFramesWrapper

    // Initialization block for singleton setup
    fun initialize(xframesWrapper: XFramesWrapper) {
        xframes = xframesWrapper
    }

    override fun onInit() {
        println("Initialization callback called!")

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val nodeAdapter = moshi.adapter(Node::class.java)
        val childrenAdapter = moshi.adapter(List::class.java).lenient()

        val rootNode = Node(
            id = 0,
            type = "node",
            root = true
        )
        val rootNodeJson = nodeAdapter.toJson(rootNode)

        val textNode = Node(
            id = 1,
            type = "unformatted-text",
            text = "Hello, world!"
        )
        val textNodeJson = nodeAdapter.toJson(textNode)

        val children = listOf(1)
        val childrenJson = childrenAdapter.toJson(children)

        xframes.setElement(rootNodeJson)
        xframes.setElement(textNodeJson)
        if (childrenJson != null) {
            xframes.setChildren(0, childrenJson)
        }
    }

    override fun onTextChanged(id: Int, text: String) {
        println("Text changed (ID: $id, Text: $text)")
    }

    override fun onComboChanged(id: Int, value: Int) {
        println("Combo changed (ID: $id, Value: $value)")
    }

    override fun onNumericValueChanged(id: Int, value: Float) {
        println("Numeric value changed (ID: $id, Value: $value)")
    }

    override fun onBooleanValueChanged(id: Int, value: Boolean) {
        println("Boolean value changed (ID: $id, Value: $value)")
    }

    override fun onMultipleNumericValuesChanged(id: Int, values: FloatArray) {
        print("Multiple numeric values changed (ID: $id, Values: ")
        values.forEach { value -> print("$value ") }
        println(")")
    }

    override fun onClick(id: Int) {
        println("Click callback (ID: $id)")
    }
}
