package org.kargs

object Colors {
    enum class Color(val code: String) {
        RESET("\u001B[0m"),

        // Base colors
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        MAGENTA("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m"),
        GRAY("\u001B[90m"),

        // Bright colors
        BRIGHT_BLACK("\u001B[90m"),
        BRIGHT_RED("\u001B[91m"),
        BRIGHT_GREEN("\u001B[92m"),
        BRIGHT_YELLOW("\u001B[93m"),
        BRIGHT_BLUE("\u001B[94m"),
        BRIGHT_MAGENTA("\u001B[95m"),
        BRIGHT_CYAN("\u001B[96m"),
        BRIGHT_WHITE("\u001B[97m"),

        // Extended 256-color palette (some popular ones)
        ORANGE("\u001B[38;5;208m"),
        PURPLE("\u001B[38;5;129m"),
        PINK("\u001B[38;5;205m"),
        LIME("\u001B[38;5;154m"),
        TEAL("\u001B[38;5;80m"),
        NAVY("\u001B[38;5;17m"),
        MAROON("\u001B[38;5;88m"),
        OLIVE("\u001B[38;5;100m"),
        SILVER("\u001B[38;5;248m"),
        GOLD("\u001B[38;5;220m"),
        CORAL("\u001B[38;5;209m"),
        SALMON("\u001B[38;5;210m"),
        KHAKI("\u001B[38;5;185m"),
        VIOLET("\u001B[38;5;177m"),
        INDIGO("\u001B[38;5;54m"),
        TURQUOISE("\u001B[38;5;80m"),
        CRIMSON("\u001B[38;5;196m"),
        FOREST_GREEN("\u001B[38;5;22m"),
        ROYAL_BLUE("\u001B[38;5;21m"),
        DARK_ORANGE("\u001B[38;5;166m"),
        LIGHT_GRAY("\u001B[38;5;250m"),
        DARK_GRAY("\u001B[38;5;240m"),
        
        // Pastel colors
        PASTEL_PINK("\u001B[38;5;217m"),
        PASTEL_BLUE("\u001B[38;5;153m"),
        PASTEL_GREEN("\u001B[38;5;157m"),
        PASTEL_YELLOW("\u001B[38;5;229m"),
        PASTEL_PURPLE("\u001B[38;5;183m"),
        PASTEL_ORANGE("\u001B[38;5;223m"),

        // Styles
        BOLD("\u001B[1m"),
        DIM("\u001B[2m"),
        ITALIC("\u001B[3m"),
        UNDERLINE("\u001B[4m"),
        BLINK("\u001B[5m"),
        REVERSE("\u001B[7m"),
        STRIKETHROUGH("\u001B[9m"),

        // Background colors
        BG_BLACK("\u001B[40m"),
        BG_RED("\u001B[41m"),
        BG_GREEN("\u001B[42m"),
        BG_YELLOW("\u001B[43m"),
        BG_BLUE("\u001B[44m"),
        BG_MAGENTA("\u001B[45m"),
        BG_CYAN("\u001B[46m"),
        BG_WHITE("\u001B[47m"),

        // Semantic presets
        ERROR("${BRIGHT_RED.code}${BOLD.code}"),
        WARN("${YELLOW.code}${BOLD.code}"),
        INFO(CYAN.code),
        DEBUG("${DIM.code}${GRAY.code}"),
        SUCCESS("${BRIGHT_GREEN.code}${BOLD.code}"),
        
        // File/UI semantic colors
        FILENAME("${BRIGHT_BLUE.code}${BOLD.code}"),
        HEADING("${BRIGHT_MAGENTA.code}${BOLD.code}"),
        SUBHEADING("${MAGENTA.code}${BOLD.code}"),
        EMPHASIS("${BRIGHT_YELLOW.code}${BOLD.code}"),
        HIGHLIGHT("${BG_YELLOW.code}${BLACK.code}"),
        LINK("${BRIGHT_CYAN.code}${UNDERLINE.code}"),
        CODE(BRIGHT_GREEN.code),
        COMMENT("${DIM.code}${GRAY.code}"),
        KEYWORD(BRIGHT_MAGENTA.code),
        STRING(GREEN.code),
        NUMBER(BRIGHT_BLUE.code),
        OPERATOR(BRIGHT_RED.code),
        
