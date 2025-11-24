package org.kargs


/**
 * Base class for defining subcommands with their options, flags, and arguments
 *
 * @param name The name for this subcommand
 * @param description Help description for this subcommand
 * @param aliases Alternative names for this subcommand
 */
abstract class Subcommand(
    val name: String,
    val description: String = "",
    val aliases: List<String> = emptyList()
) {
    private val _options = mutableListOf<Option<*>>()
    private val _flags = mutableListOf<Flag>()
    private val _arguments = mutableListOf<Argument<*>>()
    private val _optionalOptions = mutableListOf<OptionalOption>()

    init {
        require(name.isNotBlank()) { "Subcommand name cannot be blank" }
    }

    /**
     * Register a property (option, flag, argument) with this subcommand
     * Called automatically by property delagates
     */
    fun registerProperty(prop: KargsProperty<*>) {
        when (prop) {
            is Option<*> -> _options += prop
            is Flag -> _flags += prop
            is Argument<*> -> _arguments += prop
            is OptionalOption -> _optionalOptions += prop
        }
    }

    // Public read-only access to registered properties
    val options: List<Option<*>> get() = _options
    val flags: List<Flag> get() = _flags
    val arguments: List<Argument<*>> get() = _arguments
    val optionalOptions: List<OptionalOption> get() = _optionalOptions

    /**
     * Execute this subcommand - must be implemented by subclasses
     */
    abstract fun execute()

    /**
     * Print help information for this subcommand
     */
    fun printHelp() {
        println(Colors.boldWhite("Usage: $name ${"[options]"}${if (arguments.isNotEmpty()) " ${arguments.joinToString(" ") { if (it.required) "<${it.name}>" else "[${it.name}]" }}" else ""}"))

        if (description.isNotEmpty()) {
            println()
            println(Colors.boldMagenta(description))
        }

        if (options.isNotEmpty()) {
            println()
            println(Colors.boldWhite("Options:"))
            options.forEach { option ->
                val shortName = option.shortName?.let { "-$it, " } ?: "    "
                val required = if (option.required) Colors.dimBlue(" (required)") else ""
                val defaultVal = option.getValueOrDefault()?.let { Colors.boldBlue(" [default: $it]") } ?: ""
                val typeInfo = Colors.yellow(getTypeInfo(option.type))

                println("  $shortName--${option.longName}${typeInfo}")
                option.description?.let { desc ->
                    println(Colors.dimMagenta("        $desc$required$defaultVal"))
                }
                println()
            }
        }

        if (optionalOptions.isNotEmpty()) {
            println()
            println(Colors.boldWhite("Optional Value Options:"))
            optionalOptions.forEach { option ->
                val shortName = option.shortName?.let { "-$it, " } ?: "    "
                println("  $shortName--${option.longName} [value]")
                option.description?.let { desc ->
                    println(Colors.dimMagenta("        $desc (can be used as flag or with value)"))
                }
                println()
            }
        }

        if (flags.isNotEmpty()) {
            println()
            println(Colors.boldWhite("Flags:"))
            flags.forEach { flag ->
                val shortName = flag.shortName?.let { "-$it, " } ?: "    "
                println("  $shortName--${flag.longName}")
                flag.description?.let { desc ->
                    println(Colors.dimMagenta("        $desc"))
                }
                println()
            }
        }

        if (arguments.isNotEmpty()) {
            println()
            println(Colors.boldWhite("Arguments:"))
            arguments.forEach { arg ->
                val required = Colors.dimBlue(if (arg.required) " (required)" else " (optional)")
                val typeInfo = Colors.yellow(getTypeInfo(arg.type))
                println("  ${arg.name}$typeInfo$required")
                arg.description?.let { desc ->
                    println(Colors.dimMagenta("    $desc"))
                }
                println()
            }
        }
    }

    /**
     * Validate all properties in this subcommand
     * @return list of validation errors, empty if all valid
     */
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        (options + flags + arguments).forEach { prop -> 
            prop.getValidationError()?.let { error -> 
                errors.add(error)
            }
        }

        return errors
    }

    /**
     * Type info helper method
     */
    private fun getTypeInfo(type: ArgType<*>): String {
        return when (type) {
            is ArgType.StringType -> " <string>"
            is ArgType.IntType -> " <int>"
            is ArgType.DoubleType -> " <double>"
            is ArgType.BooleanType -> " <bool>"
            is ArgType.IntRange -> " <${type.min}-${type.max}>"
            is ArgType.Choice -> " <${type.choices.joinToString("|")}>"
            is ArgType.OptionalValue -> " [string]"
            is ArgType.FilePath -> " <path>"
        }
    }
}
