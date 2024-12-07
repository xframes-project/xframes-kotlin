package dev.xframes

import androidx.compose.runtime.*
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.util.concurrent.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

@JsonClass(generateAdapter = true)
data class FontDefinition(val name: String, val size: Int)

@JsonClass(generateAdapter = true)
data class FontDefinitions(val defs: List<FontDefinition>)

@JsonClass(generateAdapter = true)
data class ColorValue(val color: String, val alpha: Int)

@JsonClass(generateAdapter = true)
data class Theme(val colors: Map<Int, List<Any>>)

class XFramesWrapper {
    external fun setElement(elementJson: String)
    external fun setChildren(parentId: Int, childrenJson: String)
    external fun init(
        assetsBasePath: String,
        rawFontDefinitions: String,
        rawStyleOverrideDefinitions: String,
        allCallbacks: AllCallbacks
    )

    companion object {
        init {
            System.loadLibrary("xframesjni")
        }
    }

    // Enum for ImGui colors
    enum class ImGuiCol(val value: Int) {
        Text(0), TextDisabled(1), WindowBg(2), ChildBg(3), PopupBg(4),
        Border(5), BorderShadow(6), FrameBg(7), FrameBgHovered(8), FrameBgActive(9),
        TitleBg(10), TitleBgActive(11), TitleBgCollapsed(12), MenuBarBg(13),
        ScrollbarBg(14), ScrollbarGrab(15), ScrollbarGrabHovered(16),
        ScrollbarGrabActive(17), CheckMark(18), SliderGrab(19), SliderGrabActive(20),
        Button(21), ButtonHovered(22), ButtonActive(23), Header(24),
        HeaderHovered(25), HeaderActive(26), Separator(27), SeparatorHovered(28),
        SeparatorActive(29), ResizeGrip(30), ResizeGripHovered(31), ResizeGripActive(32),
        Tab(33), TabHovered(34), TabActive(35), TabUnfocused(36),
        TabUnfocusedActive(37), PlotLines(38), PlotLinesHovered(39), PlotHistogram(40),
        PlotHistogramHovered(41), TableHeaderBg(42), TableBorderStrong(43),
        TableBorderLight(44), TableRowBg(45), TableRowBgAlt(46),
        TextSelectedBg(47), DragDropTarget(48), NavHighlight(49),
        NavWindowingHighlight(50), NavWindowingDimBg(51), ModalWindowDimBg(52),
        COUNT(53)
    }
}

data class WidgetNode(
    var type: String,
    var props: Map<String, Any?> = emptyMap(),
    val children: MutableList<WidgetNode> = mutableListOf()
)

class WidgetTreeApplier(root: WidgetNode) : AbstractApplier<WidgetNode>(root) {
    override fun onClear() {
        println("onClear")
        current.children.clear()
    }

    override fun insertBottomUp(index: Int, instance: WidgetNode) {
        println("insertBottomUp")
        current.children.add(index, instance)
    }

    override fun insertTopDown(index: Int, instance: WidgetNode) {
        println("insertTopDown")
        current.children.add(index, instance)
    }

    override fun move(from: Int, to: Int, count: Int) {
        println("move")
        val moved = current.children.subList(from, from + count)
        current.children.removeAll(moved)
        current.children.addAll(to, moved)
    }

    override fun remove(index: Int, count: Int) {
        println("remove")
        current.children.subList(index, index + count).clear()
    }
}

@Composable
fun WidgetNodeComposable(type: String, props: Map<String, Any?> = emptyMap(), content: @Composable () -> Unit = {}) {
    ComposeNode<WidgetNode, WidgetTreeApplier>(
        factory = { WidgetNode(type, props) },
        update = {
            set(type) { this.type = type }
            set(props) { this.props = props }
        },
        content = content
    )
}

fun buildWidgetTree(): WidgetNode {
    val root = WidgetNode("root")
    val applier = WidgetTreeApplier(root)
    val composition = Composition(applier, Recomposer(MainScope().coroutineContext))

    composition.setContent {
        WidgetNodeComposable("Container") {
            WidgetNodeComposable("Button", mapOf("text" to "Click Me"))
        }
    }

    return root
}





