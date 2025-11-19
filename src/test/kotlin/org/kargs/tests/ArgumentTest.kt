package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kargs.Argument
import org.kargs.ArgType
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ArgumentTest {
    
    @Test
    fun `argument creation`() {
        val arg = Argument(ArgType.String, "input", "Input file")
        assertEquals("input", arg.name)
        assertEquals("Input file", arg.description)
        assertTrue(arg.required)
    }
    
    @Test
    fun `optional argument`() {
        val arg = Argument(ArgType.String, "output", "Output file", required = false)
        assertFalse(arg.required)
        assertTrue(arg.isValid()) // optional and null is valid
    }
    
    @Test
    fun `argument parsing`() {
        val arg = Argument(ArgType.Int, "count", "Item count")
        assertNull(arg.value)
        
        arg.parseValue("42")
        assertEquals(42, arg.value)
    }
    
    @Test
    fun `required argument validation`() {
        val arg = Argument(ArgType.String, "required", "Required arg", required = true)
        assertFalse(arg.isValid())
        assertEquals("Argument 'required' is required", arg.getValidationError())
        
        arg.parseValue("value")
        assertTrue(arg.isValid())
        assertNull(arg.getValidationError())
    }
    
    @Test
    fun `argument throws on blank name`() {
        assertThrows<IllegalArgumentException> {
            Argument(ArgType.String, "", "Description")
        }
        
        assertThrows<IllegalArgumentException> {
            Argument(ArgType.String, "   ", "Description") // whitespace only
        }
    }
    
    @Test
    fun `argument with choice type`() {
        val arg = Argument(ArgType.choice("red", "green", "blue"), "color", "Color choice")
        
        arg.parseValue("red")
        assertEquals("red", arg.value)
        assertTrue(arg.isValid())
    }
    
    @Test
    fun `argument with range type`() {
        val arg = Argument(ArgType.intRange(1, 100), "percentage", "Percentage value")
        
        arg.parseValue("50")
        assertEquals(50, arg.value)
        assertTrue(arg.isValid())
    }
}

