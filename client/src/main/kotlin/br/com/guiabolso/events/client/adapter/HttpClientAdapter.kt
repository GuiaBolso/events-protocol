package br.com.guiabolso.events.client.adapter

import br.com.guiabolso.events.client.exception.FailedDependencyException
import java.nio.charset.Charset
import java.util.concurrent.TimeoutException

interface HttpClientAdapter {

    @Throws(TimeoutException::class, FailedDependencyException::class)
    fun post(url: String, headers: Map<String, String>, payload: String, charset: Charset, timeout: Int? = null): String

}