package dev.xframes

import androidx.compose.runtime.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.*
import kotlinx.coroutines.*
import java.util.concurrent.locks.ReentrantLock

@JsonClass(generateAdapter = true)
data class FontDefinition(val name: String, val size: Int)

@JsonClass(generateAdapter = true)
data class FontDefinitions(val defs: List<FontDefinition>)

@JsonClass(generateAdapter = true)
data class ColorValue(val color: String, val alpha: Int)

@JsonClass(generateAdapter = true)
data class Theme(val colors: Map<Int, List<Any>>)

class XFramesWrapper {
    external fun getStyle(): String
    external fun setElement(elementJson: String)
    external fun setChildren(parentId: Int, childrenJson: String)
    external fun elementInternalOp(id: Int, data: String)
    external fun appendTextToClippedMultiLineTextRenderer(id: Int, text: String)
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

data class JsonSetData(val op: String, val data: Any)
data class JsonSetValue(val op: String, val value: String)
data class JsonSetSelectedIndex(val op: String, val index: Int)
data class JsonResetData(val op: String)
data class JsonAppendData(val op: String, val data: Any)
data class JsonAppendDataToPlotLine(val op: String, val x: Float, val y: Float)
data class JsonSetAxesDecimalDigits(val op: String, val x: Float, val y: Float)
data class JsonSetAxesAutoFit(val op: String, val enabled: Boolean)

object widgetRegistrationService {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapterSetData: JsonAdapter<JsonSetData> = moshi.adapter(JsonSetData::class.java)
    val jsonAdapterSetSelectedIndex: JsonAdapter<JsonSetSelectedIndex> = moshi.adapter(JsonSetSelectedIndex::class.java)
    val jsonAdapterSetValue: JsonAdapter<JsonSetValue> = moshi.adapter(JsonSetValue::class.java)
    val jsonAdapterResetData: JsonAdapter<JsonResetData> = moshi.adapter(JsonResetData::class.java)
    val jsonAdapterAppendData: JsonAdapter<JsonAppendData> = moshi.adapter(JsonAppendData::class.java)
    val jsonAdapterAppendDataToPlotLine: JsonAdapter<JsonAppendDataToPlotLine> = moshi.adapter(JsonAppendDataToPlotLine::class.java)
    val jsonAdapterSetAxesDecimalDigits: JsonAdapter<JsonSetAxesDecimalDigits> = moshi.adapter(JsonSetAxesDecimalDigits::class.java)
    val jsonAdapterSetAxesAutoFit: JsonAdapter<JsonSetAxesAutoFit> = moshi.adapter(JsonSetAxesAutoFit::class.java)

    private lateinit var xFramesWrapper: XFramesWrapper

    private var lastWidgetId: Int = 0

    private val idGeneratorLock = ReentrantLock()
    private val idRegistrationLock = ReentrantLock()

    val widgetRegistry = mutableMapOf<Int, WidgetNode>()

    val onClickRegistry = mutableMapOf<Int, () -> Unit>()

    fun setXFramesWrapper(wrapper: XFramesWrapper) {
        this.xFramesWrapper = wrapper
    }

    fun getWidgetById(id: Int): WidgetNode? {
        return widgetRegistry[id]
    }

    // Update registry when widgets are created or updated
    fun registerWidget(id: Int, widget: WidgetNode) {
        idRegistrationLock.lock()
        widgetRegistry[id] = widget
        idRegistrationLock.unlock()
    }

    // Function to get the next widget ID
    fun getNextWidgetId(): Int {
        idGeneratorLock.lock() // Acquire the lock
        try {
            return lastWidgetId++
        } finally {
            idGeneratorLock.unlock() // Ensure the lock is released
        }
    }

    fun registerWidgetForOnClickEvent(id: Int, fn: () -> Unit) {
        this.onClickRegistry[id] = fn;
    }

    fun dispatchOnClickEvent(id: Int) {
        this.onClickRegistry[id]?.let { it() };
    }

    fun getStyle(): String {
        return this.xFramesWrapper.getStyle()
    }

    fun setData(id: Int, data: ArrayList<Any>) {
        val jsonData = JsonSetData("setData", data)
        jsonAdapterSetData.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }

    fun appendData(id: Int, data: ArrayList<Any>) {
        val jsonData = JsonAppendData("appendData", data)
        jsonAdapterAppendData.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }

    fun resetData(id: Int) {
        val jsonData = JsonResetData("resetData")
        jsonAdapterResetData.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }

    fun appendDataToPlotLine(id: Int, x: Float, y: Float) {
        val jsonData = JsonAppendDataToPlotLine("appendData", x, y)
        jsonAdapterAppendDataToPlotLine.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }

    fun setPlotLineAxesDecimalDigits(id: Int, x: Float, y: Float) {
        val jsonData = JsonSetAxesDecimalDigits("setAxesDecimalDigits", x, y)
        jsonAdapterSetAxesDecimalDigits.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }

