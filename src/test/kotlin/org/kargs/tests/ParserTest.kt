package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.kargs.ArgumentParseException
import org.kargs.Parser
import org.kargs.ParserConfig
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ParserTest {
    
    private lateinit var parser: Parser
    private lateinit var testCmd: TestUtils.TestSubcommand
    
    @BeforeEach
    fun setup() {
        parser = Parser("testapp")
        testCmd = TestUtils.TestSubcommand()
        parser.subcommands(testCmd)
    }
    
    @Test
    fun `parse basic command with required option`() {
        parser.parse(arrayOf("test", "--required", "value", "input.txt"))
        
        assertTrue(testCmd.executed)
        assertEquals("value", testCmd.requiredOpt)
        assertEquals("input.txt", testCmd.inputArg)
    }
    
    @Test
    fun `parse command with short options`() {
        parser.parse(arrayOf("test", "-r", "required", "-s", "string", "-n", "42", "input.txt"))
        
        assertTrue(testCmd.executed)
        assertEquals("required", testCmd.requiredOpt)
        assertEquals("string", testCmd.stringOpt)
        assertEquals(42, testCmd.intOpt)
        assertEquals("input.txt", testCmd.inputArg)
    }
    
    @Test
    fun `parse command with flags`() {
        parser.parse(arrayOf("test", "--required", "value", "--verbose", "--debug", "input.txt"))
        
        assertTrue(testCmd.executed)
        assertEquals(true, testCmd.verboseFlag)
        assertEquals(true, testCmd.debugFlag)
        assertEquals(false, testCmd.forceFlag) // not set, should be default
    }
    
    @Test
    fun `parse command with combined short flags`() {
        parser.parse(arrayOf("test", "-r", "value", "-vf", "input.txt"))
        
        assertTrue(testCmd.executed)
        assertEquals("value", testCmd.requiredOpt)
        assertEquals(true, testCmd.verboseFlag)
        assertEquals(true, testCmd.forceFlag)
    }
    
    @Test
    fun `parse command with all argument types`() {
        parser.parse(arrayOf(
            "test", 
            "--required", "req",
            "--string", "hello",
            "--number", "123",
            "--range", "5",
            "--choice", "b",
            "--double", "3.14",
            "--verbose",
            "input.txt",
            "output.txt"
        ))
        
        assertTrue(testCmd.executed)
        assertEquals("req", testCmd.requiredOpt)
        assertEquals("hello", testCmd.stringOpt)
        assertEquals(123, testCmd.intOpt)
        assertEquals(5, testCmd.rangeOpt)
        assertEquals("b", testCmd.choiceOpt)
        assertEquals(3.14, testCmd.doubleOpt)
        assertEquals(true, testCmd.verboseFlag)
        assertEquals("input.txt", testCmd.inputArg)
        assertEquals("output.txt", testCmd.outputArg)
    }
    
    @Test
    fun `throws exception when required option missing`() {
        assertThrows<ArgumentParseException> {
            parser.parse(arrayOf("test", "input.txt"))
        }
        assertFalse(testCmd.executed)
    }
    
    @Test
    fun `throws exception on invalid integer`() {
        assertThrows<ArgumentParseException> {
            parser.parse(arrayOf("test", "--required", "value", "--number", "not-a-number", "input.txt"))
        }
    }
    
    @Test
    fun `throws exception on out of range value`() {
        assertThrows<ArgumentParseException> {
            parser.parse(arrayOf("test", "--required", "value", "--range", "15", "input.txt"))
        }
    }
    
    @Test
    fun `throws exception on invalid choice`() {
        assertThrows<ArgumentParseException> {
            parser.parse(arrayOf("test", "--required", "value", "--choice", "invalid", "input.txt"))
        }
    }
    
    @Test
    fun `handles unknown command`() {
        val output = TestUtils.captureOutput {
            parser.parse(arrayOf("unknown", "arg"))
        }
        assertTrue(output.contains("Unknown command: unknown"))
    }
    
    @Test
    fun `shows global help on empty args`() {
        val output = TestUtils.captureOutput {
            parser.parse(arrayOf())
        }
        assertTrue(output.contains("Usage: testapp <command>"))
        assertTrue(output.contains("test"))
    }
    
    @Test
    fun `shows global help on --help`() {
        val output = TestUtils.captureOutput {
            parser.parse(arrayOf("--help"))
        }
        assertTrue(output.contains("Usage: testapp <command>"))
    }
    
    @Test
    fun `handles command aliases`() {
        val aliasCmd = TestUtils.TestSubcommand("build", "Build command", listOf("b", "compile"))
        parser.subcommands(aliasCmd)
        
        // Test main name
        parser.parse(arrayOf("build", "--required", "value", "input.txt"))
        assertTrue(aliasCmd.executed)
        
        // Reset and test alias
        aliasCmd.executed = false
        parser.parse(arrayOf("b", "--required", "value", "input.txt"))
        assertTrue(aliasCmd.executed)
    }
    
    @Test
    fun `handles missing option value`() {
        assertThrows<ArgumentParseException> {
            parser.parse(arrayOf("test", "--required"))
        }
    }
    
    @Test
    fun `handles missing short option value`() {
        assertThrows<ArgumentParseException> {
            parser.parse(arrayOf("test", "-r"))
        }
    }
}

