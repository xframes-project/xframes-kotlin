package dev.xframes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.squareup.moshi.JsonAdapter

object MyCallbackHandler : AllCallbacks {
    lateinit var xframes: XFramesWrapper
    lateinit var composition: Composition
    lateinit var App: @Composable () -> Unit
    lateinit var jsonAdapter: JsonAdapter<WidgetNode>

    // Initialization block for singleton setup
    fun initialize(xframesWrapper: XFramesWrapper, jsonAdapter: JsonAdapter<WidgetNode>, composition: Composition, App: @Composable () -> Unit) {
        this.xframes = xframesWrapper
        this.composition = composition
        this.App = App
        this.jsonAdapter = jsonAdapter
    }

    override fun onInit() {
        xframes.setElement(jsonAdapter.toJson(widgetRegistrationService.getWidgetById(0)))

        composition.setContent {
            App()
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
