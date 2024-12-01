package dev.xframes

import org.json.JSONObject
import java.util.*
import java.util.concurrent.*

class XFramesWrapper {
    // Native method declarations
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

// Main function defined outside the class
fun main() {
    println("Start!")

    val clazz = Class.forName("dev.xframes.MyCallbackHandler")
    println("Class found: $clazz")

    val xframes = XFramesWrapper()

    MyCallbackHandler.initialize(xframes)

    // Initialize with paths and callbacks
    xframes.init("C:\\dev\\xframes-kotlin\\assets", getFontDefinitions(), getStyleOverrides(), MyCallbackHandler)

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
    val fontDefs = JSONObject()
    val defs = mutableListOf<Map<String, Any>>()
    val entry = mutableMapOf<String, Any>()
    entry["name"] = "roboto-regular"
    entry["sizes"] = listOf(16, 18, 20, 24, 28, 32, 36, 48)
    defs.add(entry)

    // Flatten font definitions
    val flattenedDefs = mutableListOf<Map<String, Any>>()
    for (fontEntry in defs) {
        val fontName = fontEntry["name"] as String
        val sizes = fontEntry["sizes"] as List<Int>

        for (size in sizes) {
            val fontDef = mutableMapOf<String, Any>()
            fontDef["name"] = fontName
            fontDef["size"] = size
            flattenedDefs.add(fontDef)
        }
    }

    fontDefs.put("defs", flattenedDefs)

    return fontDefs.toString()
}

// Returns style overrides as a JSON string
fun getStyleOverrides(): String {
    val theme2Colors = mutableMapOf<String, String>()
    theme2Colors["darkestGrey"] = "#141f2c"
    theme2Colors["darkerGrey"] = "#2a2e39"
    theme2Colors["darkGrey"] = "#363b4a"
    theme2Colors["lightGrey"] = "#5a5a5a"
    theme2Colors["lighterGrey"] = "#7A818C"
    theme2Colors["evenLighterGrey"] = "#8491a3"
    theme2Colors["black"] = "#0A0B0D"
    theme2Colors["green"] = "#75f986"
    theme2Colors["red"] = "#ff0062"
    theme2Colors["white"] = "#fff"

    val theme2 = mutableMapOf<String, Any>()
    val colorMap = mutableMapOf<Int, Any>()
    colorMap[XFramesWrapper.ImGuiCol.Text.value] = listOf(theme2Colors["white"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TextDisabled.value] = listOf(theme2Colors["lighterGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.WindowBg.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ChildBg.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.PopupBg.value] = listOf(theme2Colors["white"], 1)
    colorMap[XFramesWrapper.ImGuiCol.Border.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.BorderShadow.value] = listOf(theme2Colors["darkestGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.FrameBg.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.FrameBgHovered.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.FrameBgActive.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TitleBg.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TitleBgActive.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TitleBgCollapsed.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.MenuBarBg.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ScrollbarBg.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ScrollbarGrab.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ScrollbarGrabHovered.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ScrollbarGrabActive.value] = listOf(theme2Colors["darkestGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.CheckMark.value] = listOf(theme2Colors["darkestGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.SliderGrab.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.SliderGrabActive.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.Button.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ButtonHovered.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ButtonActive.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.Header.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.HeaderHovered.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.HeaderActive.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.Separator.value] = listOf(theme2Colors["darkestGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.SeparatorHovered.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.SeparatorActive.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ResizeGrip.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ResizeGripHovered.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ResizeGripActive.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.Tab.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TabHovered.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TabActive.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TabUnfocused.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TabUnfocusedActive.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.PlotLines.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.PlotLinesHovered.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.PlotHistogram.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.PlotHistogramHovered.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TableHeaderBg.value] = listOf(theme2Colors["black"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TableBorderStrong.value] = listOf(theme2Colors["lightGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TableBorderLight.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TableRowBg.value] = listOf(theme2Colors["darkGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TableRowBgAlt.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.TextSelectedBg.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.DragDropTarget.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.NavHighlight.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.NavWindowingHighlight.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.NavWindowingDimBg.value] = listOf(theme2Colors["darkerGrey"], 1)
    colorMap[XFramesWrapper.ImGuiCol.ModalWindowDimBg.value] = listOf(theme2Colors["darkerGrey"], 1)

    theme2["colors"] = colorMap

    return JSONObject(theme2).toString()
}
