package br.com.guiabolso.events.validation

import br.com.guiabolso.events.exception.MissingRequiredParameterException
import kotlin.reflect.full.memberProperties

fun <T> T?.required(name: String): T {
    return this ?: throw MissingRequiredParameterException(name)
}

fun validateInput(data: Any?, path: String = "payload") {
    if (data == null) throw MissingRequiredParameterException("Missing required property $path.")

    val klass = data::class
    if (klass.isData || klass.annotations.any { it is Validatable }) {
        klass.memberProperties.forEach { prop ->
            val currentPath = "$path.${prop.name}"
            val currentValue = prop.getter.call(data)
            if (!prop.returnType.isMarkedNullable && currentValue == null) {
                throw MissingRequiredParameterException("Missing required property $currentPath.")
            }
            if (currentValue != null) {
                validateInput(currentValue, currentPath)
            }
        }
    }
}