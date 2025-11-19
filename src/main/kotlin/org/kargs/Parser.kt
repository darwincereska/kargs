package org.kargs

/**
 * Main argument parser that handles command-line argument parsing and routing to subcommands.
 *
 * @param programName The name of the program (used in help messages)
 * @param config Configuration options for the parser behavior
 */
class Parser(
    val programName: String,
    private val config: ParserConfig = ParserConfig.DEFAULT
) {
    private val commands = mutableListOf<Subcommand>()

    /**
     * Register one or more subcommands with this parser
     */
    fun subcommands(vararg cmds: Subcommand) {
        commands.addAll(cmds)
    }

    /**
     * Parse the provided command-line arguments and execute the appropiate command
     *
     * @param args Array of command-line arguments
     */
    fun parse(args: Array<String>) {
        if (args.isEmpty()) {
            if (config.helpOnEmpty) {
                printGlobalHelp()
            }
            return
        }

        // Check for global help
        if (args.contains("--help") || args.contains("-h")) {
            printGlobalHelp()
            return
        }

        val cmdName = args[0]
        val cmd = findCommand(cmdName)

        if (cmd == null) {
            printError("Unknown command: $cmdName")
            if (!config.strictMode) {
                printGlobalHelp()
            }
            return
        }

        // Check for command specific help
        if (args.contains("--help") || args.contains("-h")) {
            cmd.printHelp()
            return
        }

        try {
            parseCommandArgs(cmd, args.sliceArray(1 until args.size))
            validateRequiredOptions(cmd)
            cmd.execute()
        } catch (e: ArgumentParseException) {
            printError(e.message ?: "Parse error")
            cmd.printHelp()
            throw e
        }
    }

    /**
     * Find a command by name or alias
     */
    private fun findCommand(name: String): Subcommand? {
        val searchName = if (config.caseSensitive) name else name.lowercase()
        return commands.firstOrNull { cmd -> 
            val cmdName = if (config.caseSensitive) cmd.name else cmd.name.lowercase()
            val aliases = if (config.caseSensitive) cmd.aliases else cmd.aliases.map { it.lowercase() }
            cmdName == searchName || aliases.contains(searchName)
        }
    }

    /**
     * Parse arguments for a specific command
     */
    private fun parseCommandArgs(cmd: Subcommand, args: Array<String>) {
        var i = 0
        val positionalArgs = mutableListOf<String>()

        while (i < args.size) {
            val arg = args[i]

            when {
                arg.startsWith("--") -> {
                    val key = arg.removePrefix("--")
                    i = parseLongOption(cmd, key, args, i)
                }

                arg.startsWith("-") && arg.length > 1 -> {
                    val key = arg.removePrefix("-")
                    i = parseShortOption(cmd, key, args, i)
                }

                else -> {
                    positionalArgs.add(arg)
                }
            }
            i++
        }
        // Handle positional arguments
        parsePositionalArguments(cmd, positionalArgs)
    }

    /**
     * Parse a long option (--option)
     */
    private fun parseLongOption(cmd: Subcommand, key: String, args: Array<String>, index: Int): Int {
        val option = cmd.options.firstOrNull { it.longName == key }
        val flag = cmd.flags.firstOrNull { it.longName == key }

        return when {
            option != null -> {
                if (index + 1 >= args.size) {
                    throw ArgumentParseException("Missing value for option --$key")
                }
                try {
                    option.parseValue(args[index + 1])
                    index + 1
                } catch (e: Exception) {
                    throw ArgumentParseException("Invalid value for option --$key: ${e.message}")
                }
            }

            flag != null -> {
                flag.setFlag()
                index
            }

            else -> {
                if (config.strictMode) {
                    throw ArgumentParseException("Unknown option --$key")
                } else {
                    printWarning("Unknown option --$key")
                    index
                }
            }
        }
    }

    /**
     * Parse a short option (-o)
     */
    private fun parseShortOption(cmd: Subcommand, key: String, args: Array<String>, index: Int): Int {
        // Handle combined short flags like -acc
        if (key.length > 1) {
            key.forEach { char -> 
                val flag = cmd.flags.firstOrNull { it.shortName == char.toString() }
                if (flag != null) {
                    flag.setFlag()
                } else if (config.strictMode) {
                    throw ArgumentParseException("Unknown flag -$char")
                }
            }
            return index
        }

        val option = cmd.options.firstOrNull { it.shortName == key }
        val flag = cmd.flags.firstOrNull { it.shortName == key }

        return when {
            option != null -> {
                if (index + 1 >= args.size) {
                    throw ArgumentParseException("Missing value for option -$key")
                }
                try {
                    option.parseValue(args[index + 1])
                    index + 1
                } catch (e: Exception) {
                    throw ArgumentParseException("Invalid value for option -$key: ${e.message}")
                }
            }

            flag != null -> {
                flag.setFlag()
                index
            }

            else -> {
                if (config.strictMode) {
                    throw ArgumentParseException("Unknown option -$key")
                } else {
                    printWarning("Unknown option -$key")
                    index
                }
            }
        }
    }

    /**
     * Parse positional arguments
     */
    private fun parsePositionalArguments(cmd: Subcommand, args: List<String>) {
        val arguments = cmd.arguments

        if (args.size > arguments.size) {
            val extra = args.drop(arguments.size)
            if (config.strictMode) {
                throw ArgumentParseException("Too many arguments: ${extra.joinToString(", ")}")
            } else {
                printWarning("Ignoring extra arguments: ${extra.joinToString(", ")}")
            }
        }

        args.forEachIndexed { index, value -> 
            if (index < arguments.size) {
                try {
                    arguments[index].parseValue(value)
                } catch (e: Exception) {
                    throw ArgumentParseException("Invalid value for argument ${arguments[index].name}: ${e.message}")
                }
            }
        }
    }

    /**
     * Validate that all required options have been provided
     */
    private fun validateRequiredOptions(cmd: Subcommand) {
        val missingRequired = cmd.options.filter { it.required && it.value == null }
        if (missingRequired.isNotEmpty()) {
            val missing = missingRequired.joinToString(", ") { "--${it.longName}" }
            throw ArgumentParseException("Missing required options: $missing")
        }
    }

    /**
     * Print global help menu
     */
    private fun printGlobalHelp() {
        println(colorize("Usage: $programName <command> [options]", Color.BOLD))
        println()
        println(colorize("Commands:", Color.BOLD))
        commands.forEach { cmd -> 
            val aliases = if (cmd.aliases.isNotEmpty()) " (${cmd.aliases.joinToString(", ")})" else ""
            println("  ${colorize(cmd.name, Color.GREEN)}$aliases")
            if (cmd.description.isNotEmpty()) {
                println("    ${cmd.description}")
            }
        }
        println()
        println("Use `$programName <command> --help` for more information about a command.")
    }

    /**
     * Print error message with optional coloring
     */
    private fun printError(message: String) {
        println(colorize("Error: $message", Color.RED))
    }

    /**
     * Print warning message with optional coloring
     */
    private fun printWarning(message: String) {
        println(colorize("Warning: $message", Color.YELLOW))
    }

    /**
     * Apply color to text if colors are enabled
     */
    private fun colorize(text: String, color: Color): String {
        return if (config.colorsEnabled) {
            "${color.code}$text${Color.RESET.code}"
        } else {
            text
        }
    }

    /**
     * ANSI color codes for terminal output
     */
    private enum class Color(val code: String) {
        RESET("\u001B[0m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BOLD("\u001B[1m")
    }
}

/**
 * Custom exception for argument parsing errors
 */
class ArgumentParseException(message: String) : Exception(message)
