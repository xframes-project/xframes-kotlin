package dev.xframes

interface AllCallbacks {
    fun onInit()
    fun onTextChanged(id: Int, text: String)
    fun onComboChanged(id: Int, value: Int)
    fun onNumericValueChanged(id: Int, value: Float)
    fun onBooleanValueChanged(id: Int, value: Boolean)
    fun onMultipleNumericValuesChanged(id: Int, values: FloatArray)
    fun onClick(id: Int)
}