package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kargs.ArgumentParseException
import org.kargs.Flag
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class FlagTest {
    
    @Test
    fun `flag creation with default false`() {
        val flag = Flag("verbose", "v", "Verbose output")
        assertEquals("verbose", flag.longName)
        assertEquals("v", flag.shortName)
        assertEquals("Verbose output", flag.description)
        assertEquals(false, flag.value)
        assertFalse(flag.isSet())
    }
    
    @Test
    fun `flag creation with custom default`() {
        val flag = Flag("enabled", defaultValue = true)
        assertEquals(true, flag.value)
        assertFalse(flag.isSet()) // not explicitly set, just default
    }
    
    @Test
    fun `setFlag updates value`() {
        val flag = Flag("debug", "d")
        assertEquals(false, flag.value)
        
        flag.setFlag()
        assertEquals(true, flag.value)
        assertTrue(flag.isSet())
    }
    
    @Test
    fun `parseValue handles boolean strings`() {
        val flag = Flag("test")
        
        flag.parseValue("true")
        assertEquals(true, flag.value)
        
        flag.parseValue("false")
        assertEquals(false, flag.value)
        
        flag.parseValue("yes")
        assertEquals(true, flag.value)
        
        flag.parseValue("no")
        assertEquals(false, flag.value)
        
        flag.parseValue("1")
        assertEquals(true, flag.value)
        
        flag.parseValue("0")
        assertEquals(false, flag.value)
    }
    
    @Test
    fun `parseValue throws on invalid input`() {
        val flag = Flag("test")
        assertThrows<ArgumentParseException> {
            flag.parseValue("maybe")
        }
        
        assertThrows<ArgumentParseException> {
            flag.parseValue("2")
        }
    }
    
    @Test
    fun `flag throws on invalid names`() {
        assertThrows<IllegalArgumentException> {
            Flag("", "v") // blank long name
        }
        
        assertThrows<IllegalArgumentException> {
            Flag("--invalid") // long name with dashes
        }
        
        assertThrows<IllegalArgumentException> {
            Flag("valid", "ab") // short name too long
        }
        
        assertThrows<IllegalArgumentException> {
            Flag("valid", "-v") // short name with dash
        }
    }
    
    @Test
    fun `flag isSet works correctly with defaults`() {
        val flag = Flag("test", defaultValue = true)
        assertFalse(flag.isSet()) // has default but not explicitly set
        
        flag.setFlag()
        assertTrue(flag.isSet()) // now explicitly set
    }
}

