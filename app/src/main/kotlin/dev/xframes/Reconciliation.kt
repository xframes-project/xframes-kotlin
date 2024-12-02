package dev.xframes

fun reconcile(oldNode: ComponentNode, newNode: ComponentNode, updateQueue: UpdateQueue) {
    // If the id or type is different, replace the node
    if (oldNode.id != newNode.id) {
        updateQueue.enqueue(Update.ReplaceChild(oldNode, newNode))
    } else {
        // Diff props (check for changes)
        val propUpdates = oldNode.props?.let { newNode.props?.let { it1 -> diffProps(it, it1) } }
        if (propUpdates != null) {
            if (propUpdates.isNotEmpty()) {
                updateQueue.enqueue(Update.UpdateProps(propUpdates))
            }
        }

        // Diff children (check for added, removed, or updated children)
        val childUpdates = diffChildren(oldNode.children, newNode.children)
        childUpdates.forEach { updateQueue.enqueue(it) }

        // Handle lifecycle updates
        if (!oldNode.componentLifecycle.mounted && newNode.componentLifecycle.mounted) {
            updateQueue.enqueue(Update.Mount(newNode))
        }
        if (oldNode.componentLifecycle.mounted && newNode.componentLifecycle.updated) {
            updateQueue.enqueue(Update.UpdateLifecycle(newNode))
        }
        if (oldNode.componentLifecycle.mounted && !newNode.componentLifecycle.mounted) {
            updateQueue.enqueue(Update.Unmount(oldNode))
        }
    }
}

fun diffProps(oldProps: Map<String, Any?>, newProps: Map<String, Any?>): Map<String, Any?> {
    val diff = mutableMapOf<String, Any?>()
    for (key in oldProps.keys) {
        if (oldProps[key] != newProps[key]) {
            diff[key] = newProps[key] ?: null
        }
    }
    for (key in newProps.keys) {
        if (!oldProps.containsKey(key)) {
            diff[key] = newProps[key]
        }
    }
    return diff
}

fun diffChildren(oldChildren: List<ComponentNode>, newChildren: List<ComponentNode>): List<Update> {
    val updates = mutableListOf<Update>()
    val maxLength = maxOf(oldChildren.size, newChildren.size)

    for (i in 0 until maxLength) {
        val oldChild = if (i < oldChildren.size) oldChildren[i] else null
        val newChild = if (i < newChildren.size) newChildren[i] else null

        when {
            oldChild != null && newChild != null -> {
                // If the types are different, replace the old child
                if (oldChild.type != newChild.type) {
                    updates.add(Update.ReplaceChild(oldChild, newChild))
                }
                // If the props are different, update the props
                else if (oldChild.props != newChild.props) {
                    newChild.props?.let { Update.UpdateProps(it) }?.let { updates.add(it) }
                }
            }
            oldChild == null && newChild != null -> {
                // New child exists in `newChildren` but not in `oldChildren`
                updates.add(Update.AddChild(newChild))
            }
            oldChild != null && newChild == null -> {
                // Child exists in `oldChildren` but not in `newChildren`
                updates.add(Update.RemoveChild(oldChild))
            }
        }
    }

    return updates
}

fun requestFlush(updateQueue: UpdateQueue) {
    // In a real system, you'd want to flush based on some condition (e.g., a timer or user action)
    println("Flushing updates...")
    updateQueue.flush()
}

