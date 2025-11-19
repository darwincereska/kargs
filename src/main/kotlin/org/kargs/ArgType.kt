package org.kargs

/**
 * Sealed class representing different argument types with conversion and validation logic
 */
sealed class ArgType<T>(val typeName: String) {
    /**
     * Convert a string value to the target type
     * @throws ArgumentParseException if conversion fails
     */
    abstract fun convert(value: String): T

    /**
     * Validate that a value is acceptable for this type
     * @return true if valid, false otherwise
     */
    open fun validate(value: T): Boolean = true

    /**
     * Get a description of valid values for this type
     */
    open fun getValidationDescription(): String = "any $typeName"

    object StringType : ArgType<kotlin.String>("String") {
        override fun convert(value: String) = value
    }

    object IntType : ArgType<kotlin.Int>("Int") {
        override fun convert(value: String): kotlin.Int = value.toIntOrNull() ?: throw ArgumentParseException("`$value` is not a valid integer")
    }

    object BooleanType : ArgType<kotlin.Boolean>("Boolean") {
        override fun convert(value: String): kotlin.Boolean {
            return when (value.lowercase()) {
                "true", "yes", "1", "on" -> true
                "false", "no", "0", "off" -> false
                else -> throw ArgumentParseException("'$value' is not a valid boolean (true/false, yes/no, 1/0, on/off)")
            }
        }
    }

    object DoubleType : ArgType<kotlin.Double>("Double") {
        override fun convert(value: String): kotlin.Double {
            return value.toDoubleOrNull()
                ?: throw ArgumentParseException("'$value' is not a valid number")
        }
    }

    /**
     * Create a constrained integer type with min/max bounds
     */
    class IntRange(private val min: kotlin.Int, private val max: kotlin.Int) : ArgType<kotlin.Int>("Int") {
        override fun convert(value: String): kotlin.Int {
            val intValue = value.toIntOrNull() ?: throw ArgumentParseException("`$value` is not a valid integer")
            if (intValue !in min..max) {
                throw ArgumentParseException("`$value` must be between $min and $max")
            }
            return intValue
        }

        override fun getValidationDescription(): String = "integer between $min and $max"
    }

    /**
     * Create an enum type from list of valid choices
     */
    class Choice(private val choices: List<kotlin.String>) : ArgType<kotlin.String>("Choice") {
        override fun convert(value: String): kotlin.String {
            if (value !in choices) {
                throw ArgumentParseException("`$value` is not a valid choice. Valid options: ${choices.joinToString(", ")}")
            }
            return value
        }

        override fun getValidationDescription(): String = "one of: ${choices.joinToString(", ")}"
    }

    companion object {
        val String: ArgType<kotlin.String> = StringType
        val Int: ArgType<kotlin.Int> = IntType
        val Boolean: ArgType<kotlin.Boolean> = BooleanType
        val Double: ArgType<kotlin.Double> = DoubleType

        /**
         * Create an integer type with bounds
         */
        fun intRange(min: kotlin.Int, max: kotlin.Int) = IntRange(min, max)

        /**
         * Create a choice type from valid options
         */
        fun choice(vararg options: kotlin.String) = Choice(options.toList())
    }
}
