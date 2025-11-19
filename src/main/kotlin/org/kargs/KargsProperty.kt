package org.kargs

import kotlin.reflect.KProperty

/**
 * Base class for all command-line argument properties (options, flags, arguments)
 */
abstract class KargsProperty<T>(val description: String? = null) {
    var value: T? = null
        protected set
    
    internal lateinit var parent: Subcommand

    /**
     * Parse a string value into the appropiate type
     * @throws ArgumentParseException if parsing fails
     */
    abstract fun parseValue(str: String)

    /**
     * Validate the current value
     * @return true if valid, false otherwise
     */
    open fun isValid(): Boolean = true

    /**
     * Get validation error message if value is invalid
     */
    open fun getValidationError(): String? = null

    /**
     * Property delagate setup - registers this property with its parent subcommand
     */
    operator fun provideDelegate(thisRef: Subcommand, prop: KProperty<*>): KargsProperty<T> {
        parent = thisRef
        parent.registerProperty(this)
        return this
    }

    /**
     * Property delagate getter
     */
    operator fun getValue(thisRef: Subcommand, property: KProperty<*>): T? = value

    /*
     * Property delagate setter
     */
    operator fun setValue(thisRef: Subcommand, property: KProperty<*>, value: T?) {
        this.value = value
    }
}
