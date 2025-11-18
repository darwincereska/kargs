package org.kargs

class Parser(val programName: String) {
    private val commands = mutableListOf<Subcommand>()

    fun subcommands(vararg cmds: Subcommand) {
        commands.addAll(cmds)
    }

    fun parse(args: Array<String>) {
        if (args.isEmpty()) {
            printGlobalHelp()
            return
        }

        val cmdName = args[0]
        val cmd = commands.firstOrNull { it.name == cmdName || it.aliases.contains(cmdName) }

        if (cmd == null) {
            println("Unknown command: $cmdName")
            printGlobalHelp()
            return
        }

        if (args.contains("--help") || args.contains("-h")) {
            cmd.printHelp()
            return
        }

        // Reflection: find all Option, Flag, Argument properties
        val props = cmd::class.members.filterIsInstance<kotlin.reflect.KProperty<*>>()
        val options = props.mapNotNull { it.getter.call(cmd) as? Option<*> }
        val flags = props.mapNotNull { it.getter.call(cmd) as? Flag }
        val arguments = props.mapNotNull { it.getter.call(cmd) as? Argument<*> }

        // Parse args starting at index 1
        var i = 1
        while (i < args.size) {
            val arg = args[i]
            when {
                arg.startsWith("--") -> {
                    val key = arg.removePrefix("--")
                    val option = options.firstOrNull { it.longName == key }
                    val flag = flags.firstOrNull { it.longName == key }

                    when {
                        option != null -> {
                            i++
                            if (i >= args.size) throw IllegalArgumentException("Missing value for option --$key")
                            option.parseValue(args[i])
                        }
                        flag != null -> flag.setFlag()
                        else -> println("Unknown option --$key")
                    }
                }

                arg.startsWith("-") -> {
                    val key = arg.removePrefix("-")
                    val option = options.firstOrNull { it.shortName == key }
                    val flag = flags.firstOrNull { it.shortName == key }

                    when {
                        option != null -> {
                            i++
                            if (i >= args.size) throw IllegalArgumentException("Missing value for option -$key")
                            option.parseValue(args[i])
                        }
                        flag != null -> flag.setFlag()
                        else -> println("Unknown option -$key")
                    }
                }

                else -> {
                    // Positional arguments
                    val nextArg = arguments.firstOrNull { it.value == null }
                    if (nextArg != null) {
                        nextArg.parseValue(arg)
                    } else {
                        println("Unexpected argument: $arg")
                    }
                }
            }
            i++
        }

        // Execute the command
        cmd.execute()
    }

    private fun printGlobalHelp() {
        println("Usage: $programName <command> [options]")
        println("\nCommands:")
        commands.forEach {
            println("  ${it.name}\t${it.description}")
        }
    }
}

