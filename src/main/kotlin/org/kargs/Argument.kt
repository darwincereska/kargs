package org.kargs

/**
 * Represents a positional command-line argument
 *
 * @param type The type converter for this argument's value
 * @param name The name of this argument (used in help)
 * @param description Help text for this argument
 * @param required Whether this argument must be provided
 */
class Argument<T>(
    val type: ArgType<T>,
    val name: String,
    description: String? = null,
    val required: Boolean = true
) : KargsProperty<T>(description) {
    init {
        require(name.isNotBlank()) { "Argument name cannot be blank" }
    }

    override fun parseValue(str: String) {
        value = type.convert(str)
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
            required && value == null -> "Argument '$name' is required"
            value != null && !type.validate(value!!) -> "Invalid value for argument '$name': expected ${type.getValidationDescription()}"
            else -> null
        }
    }
}
