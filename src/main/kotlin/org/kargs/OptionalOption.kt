package org.kargs

/**
 * Option that can be used as a flag or with a value
 */
class OptionalOption(
    val longName: String,
    val shortName: String? = null,
    description: String? = null,
    private val defaultWhenPresent: String = "true"
) : KargsProperty<String>(description) {
    private var wasExplicitlySet = false

    init {
        require(longName.isNotBlank()) { "Long name cannot be blank" }
        require(!longName.startsWith("-")) { "Long name should not start with dashes" }
        shortName?.let {
            require(it.length == 1) { "Short name must be exactly one character" }
            require(!it.startsWith("-")) { "Short name should not start with dashes" }
        }
    }

    override fun parseValue(str: String) {
        value = str
        wasExplicitlySet = true
    }

    /**
     * Set as flag (no value provided)
     */
    fun setAsFlag() {
        value = defaultWhenPresent
        wasExplicitlySet = true
    }

    fun isSet(): Boolean = wasExplicitlySet
}
