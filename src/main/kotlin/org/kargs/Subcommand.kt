package org.kargs

abstract class Subcommand(
    val name: String,
    val description: String = "",
    val aliases: List<String> = emptyList()
) {
    abstract fun execute()

    open fun printHelp() {
        println("Usage: $name [options] [arguments]")
        if (description.isNotEmpty()) println(description)

        val props = this::class.members.filterIsInstance<kotlin.reflect.KProperty<*>>()
        val options = props.mapNotNull { it.getter.call(this) as? Option<*> }
        val flags = props.mapNotNull { it.getter.call(this) as? Flag }
        val arguments = props.mapNotNull { it.getter.call(this) as? Argument<*> }

        if (options.isNotEmpty()) {
            println("\nOptions:")
            options.forEach { o ->
                println("  ${o.shortName?.let { "-$it, " } ?: ""}--${o.longName}\t${o.description}")
            }
        }

        if (flags.isNotEmpty()) {
            println("\nFlags:")
            flags.forEach { f ->
                println("  ${f.shortName?.let { "-$it, " } ?: ""}--${f.longName}\t${f.description}")
            }
        }

        if (arguments.isNotEmpty()) {
            println("\nArguments:")
            arguments.forEach { a ->
                println("  ${a.name}\t${a.description}")
            }
        }
    }
}

