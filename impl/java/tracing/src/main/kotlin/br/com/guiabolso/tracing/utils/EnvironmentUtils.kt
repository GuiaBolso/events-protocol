package br.com.guiabolso.tracing.utils

object EnvironmentUtils {

    @JvmStatic
    fun getProperty(key: String, default: String) = System.getenv(key) ?: default

    @JvmStatic
    fun getProperty(key: String, default: Int) = System.getenv(key)?.let { it.toInt() } ?: default
}
