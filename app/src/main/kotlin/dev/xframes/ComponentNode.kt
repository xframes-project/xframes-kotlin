package dev.xframes

data class ComponentNode(
    val id: Int,
    val type: String,
    val props: Map<String, Any?>? = null,
    val children: List<ComponentNode> = emptyList(),
    val componentLifecycle: ComponentLifecycle = ComponentLifecycle()
) {
    constructor(id: Int, type: String) : this(
        id = id,
        type = type,
        props = null
    )
}
