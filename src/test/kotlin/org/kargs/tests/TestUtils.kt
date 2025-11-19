package org.kargs.tests

import org.kargs.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Utility functions for testing
 */
object TestUtils {
    
    /**
     * Capture console output during execution
     */
    fun captureOutput(block: () -> Unit): String {
        val originalOut = System.out
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        
        try {
            block()
            return outputStream.toString().trim()
        } finally {
            System.setOut(originalOut)
        }
    }
    
    /**
     * Create a test subcommand for testing purposes
     */
    class TestSubcommand(
        name: String = "test",
        description: String = "Test command",
        aliases: List<String> = emptyList()
    ) : Subcommand(name, description, aliases) {
        
        var executed = false
        var executionData: Map<String, Any?> = emptyMap()
        
        // Test properties
        val stringOpt by Option(ArgType.String, "string", "s", "String option")
        val requiredOpt by Option(ArgType.String, "required", "r", "Required option", required = true)
        val intOpt by Option(ArgType.Int, "number", "n", "Integer option")
        val rangeOpt by Option(ArgType.intRange(1, 10), "range", description = "Range option")
        val choiceOpt by Option(ArgType.choice("a", "b", "c"), "choice", "c", "Choice option")
        val doubleOpt by Option(ArgType.Double, "double", "d", "Double option")
        
        val verboseFlag by Flag("verbose", "v", "Verbose flag")
        val debugFlag by Flag("debug", description = "Debug flag")
        val forceFlag by Flag("force", "f", "Force flag", defaultValue = false)
        
        val inputArg by Argument(ArgType.String, "input", "Input argument")
        val outputArg by Argument(ArgType.String, "output", "Output argument", required = false)
        
        override fun execute() {
            executed = true
            executionData = mapOf(
                "stringOpt" to stringOpt,
                "requiredOpt" to requiredOpt,
                "intOpt" to intOpt,
                "rangeOpt" to rangeOpt,
                "choiceOpt" to choiceOpt,
                "doubleOpt" to doubleOpt,
                "verboseFlag" to verboseFlag,
                "debugFlag" to debugFlag,
                "forceFlag" to forceFlag,
                "inputArg" to inputArg,
                "outputArg" to outputArg
            )
        }
    }
}

