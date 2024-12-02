package dev.xframes

data class ComponentNode(
    val id: Int,  // Unique identifier for the component
    val type: String,  // Component type (could be a class name or identifier)
    val props: Map<String, Any?>? = null,  // Component properties (state, configuration)
    val children: List<ComponentNode> = emptyList(),
    val key: String? = null,
    val componentLifecycle: ComponentLifecycle = ComponentLifecycle()
) {
    constructor(id: Int, type: String) : this(
        id = id,
        type = type,
        props = null
    )
}
