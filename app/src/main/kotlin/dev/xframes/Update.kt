package dev.xframes

sealed class Update {
    enum class Priority { HIGH, LOW }

    abstract val priority: Priority

    // Represents replacing an old component with a new one
    data class ReplaceChild(
        val oldNode: ComponentNode,
        val newNode: ComponentNode,
        override val priority: Priority = Priority.LOW
    ) : Update()

    // Represents a props update
    data class UpdateProps(
        val props: Map<String, Any?>,
        override val priority: Priority = Priority.LOW
    ) : Update()

    // Represents adding a child node
    data class AddChild(
        val child: ComponentNode,
        override val priority: Priority = Priority.LOW
    ) : Update()

    // Represents removing a child node
    data class RemoveChild(
        val child: ComponentNode,
        override val priority: Priority = Priority.LOW
    ) : Update()

    // Represents mounting a node
    data class Mount(
        val node: ComponentNode,
        override val priority: Priority = Priority.HIGH
    ) : Update()

    // Represents lifecycle updates for a node
    data class UpdateLifecycle(
        val node: ComponentNode,
        override val priority: Priority = Priority.LOW
    ) : Update()

    // Represents unmounting a node
    data class Unmount(
        val node: ComponentNode,
        override val priority: Priority = Priority.LOW
    ) : Update()
}