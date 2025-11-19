package org.kargs

/**
 * Configuration class for customizing parser behavior
 */
data class ParserConfig(
    val colorsEnabled: Boolean = true,
    val strictMode: Boolean = false, // Whether to fail on unknown options
    val helpOnEmpty: Boolean = true, // Show help when no args provided
    val caseSensitive: Boolean = true,
    val allowAbbreviations: Boolean = false, // Allow partial option matching
    val programVersion: String? = null // Adds --version and -v flag if set
) {
    companion object {
        val DEFAULT = ParserConfig()
    }
}
