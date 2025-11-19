package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kargs.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

class SubcommandTest {
    
    @Test
    fun `subcommand creation with valid parameters`() {
        val cmd = TestUtils.TestSubcommand("build", "Build the project", listOf("b", "compile"))
        assertEquals("build", cmd.name)
        assertEquals("Build the project", cmd.description)
        assertEquals(listOf("b", "compile"), cmd.aliases)
    }
    
    @Test
    fun `subcommand throws on blank name`() {
        assertThrows<IllegalArgumentException> {
            TestUtils.TestSubcommand("", "Description")
        }
        
        assertThrows<IllegalArgumentException> {
            TestUtils.TestSubcommand("   ", "Description") // whitespace only
        }
    }
    
    @Test
    fun `subcommand registers properties correctly`() {
        val cmd = TestUtils.TestSubcommand()
        
        // Properties should be registered automatically via delegates
        assertTrue(cmd.options.isNotEmpty())
        assertTrue(cmd.flags.isNotEmpty())
        assertTrue(cmd.arguments.isNotEmpty())
        
        // Check specific properties are registered
        assertTrue(cmd.options.any { it.longName == "required" })
        assertTrue(cmd.flags.any { it.longName == "verbose" })
        assertTrue(cmd.arguments.any { it.name == "input" })
    }
    
    @Test
    fun `subcommand validation works`() {
        val cmd = TestUtils.TestSubcommand()
        
        // Should have validation errors for required fields
        val errors = cmd.validate()
        assertTrue(errors.isNotEmpty())
        assertTrue(errors.any { it.contains("required") })
        
        // Set required values through parsing (the proper way)
        val requiredOption = cmd.options.find { it.longName == "required" }
        val inputArgument = cmd.arguments.find { it.name == "input" }
        
        requiredOption?.parseValue("value")
        inputArgument?.parseValue("input.txt")
        
        // Should now be valid
        val errorsAfter = cmd.validate()
        assertTrue(errorsAfter.isEmpty())
    }
    
    @Test
    fun `subcommand help generation`() {
        val cmd = TestUtils.TestSubcommand("test", "Test command for validation")
        
        val helpOutput = TestUtils.captureOutput {
            cmd.printHelp()
        }
        
        assertTrue(helpOutput.contains("Usage: test [options]"))
        assertTrue(helpOutput.contains("Test command for validation"))
        assertTrue(helpOutput.contains("Options:"))
        assertTrue(helpOutput.contains("--required"))
        assertTrue(helpOutput.contains("Flags:"))
        assertTrue(helpOutput.contains("--verbose"))
        assertTrue(helpOutput.contains("Arguments:"))
        assertTrue(helpOutput.contains("input"))
    }
    
    @Test
    fun `subcommand property access works`() {
        val cmd = TestUtils.TestSubcommand()
        
        // Initially null/default values
        assertNull(cmd.requiredOpt)
        assertEquals(false, cmd.verboseFlag)
        assertNull(cmd.inputArg)
        
        // Set values through parsing (simulating real usage)
        val requiredOption = cmd.options.find { it.longName == "required" }
        val verboseFlag = cmd.flags.find { it.longName == "verbose" }
        val inputArgument = cmd.arguments.find { it.name == "input" }
        
        requiredOption?.parseValue("test")
        verboseFlag?.setFlag()
        inputArgument?.parseValue("file.txt")
        
        // Values should be accessible
        assertEquals("test", cmd.requiredOpt)
        assertEquals(true, cmd.verboseFlag)
        assertEquals("file.txt", cmd.inputArg)
    }
    
    @Test
    fun `subcommand property types are correct`() {
        val cmd = TestUtils.TestSubcommand()
        
        // Check that we can find properties by their characteristics
        val stringOption = cmd.options.find { it.longName == "string" }
        val intOption = cmd.options.find { it.longName == "number" }
        val rangeOption = cmd.options.find { it.longName == "range" }
        val choiceOption = cmd.options.find { it.longName == "choice" }
        
        assertTrue(stringOption != null)
        assertTrue(intOption != null)
        assertTrue(rangeOption != null)
        assertTrue(choiceOption != null)
        
        // Test parsing different types
        stringOption?.parseValue("hello")
        intOption?.parseValue("42")
        rangeOption?.parseValue("5")
        choiceOption?.parseValue("b")
        
        assertEquals("hello", cmd.stringOpt)
        assertEquals(42, cmd.intOpt)
        assertEquals(5, cmd.rangeOpt)
        assertEquals("b", cmd.choiceOpt)
    }
    
    @Test
    fun `subcommand execution tracking works`() {
        val cmd = TestUtils.TestSubcommand()
        
        assertFalse(cmd.executed)
        assertTrue(cmd.executionData.isEmpty())
        
        // Set some values and execute
        val requiredOption = cmd.options.find { it.longName == "required" }
        val inputArgument = cmd.arguments.find { it.name == "input" }
        
        requiredOption?.parseValue("test")
        inputArgument?.parseValue("input.txt")
        
        cmd.execute()
        
        assertTrue(cmd.executed)
        assertTrue(cmd.executionData.isNotEmpty())
        assertEquals("test", cmd.executionData["requiredOpt"])
        assertEquals("input.txt", cmd.executionData["inputArg"])
    }
}

