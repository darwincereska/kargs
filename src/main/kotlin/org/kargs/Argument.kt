package org.kargs

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Argument<T>(
    val type: ArgType<T>,
    val name: String,
    val description: String = "",
    val required: Boolean = true
) : ReadWriteProperty<Any?, T?> {

    private var _value: T? = null

    var value: T?
    get() = _value
    set(v) { _value = v }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = _value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) { _value = value }

    fun parseValue(input: String) {
        _value = type.convert(input)
    }
}