        // Status colors
        ACTIVE(BRIGHT_GREEN.code),
        INACTIVE("${DIM.code}${GRAY.code}"),
        PENDING(YELLOW.code),
        FAILED(BRIGHT_RED.code),
        PASSED(BRIGHT_GREEN.code),
        SKIPPED(BRIGHT_YELLOW.code),
        
        // UI Elements
        BORDER(DARK_GRAY.code),
        MENU_ITEM(BRIGHT_WHITE.code),
        SELECTED("${BG_BLUE.code}${BRIGHT_WHITE.code}"),
        DISABLED("${DIM.code}${GRAY.code}"),
        
        // Log levels
        TRACE("${DIM.code}${LIGHT_GRAY.code}"),
        VERBOSE(GRAY.code),
        FATAL("${BG_RED.code}${BRIGHT_WHITE.code}${BOLD.code}");

        fun bold() = DerivedColor("${BOLD.code}$code")
        fun dim() = DerivedColor("${DIM.code}$code")
        fun italic() = DerivedColor("${ITALIC.code}$code")
        fun underline() = DerivedColor("${UNDERLINE.code}$code")
        fun strikethrough() = DerivedColor("${STRIKETHROUGH.code}$code")
        fun reverse() = DerivedColor("${REVERSE.code}$code")
        fun blink() = DerivedColor("${BLINK.code}$code")
        
        // Combine with background
        fun onBackground(bgColor: Color) = DerivedColor("${bgColor.code}$code")
    }

    /** Wrapper for modified colors */
    class DerivedColor(val combinedCode: String) {
        fun bold() = DerivedColor("${Color.BOLD.code}$combinedCode")
        fun dim() = DerivedColor("${Color.DIM.code}$combinedCode")
        fun italic() = DerivedColor("${Color.ITALIC.code}$combinedCode")
        fun underline() = DerivedColor("${Color.UNDERLINE.code}$combinedCode")
        fun strikethrough() = DerivedColor("${Color.STRIKETHROUGH.code}$combinedCode")
        fun reverse() = DerivedColor("${Color.REVERSE.code}$combinedCode")
        fun blink() = DerivedColor("${Color.BLINK.code}$combinedCode")
    }

    // Global enable/disable
    private var globalColorsEnabled = true
    fun setGlobalColorsEnabled(enabled: Boolean) { globalColorsEnabled = enabled }
    fun areGlobalColorsEnabled(): Boolean = globalColorsEnabled

    // colorize() overloads
    fun colorize(text: String, color: Color): String =
        if (globalColorsEnabled) "${color.code}$text${Color.RESET.code}" else text

    fun colorize(text: String, color: DerivedColor): String =
        if (globalColorsEnabled) "${color.combinedCode}$text${Color.RESET.code}" else text

    // RGB color support (for terminals that support it)
    fun rgb(r: Int, g: Int, b: Int): DerivedColor = 
        DerivedColor("\u001B[38;2;$r;$g;${b}m")
    
    fun bgRgb(r: Int, g: Int, b: Int): DerivedColor = 
        DerivedColor("\u001B[48;2;$r;$g;${b}m")

    // Hex color support
    fun hex(hexColor: String): DerivedColor {
        val hex = hexColor.removePrefix("#")
        val r = hex.take(2).toInt(16)
        val g = hex.substring(2, 4).toInt(16)
        val b = hex.substring(4, 6).toInt(16)
        return rgb(r, g, b)
    }

    // --------------------------------------------------------
    // Semantic convenience helpers
    // --------------------------------------------------------
    fun filename(text: String) = colorize(text, Color.FILENAME)
    fun heading(text: String) = colorize(text, Color.HEADING)
    fun subheading(text: String) = colorize(text, Color.SUBHEADING)
    fun emphasis(text: String) = colorize(text, Color.EMPHASIS)
    fun highlight(text: String) = colorize(text, Color.HIGHLIGHT)
    fun link(text: String) = colorize(text, Color.LINK)
    fun code(text: String) = colorize(text, Color.CODE)
    fun comment(text: String) = colorize(text, Color.COMMENT)
    fun keyword(text: String) = colorize(text, Color.KEYWORD)
    fun string(text: String) = colorize(text, Color.STRING)
    fun number(text: String) = colorize(text, Color.NUMBER)
    fun operator(text: String) = colorize(text, Color.OPERATOR)
    
    // Status helpers
    fun active(text: String) = colorize(text, Color.ACTIVE)
    fun inactive(text: String) = colorize(text, Color.INACTIVE)
    fun pending(text: String) = colorize(text, Color.PENDING)
    fun failed(text: String) = colorize(text, Color.FAILED)
    fun passed(text: String) = colorize(text, Color.PASSED)
    fun skipped(text: String) = colorize(text, Color.SKIPPED)
    fun success(text: String) = colorize(text, Color.SUCCESS)
    
