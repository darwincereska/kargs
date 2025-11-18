package org.kargs

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Option<T>(
    val type: ArgType<T>,
    val longName: String,
    val shortName: String? = null,
    val description: String = "",
    val required: Boolean = false,
    val default: T? = null
) : ReadWriteProperty<Any?, T?> {

    private var _value: T? = default

    var value: T?
    get() = _value
    set(v) { _value = v }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = _value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) { _value = value }

    fun parseValue(input: String) {
        _value = type.convert(input)
    }
}

