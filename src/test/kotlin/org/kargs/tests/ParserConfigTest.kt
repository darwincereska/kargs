package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kargs.ArgumentParseException
import org.kargs.Parser
import org.kargs.ParserConfig
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ParserConfigTest {
    
    @Test
    fun `default config values`() {
        val config = ParserConfig.DEFAULT
        assertTrue(config.colorsEnabled)
        assertFalse(config.strictMode)
        assertTrue(config.helpOnEmpty)
        assertTrue(config.caseSensitive)
        assertFalse(config.allowAbbreviations)
    }
    
    @Test
    fun `custom config values`() {
        val config = ParserConfig(
            colorsEnabled = false,
            strictMode = true,
            helpOnEmpty = false,
            caseSensitive = false,
            allowAbbreviations = true
        )
        
        assertFalse(config.colorsEnabled)
        assertTrue(config.strictMode)
        assertFalse(config.helpOnEmpty)
        assertFalse(config.caseSensitive)
        assertTrue(config.allowAbbreviations)
    }
    
    @Test
    fun `strict mode affects unknown options`() {
        val strictParser = Parser("test", ParserConfig(strictMode = true))
        val lenientParser = Parser("test", ParserConfig(strictMode = false))
        
        val testCmd = TestUtils.TestSubcommand()
        strictParser.subcommands(testCmd)
        lenientParser.subcommands(testCmd)
        
        // Strict mode should throw
        assertThrows<ArgumentParseException> {
            strictParser.parse(arrayOf("test", "--unknown", "value", "--required", "req", "input.txt"))
        }
        
        // Lenient mode should warn but continue
        val output = TestUtils.captureOutput {
            lenientParser.parse(arrayOf("test", "--unknown", "value", "--required", "req", "input.txt"))
        }
        assertTrue(output.contains("Warning"))
        assertTrue(testCmd.executed)
    }
    
    @Test
    fun `helpOnEmpty config affects empty args behavior`() {
        val helpParser = Parser("test", ParserConfig(helpOnEmpty = true))
        val noHelpParser = Parser("test", ParserConfig(helpOnEmpty = false))
        
        val testCmd = TestUtils.TestSubcommand()
        helpParser.subcommands(testCmd)
        noHelpParser.subcommands(testCmd)
        
        // With helpOnEmpty = true, should show help
        val helpOutput = TestUtils.captureOutput {
            helpParser.parse(arrayOf())
        }
        assertTrue(helpOutput.contains("Usage:"))
        
        // With helpOnEmpty = false, should do nothing
        val noHelpOutput = TestUtils.captureOutput {
            noHelpParser.parse(arrayOf())
        }
        assertEquals("", noHelpOutput)
    }
    
    @Test
    fun `case sensitivity affects command matching`() {
        val caseSensitiveParser = Parser("test", ParserConfig(caseSensitive = true))
        val caseInsensitiveParser = Parser("test", ParserConfig(caseSensitive = false))
        
        val testCmd = TestUtils.TestSubcommand("Test") // capital T
        caseSensitiveParser.subcommands(testCmd)
        caseInsensitiveParser.subcommands(testCmd)
        
        // Case sensitive should not match "test" to "Test"
        val sensitiveOutput = TestUtils.captureOutput {
            caseSensitiveParser.parse(arrayOf("test", "--required", "value", "input.txt"))
        }
        assertTrue(sensitiveOutput.contains("Unknown command"))
        assertFalse(testCmd.executed)
        
        // Case insensitive should match
        testCmd.executed = false // reset
        caseInsensitiveParser.parse(arrayOf("test", "--required", "value", "input.txt"))
        assertTrue(testCmd.executed)
    }
}

