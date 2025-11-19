package org.kargs.tests

import org.junit.jupiter.api.Test
import org.kargs.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class IntegrationTest {
    
    class ComplexCommand : Subcommand("complex", "Complex test command", listOf("c")) {
        val input by Argument(ArgType.String, "input", "Input file")
        val output by Argument(ArgType.String, "output", "Output file", required = false)
        
        val format by Option(ArgType.choice("json", "xml", "yaml"), "format", "f", "Output format", defaultValue = "json")
        val threads by Option(ArgType.intRange(1, 32), "threads", "t", "Thread count", defaultValue = 4)
        val timeout by Option(ArgType.Double, "timeout", description = "Timeout in seconds")
        val config by Option(ArgType.String, "config", "c", "Config file", required = true)
        
        val verbose by Flag("verbose", "v", "Verbose output")
        val dryRun by Flag("dry-run", "n", "Dry run mode")
        val force by Flag("force", "f", "Force operation")
        
        var executed = false
        
        override fun execute() {
            executed = true
        }
    }
    
    @Test
    fun `complex command with all features`() {
        val parser = Parser("myapp")
        val cmd = ComplexCommand()
        parser.subcommands(cmd)
        
        parser.parse(arrayOf(
            "complex",
            "--config", "/path/to/config.yml",
            "--format", "xml",
            "--threads", "8",
            "--timeout", "30.5",
            "--verbose",
            "--dry-run",
            "input.txt",
            "output.xml"
        ))
        
        assertTrue(cmd.executed)
        assertEquals("input.txt", cmd.input)
        assertEquals("output.xml", cmd.output)
        assertEquals("/path/to/config.yml", cmd.config)
        assertEquals("xml", cmd.format)
        assertEquals(8, cmd.threads)
        assertEquals(30.5, cmd.timeout)
        assertEquals(true, cmd.verbose)
        assertEquals(true, cmd.dryRun)
        assertEquals(false, cmd.force)
    }
    
    @Test
    fun `complex command with short options and combined flags`() {
        val parser = Parser("myapp")
        val cmd = ComplexCommand()
        parser.subcommands(cmd)
        
        parser.parse(arrayOf(
            "c", // using alias
            "-c", "/config.yml",
            "-f", "yaml",
            "-t", "16",
            "-vnf", // combined flags: verbose, dry-run (n), force
            "input.txt"
        ))
        
        assertTrue(cmd.executed)
        assertEquals("input.txt", cmd.input)
        assertEquals("/config.yml", cmd.config)
        assertEquals("yaml", cmd.format)
        assertEquals(16, cmd.threads)
        assertEquals(true, cmd.verbose)
        assertEquals(true, cmd.dryRun)
        assertEquals(true, cmd.force)
    }
    
    @Test
    fun `multiple commands in same parser`() {
        val parser = Parser("tool")
        val buildCmd = TestUtils.TestSubcommand("build", "Build project")
        val testCmd = TestUtils.TestSubcommand("test", "Run tests")
        val deployCmd = TestUtils.TestSubcommand("deploy", "Deploy project")
        
        parser.subcommands(buildCmd, testCmd, deployCmd)
        
        // Test build command
        parser.parse(arrayOf("build", "--required", "value", "src/"))
        assertTrue(buildCmd.executed)
        
        // Reset and test different command
        buildCmd.executed = false
        parser.parse(arrayOf("test", "--required", "value", "tests/"))
        assertTrue(testCmd.executed)
        assertFalse(buildCmd.executed) // should not be executed again
    }
    
    @Test
    fun `validation works end-to-end`() {
        val parser = Parser("app")
        val cmd = ComplexCommand()
        parser.subcommands(cmd)
        
        // Should validate all constraints
        parser.parse(arrayOf(
            "complex",
            "--config", "config.yml",
            "--format", "json", // valid choice
            "--threads", "8",   // valid range
            "--timeout", "15.5", // valid double
            "input.txt"
        ))
        
        assertTrue(cmd.executed)
        assertEquals("json", cmd.format)
        assertEquals(8, cmd.threads)
        assertEquals(15.5, cmd.timeout)
    }
}

