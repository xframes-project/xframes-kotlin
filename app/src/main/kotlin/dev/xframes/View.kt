package dev.xframes

// Define the View class with a declarative structure
class View(id: Int, type: String, props: Map<String, Any?> = emptyMap()) : Component(id, type, props) {
    val children = mutableListOf<Component>()

    // Function to add children declaratively
    fun child(id: Int, type: String, props: Map<String, Any?> = emptyMap(), content: View.() -> Unit = {}) {
        val child = View(id, type, props)
        child.content()  // Allow child to define its own children
        children.add(child)
    }

    // Override the render method to return the ComponentNode
    override fun render(): ComponentNode {
        val childNodes = children.map { it.render() }
        return ComponentNode(id, type, props, childNodes)
    }
}

fun view(id: Int, type: String, props: Map<String, Any?> = emptyMap(), content: View.() -> Unit): View {
    val view = View(id, type, props)
    view.content()  // Let the block define the children
    return view
}
