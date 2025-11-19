package org.kargs.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.kargs.ArgType
import org.kargs.ArgumentParseException
import kotlin.test.assertEquals
import kotlin.io.path.createTempFile
import kotlin.io.path.createTempDirectory

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

    @Test
    fun `FilePath converts string to File`() {
        val type = ArgType.filePath()
        val result = type.convert("/path/to/file.txt")
        assertEquals("/path/to/file.txt", result.path)
    }

    @Test
    fun `FilePath with no constraints allows any path`() {
        val type = ArgType.filePath()
        // Should not throw for non-existent files
        val result = type.convert("/non/existent/path")
        assertEquals("/non/existent/path", result.path)
        assertEquals(true, type.validate(result))
    }

    @Test
    fun `existingFile validates file exists and is file`() {
        val type = ArgType.existingFile()

        // Create a temporary file for testing
        val tempFile = kotlin.io.path.createTempFile().toFile()
        tempFile.writeText("test content")

        try {
            // Should work with existing file
            val result = type.convert(tempFile.absolutePath)
            assertEquals(true, type.validate(result))

            // Should fail with non-existent file
            val nonExistent = type.convert("/non/existent/file.txt")
            assertEquals(false, type.validate(nonExistent))

        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `existingFile fails validation on directory`() {
        val type = ArgType.existingFile()

        // Create a temporary directory
        val tempDir = kotlin.io.path.createTempDirectory().toFile()

        try {
            val result = type.convert(tempDir.absolutePath)
            assertEquals(false, type.validate(result)) // Should fail because it's a directory, not a file

        } finally {
            tempDir.delete()
        }
    }

    @Test
    fun `existingDirectory validates directory exists and is directory`() {
        val type = ArgType.existingDirectory()

        // Create a temporary directory
        val tempDir = kotlin.io.path.createTempDirectory().toFile()

        try {
            // Should work with existing directory
            val result = type.convert(tempDir.absolutePath)
            assertEquals(true, type.validate(result))

            // Should fail with non-existent directory
            val nonExistent = type.convert("/non/existent/directory")
            assertEquals(false, type.validate(nonExistent))

        } finally {
            tempDir.delete()
        }
    }

    @Test
    fun `existingDirectory fails validation on file`() {
        val type = ArgType.existingDirectory()

        // Create a temporary file
        val tempFile = kotlin.io.path.createTempFile().toFile()
        tempFile.writeText("test")

        try {
            val result = type.convert(tempFile.absolutePath)
            assertEquals(false, type.validate(result)) // Should fail because it's a file, not a directory

        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readableFile validates file is readable`() {
        val type = ArgType.readableFile()

        // Create a temporary file
        val tempFile = kotlin.io.path.createTempFile().toFile()
        tempFile.writeText("test content")

        try {
            // Should work with readable file
            val result = type.convert(tempFile.absolutePath)
            assertEquals(true, type.validate(result))

            // Make file unreadable (this might not work on all systems)
            tempFile.setReadable(false)
            assertEquals(false, type.validate(result))

        } finally {
            tempFile.setReadable(true) // Restore permissions
            tempFile.delete()
        }
    }

    @Test
    fun `writableFile validates file is writable`() {
        val type = ArgType.writableFile()

        // Create a temporary file
        val tempFile = kotlin.io.path.createTempFile().toFile()
        tempFile.writeText("test content")

        try {
            // Should work with writable file
            val result = type.convert(tempFile.absolutePath)
            assertEquals(true, type.validate(result))

            // Make file read-only (this might not work on all systems)
            tempFile.setWritable(false)
            assertEquals(false, type.validate(result))

        } finally {
            tempFile.setWritable(true) // Restore permissions
            tempFile.delete()
        }
    }

    @Test
    fun `FilePath validation description includes constraints`() {
        assertEquals("file path", ArgType.filePath().getValidationDescription())
        assertEquals("file path (must exist, must be a file)", ArgType.existingFile().getValidationDescription())
        assertEquals("file path (must exist, must be a directory)", ArgType.existingDirectory().getValidationDescription())
        assertEquals("file path (must exist, must be a file, must be readable)", ArgType.readableFile().getValidationDescription())
        assertEquals("file path (must be writable)", ArgType.writableFile().getValidationDescription())
    }

    @Test
    fun `FilePath with multiple constraints`() {
        val type = ArgType.FilePath(
            mustExist = true,
            mustBeFile = true,
            mustBeReadable = true,
            mustBeWritable = true
        )

        val tempFile = kotlin.io.path.createTempFile().toFile()
        tempFile.writeText("test")

        try {
            val result = type.convert(tempFile.absolutePath)
            assertEquals(true, type.validate(result))

            assertEquals(
                "file path (must exist, must be a file, must be readable, must be writable)",
                type.getValidationDescription()
            )

        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `FilePath handles non-existent parent directories`() {
        val type = ArgType.writableFile()

        // Path with non-existent parent directory
        val result = type.convert("/non/existent/parent/file.txt")

        // Should convert fine (validation happens separately)
        assertEquals("/non/existent/parent/file.txt", result.path)

        // Validation should fail because parent doesn't exist
        assertEquals(false, type.validate(result))
    }
}