    fun setAxisAutoFitEnabled(id: Int, enabled: Boolean) {
        val jsonData = JsonSetAxesAutoFit("setAxesAutoFit", enabled)
        jsonAdapterSetAxesAutoFit.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }

    fun appendTextToClippedMultiLineTextRenderer(id: Int, text: String) {
        this.xFramesWrapper.appendTextToClippedMultiLineTextRenderer(id, text)
    }

    fun setInputTextValue(id: Int, value: String) {
        val jsonData = JsonSetValue("setValue", value)
        jsonAdapterSetValue.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }

    fun setComboSelectedIndex(id: Int, index: Int) {
        val jsonData = JsonSetSelectedIndex("setSelectedIndex", index)
        jsonAdapterSetSelectedIndex.toJson(jsonData)?.let {
            this.xFramesWrapper.elementInternalOp(id, it)
        }
    }
}

@JsonClass(generateAdapter = false)
data class WidgetNode(
    val type: String,
    var props: Map<String, Any?> = emptyMap(),
    var children: MutableList<WidgetNode> = mutableListOf(),
    @Json
    val id: Int = widgetRegistrationService.getNextWidgetId()
)

class WidgetNodeAdapter  {
    @FromJson
    fun fromJson(json: Map<String, Any?>): WidgetNode {
        val type = json["type"] as String
        val id = json["id"] as Int
        val props = json - "id" - "type"

        return WidgetNode(type = type, props = props, id = id)
    }

    @ToJson
    fun toJson(widgetNode: WidgetNode): Map<String, Any?> {
        return widgetNode.props + mapOf(
            "id" to widgetNode.id,
            "type" to widgetNode.type,
        )
    }
}

class WidgetTreeApplier(val xframes: XFramesWrapper, val jsonAdapter: JsonAdapter<WidgetNode>, root: WidgetNode) : AbstractApplier<WidgetNode>(root) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val childrenAdapter = moshi.adapter(List::class.java).lenient()

    override fun onClear() {
        root.children.clear()
    }

    override fun insertBottomUp(index: Int, instance: WidgetNode) {}

    override fun insertTopDown(index: Int, instance: WidgetNode) {
        current.children.add(index, instance)

        xframes.setElement(jsonAdapter.toJson(instance))
        xframes.setChildren(current.id, childrenAdapter.toJson(listOf(instance.id)))
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }

    override fun onBeginChanges() {
        super.onBeginChanges()

        // Might be useful/necessary
    }

    override fun onEndChanges() {
        super.onEndChanges()

//        val rootJson = adapter.toJson(root)
//        println("done: $rootJson")
    }
}

@Composable
fun Node(root: Boolean? = null, props: Map<String, Any?> = emptyMap(), content: @Composable () -> Unit = {}) {
    val updatedProps = remember { props + mapOf("root" to root) }

    WidgetNodeComposable("node", updatedProps, content)
}

@Composable
fun UnformattedText(text: String, props: Map<String, Any?> = emptyMap()) {
    val updatedProps = remember { props + mapOf("text" to text) }

    WidgetNodeComposable("unformatted-text", updatedProps)
}

@Composable
fun Button(label: String, onClick: () -> Unit = {}, props: Map<String, Any?> = emptyMap()) {
    val updatedProps = remember { props + mapOf("label" to label) }

    val node = WidgetNode("button", updatedProps)
    widgetRegistrationService.registerWidget(node.id, node)

    if (onClick != {}) {
        widgetRegistrationService.registerWidgetForOnClickEvent(node.id, onClick)
    }

    WidgetNodeComposable(node, updatedProps)
}

@Composable
fun WidgetNodeComposable(type: String, props: Map<String, Any?> = emptyMap(), content: @Composable () -> Unit = {}) {
    ComposeNode<WidgetNode, WidgetTreeApplier>(
        factory = {
            val node = WidgetNode(type, props)
            widgetRegistrationService.registerWidget(node.id, node)

            node
        },
        update = {
            set(props) { this.props = props }
        },
        content = content
    )
}

@Composable
fun App() {
    Node {
        UnformattedText("Hello, world!!!")
//        Button("Click here!")
    }
}

@Composable
fun WidgetNodeComposable(node: WidgetNode, props: Map<String, Any?> = emptyMap(), content: @Composable () -> Unit = {}) {
    ComposeNode<WidgetNode, WidgetTreeApplier>(
        factory = {
            node
        },
        update = {
            set(props) { this.props = props }
        },
        content = content
    )
}

// Main function defined outside the class
fun main() {
    val moshi = Moshi.Builder()
        .add(WidgetNodeAdapter())
        .build()
    val jsonAdapter = moshi.adapter(WidgetNode::class.java)

    val xframes = XFramesWrapper()

    val root = WidgetNode("node", mapOf("root" to true))
    widgetRegistrationService.registerWidget(root.id, root)

    val applier = WidgetTreeApplier(xframes, jsonAdapter, root)
    val composition = Composition(applier, Recomposer(MainScope().coroutineContext))

    MyCallbackHandler.initialize(xframes, jsonAdapter, composition) { App() }

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
