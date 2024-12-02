package dev.xframes

open class Component(
    val id: Int,
    val type: String,
    val props: Map<String, Any?> = emptyMap()
) {
    var state: Map<String, Any?> = emptyMap()

    // Lifecycle method for initialization
    open fun componentDidMount() {}

    // Lifecycle method for re-rendering when props or state change
    open fun componentDidUpdate() {}

    // Render the component to a ComponentNode
    open fun render(): ComponentNode {
        return ComponentNode(id, type, props)
    }

    // Function to set state and trigger a re-render
    fun updateState(newState: Map<String, Any?>) {
        state = newState
        componentDidUpdate()  // Trigger componentDidUpdate on state change
    }
}

class ViewComponent(
    id: Int,
    type: String,
    props: Map<String, Any?> = emptyMap()
) : Component(id, type, props) {
    val children = mutableListOf<Component>()

    // Adding a child component
    fun addChild(child: Component) {
        children.add(child)
    }

    // Override render to render children as well
    override fun render(): ComponentNode {
        val childNodes = children.map { it.render() }
        return ComponentNode(id, type, props, childNodes)
    }
}

class ButtonComponent(id: Int, type: String, props: Map<String, Any?> = emptyMap()) : Component(id, type, props) {
    var label: String = props["label"] as? String ?: "Click me"

    // Handle button click
    fun onClick() {
        // You can update state here, which triggers re-render
        updateState(mapOf("clicked" to true))
    }

    // Override render to handle button specific rendering
    override fun render(): ComponentNode {
        return ComponentNode(
            id = id,
            type = type,
            props = mapOf("label" to label),
            children = listOf()
        )
    }
}
