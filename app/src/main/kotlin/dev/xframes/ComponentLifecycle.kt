package dev.xframes

data class ComponentLifecycle(
    var mounted: Boolean = false,  // Whether the component is mounted
    var updated: Boolean = false   // Whether the component was updated
)
