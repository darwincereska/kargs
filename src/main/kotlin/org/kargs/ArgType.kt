package org.kargs

sealed class ArgType<T>(val typeName: String) {
    abstract fun convert(value: String): T

    object StringType : ArgType<kotlin.String>("String") {
        override fun convert(value: String) = value
    }

    object IntType : ArgType<kotlin.Int>("Int") {
        override fun convert(value: String) = value.toInt()
    }

    object BooleanType : ArgType<kotlin.Boolean>("Boolean") {
        override fun convert(value: String) = value.toBoolean()
    }

    companion object {
        val String: ArgType<kotlin.String> = StringType
        val Int: ArgType<kotlin.Int> = IntType
        val Boolean: ArgType<kotlin.Boolean> = BooleanType
    }
}
