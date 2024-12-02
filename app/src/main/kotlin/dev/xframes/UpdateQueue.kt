package dev.xframes

class UpdateQueue {
    private val queue: MutableList<Update> = mutableListOf()
    private var isFlushing = false  // Prevents multiple flushes at the same time

    // Add an update to the queue
    fun enqueue(update: Update) {
        queue.add(update)
    }

    // Flush the queue, applying all updates at once
    fun flush() {
        if (isFlushing) return
        isFlushing = true

        try {
            // Process all updates in the queue
            while (queue.isNotEmpty()) {
                val update = queue.removeAt(0)
                applyUpdate(update)
            }
        } finally {
            isFlushing = false
        }
    }

    // Apply a single update (this could trigger re-renders, lifecycle calls, etc.)
    private fun applyUpdate(update: Update) {
        when (update) {
            is Update.ReplaceChild -> applyReplace(update.oldNode, update.newNode)
            is Update.UpdateProps -> applyProps(update.props)
            is Update.AddChild -> applyAddChild(update.child)
            is Update.RemoveChild -> applyRemoveChild(update.child)
            is Update.Mount -> applyMount(update.node)
            is Update.UpdateLifecycle -> applyUpdateLifecycle(update.node)
            is Update.Unmount -> applyUnmount(update.node)
        }
    }

    private fun applyReplace(oldNode: ComponentNode, newNode: ComponentNode) {
        // Perform the node replacement (re-rendering, lifecycle updates)
        println("Replacing node with id: ${oldNode.id} with a new node")
    }

    private fun applyProps(props: Map<String, Any?>) {
        // Apply property changes
        println("Applying props: $props")
    }

    private fun applyAddChild(child: ComponentNode) {
        // Add child component
        println("Adding child with id: ${child.id}")
    }

    private fun applyRemoveChild(child: ComponentNode) {
        // Remove child component
        println("Removing child with id: ${child.id}")
    }

    private fun applyMount(node: ComponentNode) {
        // Trigger component mount lifecycle method
        println("Mounting node with id: ${node.id}")
    }

    private fun applyUpdateLifecycle(node: ComponentNode) {
        // Trigger lifecycle update
        println("Updating lifecycle of node with id: ${node.id}")
    }

    private fun applyUnmount(node: ComponentNode) {
        // Trigger component unmount lifecycle method
        println("Unmounting node with id: ${node.id}")
    }
}
