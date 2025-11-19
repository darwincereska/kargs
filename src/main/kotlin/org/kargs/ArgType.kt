package org.kargs

import java.io.File

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
    class IntRange(val min: kotlin.Int, val max: kotlin.Int) : ArgType<kotlin.Int>("Int") {
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
    class Choice(val choices: List<kotlin.String>) : ArgType<kotlin.String>("Choice") {
        override fun convert(value: String): kotlin.String {
            if (value !in choices) {
                throw ArgumentParseException("`$value` is not a valid choice. Valid options: ${choices.joinToString(", ")}")
            }
            return value
        }

        override fun getValidationDescription(): String = "one of: ${choices.joinToString(", ")}"
    }

    /**
     * Optional value type - can be used as flag or with value
     */
    class OptionalValue(val defaultWhenPresent: String = "true") : ArgType<String>("OptionalValue") {
        override fun convert(value: String): String = value

        override fun getValidationDescription(): String = "optional value or flag"
    }

    /**
     * File path type with existence and permision validation
     */
    class FilePath(
        val mustExist: Boolean = false,
        val mustBeFile: Boolean = false,
        val mustBeDirectory: Boolean = false,
        val mustBeReadable: Boolean = false,
        val mustBeWritable: Boolean = false
    ) : ArgType<File>("File") {
        override fun convert(value: String): File = File(value)

        override fun validate(value: File): Boolean {
            return when {
                mustExist && !value.exists() -> false
                mustBeFile && !value.isFile -> false
                mustBeDirectory && !value.isDirectory -> false
                mustBeReadable && !value.canRead() -> false
                mustBeWritable && !value.canWrite() -> false
                else -> true
            }        
        }

        override fun getValidationDescription(): String {
            val conditions = mutableListOf<String>()
            if (mustExist) conditions.add("must exist")
            if (mustBeFile) conditions.add("must be a file")
            if (mustBeDirectory) conditions.add("must be a directory")
            if (mustBeReadable) conditions.add("must be readable")
            if (mustBeWritable) conditions.add("must be writable")

            return if (conditions.isEmpty()) "file path" else "file path (${conditions.joinToString(", ")})"
        }
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

        /**
         * Create an optional value or flag
         */
        fun optionalValue(defaultWhenPresent: String = "true") = OptionalValue(defaultWhenPresent)

        /**
         * File that must exist and be a regular file
         */
        fun existingFile() = FilePath(mustExist = true, mustBeFile = true)

        /**
         * Directory that must exist
         */
        fun existingDirectory() = FilePath(mustExist = true, mustBeDirectory = true)

        /**
         * File that must exist and be readable
         */
        fun readableFile() = FilePath(mustExist = true, mustBeFile = true, mustBeReadable = true)

        /**
         * File that must be writable (can be created if doesn't exist)
         */
        fun writableFile() = FilePath(mustBeWritable = true)

        /**
         * Any file path (no validation)
         */
        fun filePath() = FilePath()
    }
}
