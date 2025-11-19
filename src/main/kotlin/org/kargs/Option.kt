package org.kargs

/**
 * Represents a command-line option that takes a value (e.g. --output file.txt)
 *
 * @param type The type converter for this options's value
 * @param longName The long form name (used with --)
 * @param shortName The short form name (used with -)
 * @param description Help text for this option
 * @param required Whether this option must be provided
 * @param defaultValue Default value if not provided
 */
class Option<T>(
    val type: ArgType<T>,
    val longName: String,
    val shortName: String? = null,
    description: String? = null,
    val required: Boolean = false,
    private val defaultValue: T? = null
) : KargsProperty<T>(description) {

    private var wasExplicitlySet = false

    init {
        // Set the default value if provided
        defaultValue?.let { value = it }

        // Validate names
        require(longName.isNotBlank()) { "Long name cannot be blank" }
        require(!longName.startsWith("-")) { "Long name should not start with dashes" }
        shortName?.let { 
            require(it.length == 1) { "Short name must be exactly one character" }
            require(!it.startsWith("-")) { "Short name should not start with dashes" }
        }
    }

    override fun parseValue(str: String) {
        value = type.convert(str)
        wasExplicitlySet = true
    }

    override fun isValid(): Boolean {
        return if (required) {
            value != null && type.validate(value!!)
        } else {
            value?.let { type.validate(it) } ?: true
        }
    }

    override fun getValidationError(): String? {
        return when {
            required && value == null -> "Option --$longName is required"
            value != null && !type.validate(value!!) -> "Invalid value for --$longName: expected ${type.getValidationDescription()}"
            else -> null
        }
    }

    /**
     * Check if the option has been set (different from default)
     */
    fun isSet(): Boolean = wasExplicitlySet

    /**
     * Get the value or default
     */
    fun getValueOrDefault(): T? = value ?: defaultValue
}
