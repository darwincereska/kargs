package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.kargs.ArgType
import org.kargs.ArgumentParseException
import kotlin.test.assertEquals

class ArgTypeTest {
    
    @Test
    fun `String type converts correctly`() {
        val type = ArgType.String
        assertEquals("hello", type.convert("hello"))
        assertEquals("", type.convert(""))
        assertEquals("123", type.convert("123"))
    }
    
    @Test
    fun `Int type converts valid integers`() {
        val type = ArgType.Int
        assertEquals(42, type.convert("42"))
        assertEquals(-10, type.convert("-10"))
        assertEquals(0, type.convert("0"))
    }
    
    @Test
    fun `Int type throws on invalid input`() {
        val type = ArgType.Int
        assertThrows<ArgumentParseException> { type.convert("not-a-number") }
        assertThrows<ArgumentParseException> { type.convert("12.5") }
        assertThrows<ArgumentParseException> { type.convert("") }
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["true", "TRUE", "yes", "YES", "1", "on", "ON"])
    fun `Boolean type converts true values`(input: String) {
        val type = ArgType.Boolean
        assertEquals(true, type.convert(input))
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["false", "FALSE", "no", "NO", "0", "off", "OFF"])
    fun `Boolean type converts false values`(input: String) {
        val type = ArgType.Boolean
        assertEquals(false, type.convert(input))
    }
    
    @Test
    fun `Boolean type throws on invalid input`() {
        val type = ArgType.Boolean
        assertThrows<ArgumentParseException> { type.convert("maybe") }
        assertThrows<ArgumentParseException> { type.convert("2") }
    }
    
    @Test
    fun `Double type converts correctly`() {
        val type = ArgType.Double
        assertEquals(3.14, type.convert("3.14"))
        assertEquals(-2.5, type.convert("-2.5"))
        assertEquals(42.0, type.convert("42"))
    }
    
    @Test
    fun `Double type throws on invalid input`() {
        val type = ArgType.Double
        assertThrows<ArgumentParseException> { type.convert("not-a-number") }
    }
    
    @Test
    fun `IntRange validates bounds`() {
        val type = ArgType.intRange(1, 10)
        assertEquals(5, type.convert("5"))
        assertEquals(1, type.convert("1"))
        assertEquals(10, type.convert("10"))
        
        assertThrows<ArgumentParseException> { type.convert("0") }
        assertThrows<ArgumentParseException> { type.convert("11") }
        assertThrows<ArgumentParseException> { type.convert("-5") }
    }
    
    @Test
    fun `Choice validates options`() {
        val type = ArgType.choice("red", "green", "blue")
        assertEquals("red", type.convert("red"))
        assertEquals("green", type.convert("green"))
        assertEquals("blue", type.convert("blue"))
        
        assertThrows<ArgumentParseException> { type.convert("yellow") }
        assertThrows<ArgumentParseException> { type.convert("RED") } // case sensitive
    }
    
    @Test
    fun `IntRange provides correct validation description`() {
        val type = ArgType.intRange(5, 15)
        assertEquals("integer between 5 and 15", type.getValidationDescription())
    }
    
    @Test
    fun `Choice provides correct validation description`() {
        val type = ArgType.choice("apple", "banana", "cherry")
        assertEquals("one of: apple, banana, cherry", type.getValidationDescription())
    }
}

