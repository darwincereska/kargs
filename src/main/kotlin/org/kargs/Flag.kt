package org.kargs

/**
 * Represents a boolean flag that doesn't take a value (e.g. --verbose, -v)
 *
 * @param longName The long form name (used with --)
 * @param shortName The short form name (used with -)
 * @param description Help text for this flag
 * @param defaultValue Default value (typically false)
 */
class Flag(
    val longName: String,
    val shortName: String? = null,
    description: String? = null,
    private val defaultValue: Boolean = false,
) : KargsProperty<Boolean>(description) {

    private var wasExplicitlySet = false

    init {
        value = defaultValue

        // Validate names
        require(longName.isNotBlank()) { "Long name cannot be blank" }
        require(!longName.startsWith("-")) { "Long name should not start with dashes" }
        shortName?.let {
            require(it.length == 1) { "Short name must be exactly one character" }
            require(!it.startsWith("-")) { "Short name should not start with dashes" }
        }
    }

    override fun parseValue(str: String) {
        value = when (str.lowercase()) {
            "true", "yes", "1", "on" -> true
            "false", "no", "0", "off" -> false
            else -> throw ArgumentParseException("Invalid flag value: $str")
        }
        wasExplicitlySet = true
    }

    /**
     * Set the flag to true (called when flag is present)
     */
    fun setFlag() {
        value = true
        wasExplicitlySet = true
    }

    /**
     * Check if this flag was explicitly set
     */
    fun isSet(): Boolean = wasExplicitlySet
}