    // Log level helpers
    fun trace(text: String) = colorize(text, Color.TRACE)
    fun verbose(text: String) = colorize(text, Color.VERBOSE)
    fun fatal(text: String) = colorize(text, Color.FATAL)

    // --------------------------------------------------------
    // Extended color convenience helpers
    // --------------------------------------------------------
    fun orange(text: String) = colorize(text, Color.ORANGE)
    fun purple(text: String) = colorize(text, Color.PURPLE)
    fun pink(text: String) = colorize(text, Color.PINK)
    fun lime(text: String) = colorize(text, Color.LIME)
    fun teal(text: String) = colorize(text, Color.TEAL)
    fun navy(text: String) = colorize(text, Color.NAVY)
    fun maroon(text: String) = colorize(text, Color.MAROON)
    fun olive(text: String) = colorize(text, Color.OLIVE)
    fun silver(text: String) = colorize(text, Color.SILVER)
    fun gold(text: String) = colorize(text, Color.GOLD)
    fun coral(text: String) = colorize(text, Color.CORAL)
    fun salmon(text: String) = colorize(text, Color.SALMON)
    fun khaki(text: String) = colorize(text, Color.KHAKI)
    fun violet(text: String) = colorize(text, Color.VIOLET)
    fun indigo(text: String) = colorize(text, Color.INDIGO)
    fun turquoise(text: String) = colorize(text, Color.TURQUOISE)
    fun crimson(text: String) = colorize(text, Color.CRIMSON)
    fun forestGreen(text: String) = colorize(text, Color.FOREST_GREEN)
    fun royalBlue(text: String) = colorize(text, Color.ROYAL_BLUE)
    fun darkOrange(text: String) = colorize(text, Color.DARK_ORANGE)
    fun lightGray(text: String) = colorize(text, Color.LIGHT_GRAY)
    fun darkGray(text: String) = colorize(text, Color.DARK_GRAY)

    // Pastel helpers
    fun pastelPink(text: String) = colorize(text, Color.PASTEL_PINK)
    fun pastelBlue(text: String) = colorize(text, Color.PASTEL_BLUE)
    fun pastelGreen(text: String) = colorize(text, Color.PASTEL_GREEN)
    fun pastelYellow(text: String) = colorize(text, Color.PASTEL_YELLOW)
    fun pastelPurple(text: String) = colorize(text, Color.PASTEL_PURPLE)
    fun pastelOrange(text: String) = colorize(text, Color.PASTEL_ORANGE)

    // --------------------------------------------------------
    // Original convenience helpers (keeping all existing ones)
    // --------------------------------------------------------
    fun bold(text: String) = colorize(text, Color.WHITE.bold())
    fun dim(text: String) = colorize(text, Color.GRAY)
    fun black(text: String) = colorize(text, Color.BLACK)
    fun red(text: String) = colorize(text, Color.RED)
    fun green(text: String) = colorize(text, Color.GREEN)
    fun yellow(text: String) = colorize(text, Color.YELLOW)
    fun blue(text: String) = colorize(text, Color.BLUE)
    fun magenta(text: String) = colorize(text, Color.MAGENTA)
    fun cyan(text: String) = colorize(text, Color.CYAN)
    fun white(text: String) = colorize(text, Color.WHITE)
    fun gray(text: String) = colorize(text, Color.GRAY)

    fun brightBlack(text: String) = colorize(text, Color.BRIGHT_BLACK)
    fun brightRed(text: String) = colorize(text, Color.BRIGHT_RED)
    fun brightGreen(text: String) = colorize(text, Color.BRIGHT_GREEN)
    fun brightYellow(text: String) = colorize(text, Color.BRIGHT_YELLOW)
    fun brightBlue(text: String) = colorize(text, Color.BRIGHT_BLUE)
    fun brightMagenta(text: String) = colorize(text, Color.BRIGHT_MAGENTA)
    fun brightCyan(text: String) = colorize(text, Color.BRIGHT_CYAN)
    fun brightWhite(text: String) = colorize(text, Color.BRIGHT_WHITE)

    // Presets
    fun error(text: String) = colorize(text, Color.ERROR)
    fun warn(text: String) = colorize(text, Color.WARN)
    fun info(text: String) = colorize(text, Color.INFO)
    fun debug(text: String) = colorize(text, Color.DEBUG)

