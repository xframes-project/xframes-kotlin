package dev.xframes

import org.koin.java.KoinJavaComponent.inject

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ComponentAnn

open class Component(
    val id: Int,
    val type: String,
    val props: Map<String, Any?> = emptyMap()
) {
    // Lifecycle method for initialization
    open fun componentDidMount() {}

    // Lifecycle method for re-rendering when props or state change
    open fun componentDidUpdate() {}

    // Render the component to a ComponentNode
    open fun render(): ComponentNode {
        return ComponentNode(id, type, props)
    }
}

class ButtonComponent(id: Int, type: String, props: Map<String, Any?> = emptyMap()) : Component(id, type, props) {
    var label: String = props["label"] as? String ?: ""



    // Override render to handle button specific rendering
    override fun render(): ComponentNode {
        return ComponentNode(
            id = id,
            type = type,
            props = mapOf("label" to label)
        )
    }
}

class CounterComponent(private val counterService: CounterService) {
    suspend fun render() {
        // Listen for state changes
        counterService.state.collect { state ->
            println("Counter: ${state.count}")
        }
    }

    fun increment() {
        counterService.increment() // Update the state
    }
}
