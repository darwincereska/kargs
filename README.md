# kargs
All-in-one tool for building cli applications in Kotlin

# Installation
```kotlin
/** build.gradle.kts */
repositories {
  mavenCentral()
}

dependencies {
  implementation("org.kargs:kargs:version")
}
```

# Usage

## Parser
```kotlin
// Main.kt
import org.kargs.*

fun main(args: Array<String>) {
  val parser = Parser("program name")

  // Register subcommands
  parser.subcommands(
    TestCommand1(),
    TestCommand2(),
    ...
  )

  parser.parse(args)
```

## Subcommand
```kotlin
// Subcommand.kt
import org.kargs.*

class TestCommand : Subcommand("name", "description", aliases = listOf("alias1", "alias2")) {
  override fun execute() {
    println("Logic")
  }
}