// Main function defined outside the class
fun main() {
    buildWidgetTree()

    val xframes = XFramesWrapper()

    MyCallbackHandler.initialize(xframes)

    // Initialize with paths and callbacks
    xframes.init("../assets", getFontDefinitions(), getStyleOverrides(), MyCallbackHandler)

    // Start periodic task
    keepProcessRunning()
}

// Keeps the process running periodically
fun keepProcessRunning() {
    val scheduler = Executors.newScheduledThreadPool(1)

    val task = Runnable {
        // Simulate periodic work (e.g., call native methods if needed)
        // xframes.setElement("someElementJson")
    }

    // Schedule the task every second
    scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS)
}

// Returns font definitions as a JSON string
fun getFontDefinitions(): String {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(FontDefinitions::class.java)

    val sizes = listOf(16, 18, 20, 24, 28, 32, 36, 48)
    val definitions = sizes.map { FontDefinition("roboto-regular", it) }
    val fontDefinitions = FontDefinitions(definitions)

    return adapter.toJson(fontDefinitions)
}

// Returns style overrides as a JSON string
fun getStyleOverrides(): String {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(Theme::class.java)

    val theme2Colors = mapOf(
        "darkestGrey" to "#141f2c",
        "darkerGrey" to "#2a2e39",
        "darkGrey" to "#363b4a",
        "lightGrey" to "#5a5a5a",
        "lighterGrey" to "#7A818C",
        "evenLighterGrey" to "#8491a3",
        "black" to "#0A0B0D",
        "green" to "#75f986",
        "red" to "#ff0062",
        "white" to "#fff"
    )

    val colorMap = mapOf(
        XFramesWrapper.ImGuiCol.Text.value to listOf(theme2Colors["white"]!!, 1),
        XFramesWrapper.ImGuiCol.TextDisabled.value to listOf(theme2Colors["lighterGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.WindowBg.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.ChildBg.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.PopupBg.value to listOf(theme2Colors["white"]!!, 1),
        XFramesWrapper.ImGuiCol.Border.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.BorderShadow.value to listOf(theme2Colors["darkestGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.FrameBg.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.FrameBgHovered.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.FrameBgActive.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TitleBg.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TitleBgActive.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TitleBgCollapsed.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.MenuBarBg.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ScrollbarBg.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ScrollbarGrab.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ScrollbarGrabHovered.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ScrollbarGrabActive.value to listOf(theme2Colors["darkestGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.CheckMark.value to listOf(theme2Colors["darkestGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.SliderGrab.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.SliderGrabActive.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.Button.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.ButtonHovered.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ButtonActive.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.Header.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.HeaderHovered.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.HeaderActive.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.Separator.value to listOf(theme2Colors["darkestGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.SeparatorHovered.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.SeparatorActive.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ResizeGrip.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.ResizeGripHovered.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ResizeGripActive.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.Tab.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.TabHovered.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TabActive.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TabUnfocused.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.TabUnfocusedActive.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.PlotLines.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.PlotLinesHovered.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.PlotHistogram.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.PlotHistogramHovered.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TableHeaderBg.value to listOf(theme2Colors["black"]!!, 1),
        XFramesWrapper.ImGuiCol.TableBorderStrong.value to listOf(theme2Colors["lightGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TableBorderLight.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TableRowBg.value to listOf(theme2Colors["darkGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TableRowBgAlt.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.TextSelectedBg.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.DragDropTarget.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.NavHighlight.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.NavWindowingHighlight.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.NavWindowingDimBg.value to listOf(theme2Colors["darkerGrey"]!!, 1),
        XFramesWrapper.ImGuiCol.ModalWindowDimBg.value to listOf(theme2Colors["darkerGrey"]!!, 1)
    )

    val theme = Theme(colors = colorMap)
    return adapter.toJson(theme)
}
