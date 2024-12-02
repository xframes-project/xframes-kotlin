package dev.xframes



class ComponentService {
    private val clickListeners = mutableMapOf<Int, () -> Unit>()

    // Register a click listener for a component
    fun registerClickListener(componentId: Int, listener: () -> Unit) {
        clickListeners[componentId] = listener
    }

    // Trigger the click event for a component
    fun triggerClick(componentId: Int) {
        clickListeners[componentId]?.invoke()
    }
}