    // Bold helpers (keeping all existing ones)
    fun boldBlack(text: String) = colorize(text, Color.BLACK.bold())
    fun boldRed(text: String) = colorize(text, Color.RED.bold())
    fun boldGreen(text: String) = colorize(text, Color.GREEN.bold())
    fun boldYellow(text: String) = colorize(text, Color.YELLOW.bold())
    fun boldBlue(text: String) = colorize(text, Color.BLUE.bold())
    fun boldMagenta(text: String) = colorize(text, Color.MAGENTA.bold())
    fun boldCyan(text: String) = colorize(text, Color.CYAN.bold())
    fun boldWhite(text: String) = colorize(text, Color.WHITE.bold())

    fun boldBrightBlack(text: String) = colorize(text, Color.BRIGHT_BLACK.bold())
    fun boldBrightRed(text: String) = colorize(text, Color.BRIGHT_RED.bold())
    fun boldBrightGreen(text: String) = colorize(text, Color.BRIGHT_GREEN.bold())
    fun boldBrightYellow(text: String) = colorize(text, Color.BRIGHT_YELLOW.bold())
    fun boldBrightBlue(text: String) = colorize(text, Color.BRIGHT_BLUE.bold())
    fun boldBrightMagenta(text: String) = colorize(text, Color.BRIGHT_MAGENTA.bold())
    fun boldBrightCyan(text: String) = colorize(text, Color.BRIGHT_CYAN.bold())
    fun boldBrightWhite(text: String) = colorize(text, Color.BRIGHT_WHITE.bold())

    // Dim helpers (keeping all existing ones)
    fun dimBlack(text: String) = colorize(text, Color.BLACK.dim())
    fun dimRed(text: String) = colorize(text, Color.RED.dim())
    fun dimGreen(text: String) = colorize(text, Color.GREEN.dim())
    fun dimYellow(text: String) = colorize(text, Color.YELLOW.dim())
    fun dimBlue(text: String) = colorize(text, Color.BLUE.dim())
    fun dimMagenta(text: String) = colorize(text, Color.MAGENTA.dim())
    fun dimCyan(text: String) = colorize(text, Color.CYAN.dim())
    fun dimWhite(text: String) = colorize(text, Color.WHITE.dim())

    fun dimBrightBlack(text: String) = colorize(text, Color.BRIGHT_BLACK.dim())
    fun dimBrightRed(text: String) = colorize(text, Color.BRIGHT_RED.dim())
    fun dimBrightGreen(text: String) = colorize(text, Color.BRIGHT_GREEN.dim())
    fun dimBrightYellow(text: String) = colorize(text, Color.BRIGHT_YELLOW.dim())
    fun dimBrightBlue(text: String) = colorize(text, Color.BRIGHT_BLUE.dim())
    fun dimBrightMagenta(text: String) = colorize(text, Color.BRIGHT_MAGENTA.dim())
    fun dimBrightCyan(text: String) = colorize(text, Color.BRIGHT_CYAN.dim())
    fun dimBrightWhite(text: String) = colorize(text, Color.BRIGHT_WHITE.dim())

    // --------------------------------------------------------
    // Utility functions
    // --------------------------------------------------------
    
    // Create a gradient effect
    fun gradient(text: String, startColor: Color, endColor: Color): String {
        if (!globalColorsEnabled || text.isEmpty()) return text
        
        val length = text.length
        if (length == 1) return colorize(text, startColor)
        
        return text.mapIndexed { index, char ->
            val ratio = index.toFloat() / (length - 1)
            // Simple interpolation between colors (this is a basic implementation)
            val color = if (ratio < 0.5f) startColor else endColor
            colorize(char.toString(), color)
        }.joinToString("")
    }
    
    // Rainbow effect
    fun rainbow(text: String): String {
        if (!globalColorsEnabled) return text
        
        val colors = listOf(
            Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, 
            Color.CYAN, Color.BLUE, Color.MAGENTA
        )
        
        return text.mapIndexed { index, char ->
            val colorIndex = index % colors.size
            colorize(char.toString(), colors[colorIndex])
        }.joinToString("")
    }
    
    // Strip all ANSI codes
    fun stripColors(text: String): String = text.replace(Regex("\u001B\\[[0-9;]*m"), "")
    
    // Get length without ANSI codes
    fun getDisplayLength(text: String): Int = stripColors(text).length
}

