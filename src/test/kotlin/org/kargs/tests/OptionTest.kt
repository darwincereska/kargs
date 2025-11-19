package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kargs.ArgType
import org.kargs.Option
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

class OptionTest {
    
    @Test
    fun `option creation with valid parameters`() {
        val option = Option(ArgType.String, "output", "o", "Output file", required = true)
        assertEquals("output", option.longName)
        assertEquals("o", option.shortName)
        assertEquals("Output file", option.description)
        assertTrue(option.required)
    }
    
    @Test
    fun `option with default value`() {
        val option = Option(ArgType.Int, "threads", "t", "Thread count", defaultValue = 4)
        assertEquals(4, option.value)
        assertEquals(4, option.getValueOrDefault())
        assertFalse(option.isSet())
    }
    
    @Test
    fun `option parsing updates value`() {
        val option = Option(ArgType.String, "name", "n")
        assertNull(option.value)
        
        option.parseValue("test")
        assertEquals("test", option.value)
        assertTrue(option.isSet())
    }
    
    @Test
    fun `option validation for required field`() {
        val option = Option(ArgType.String, "required", required = true)
        assertFalse(option.isValid())
        assertEquals("Option --required is required", option.getValidationError())
        
        option.parseValue("value")
        assertTrue(option.isValid())
        assertNull(option.getValidationError())
    }
    
    @Test
    fun `option throws on invalid names`() {
        assertThrows<IllegalArgumentException> {
            Option(ArgType.String, "", "o") // blank long name
        }
        
        assertThrows<IllegalArgumentException> {
            Option(ArgType.String, "--invalid", "o") // long name with dashes
        }
        
        assertThrows<IllegalArgumentException> {
            Option(ArgType.String, "valid", "ab") // short name too long
        }
        
        assertThrows<IllegalArgumentException> {
            Option(ArgType.String, "valid", "-o") // short name with dash
        }
    }
    
    @Test
    fun `option with range type validation`() {
        val option = Option(ArgType.intRange(1, 10), "count", "c", "Item count")
        
        option.parseValue("5")
        assertEquals(5, option.value)
        assertTrue(option.isValid())
        
        // Test that the option itself doesn't validate the range (ArgType does)
        // The validation happens during parsing
    }
    
    @Test
    fun `option isSet works correctly with defaults`() {
        val option = Option(ArgType.String, "mode", defaultValue = "default")
        assertFalse(option.isSet()) // has default but not explicitly set
        
        option.parseValue("custom")
        assertTrue(option.isSet()) // now explicitly set
        
        option.parseValue("default") // set to same as default
        assertTrue(option.isSet()) // still considered "set"
    }
}

