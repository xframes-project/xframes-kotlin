import dev.xframes.ComponentNode
import dev.xframes.Update
import dev.xframes.diffChildren
import kotlin.test.*
import org.junit.jupiter.api.Test

class DiffChildrenTest {

    @Test
    fun `test diffChildren with matching children`() {
        val oldChildren = listOf(
            ComponentNode(1, "node"),
            ComponentNode(2, "text")
        )
        val newChildren = listOf(
            ComponentNode(1, "node"),
            ComponentNode(2,"text")
        )

        val updates = diffChildren(oldChildren, newChildren)
        assertTrue(updates.isEmpty(), "There should be no updates when the children are identical")
    }

    @Test
    fun `test diffChildren with more old children than new children`() {
        val oldChildren = listOf(
            ComponentNode(1, "node"),
            ComponentNode(2, "node"),
            ComponentNode(3, "text")
        )
        val newChildren = listOf(
            ComponentNode(1, "node"),
            ComponentNode(2, "node")
        )

        val updates = diffChildren(oldChildren, newChildren)
        assertEquals(1, updates.size, "There should be one update to remove a child")
        assertTrue(updates[0] is Update.RemoveChild, "The update should be a RemoveChild")
    }

    @Test
    fun `test diffChildren with more new children than old children`() {
        val oldChildren = listOf(
            ComponentNode(1, "node"),
            ComponentNode(2, "node")
        )
        val newChildren = listOf(
            ComponentNode(1, "node"),
            ComponentNode(2, "node"),
            ComponentNode(3, "text")
        )

        val updates = diffChildren(oldChildren, newChildren)
        assertEquals(1, updates.size, "There should be one update to add a child")
        assertTrue(updates[0] is Update.AddChild, "The update should be an AddChild")
    }

    @Test
    fun `test diffChildren with no children in both lists`() {
        val oldChildren = emptyList<ComponentNode>()
        val newChildren = emptyList<ComponentNode>()

        val updates = diffChildren(oldChildren, newChildren)
        assertTrue(updates.isEmpty(), "There should be no updates when both lists are empty")
    }

    @Test
    fun `test diffChildren with one child in each list, different types`() {
        val oldChildren = listOf(
            ComponentNode(1, "div")  // Old component is a div
        )
        val newChildren = listOf(
            ComponentNode(1, "span")  // New component is a span
        )

        val updates = diffChildren(oldChildren, newChildren)

        // Expect one ReplaceChild update
        assertEquals(1, updates.size, "There should be one update to replace the child")

        // Ensure that the update is ReplaceChild
        assertTrue(updates[0] is Update.ReplaceChild, "The update should be a ReplaceChild")

        // Ensure the old and new child components are correctly assigned in ReplaceChild
        val replaceUpdate = updates[0] as Update.ReplaceChild
        assertEquals(oldChildren[0], replaceUpdate.oldNode, "The old child should be the div")
        assertEquals(newChildren[0], replaceUpdate.newNode, "The new child should be the span")
    }

    @Test
    fun `test diffChildren with different props in matching children`() {
        val oldChildren = listOf(
            ComponentNode(1, "div", mapOf("style" to "color:red"))
        )
        val newChildren = listOf(
            ComponentNode(1, "div", mapOf("style" to "color:blue"))
        )

        val updates = diffChildren(oldChildren, newChildren)

        // Expect one Update.UpdateProps because only props changed
        assertEquals(1, updates.size, "There should be one update to modify the props")

        // Ensure the update is of type Update.UpdateProps
        assertTrue(updates[0] is Update.UpdateProps, "The update should be an UpdateProps")

        // Check that the props have changed as expected
        val updateProps = updates[0] as Update.UpdateProps
        assertEquals(mapOf("style" to "color:blue"), updateProps.props, "The props should be updated")
    }
}
