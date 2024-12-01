package dev.xframes

import org.json.JSONArray
import org.json.JSONObject

object MyCallbackHandler : AllCallbacks {
    lateinit var xframes: XFramesWrapper

    // Initialization block for singleton setup
    fun initialize(xframesWrapper: XFramesWrapper) {
        xframes = xframesWrapper
    }

    override fun onInit() {
        println("Initialization callback called!")

        // Create root node
        val rootNode = JSONObject().apply {
            put("id", 0)
            put("type", "node")
            put("root", true)
        }

        // Create text node
        val textNode = JSONObject().apply {
            put("id", 1)
            put("type", "unformatted-text")
            put("text", "Hello, world!")
        }

        // Set elements
        xframes.setElement(rootNode.toString())
        xframes.setElement(textNode.toString())
        xframes.setChildren(0, JSONArray().put(1).toString())
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
