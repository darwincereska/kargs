package org.kargs

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Flag(
    val longName: String,
    val shortName: String? = null,
    val description: String = "",
    val default: Boolean = false
) : ReadWriteProperty<Any?, Boolean> {

    private var _value = default

    var value: Boolean
    get() = _value
    set(v) { _value = v }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = _value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) { _value = value }

    fun setFlag() { _value = true }
}

