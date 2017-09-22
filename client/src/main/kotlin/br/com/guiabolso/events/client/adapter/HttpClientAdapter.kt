package br.com.guiabolso.events.client.adapter

import java.nio.charset.Charset
import java.util.concurrent.TimeoutException

interface HttpClientAdapter {

    @Throws(TimeoutException::class)
    fun post(url: String, headers: Map<String, String>, payload: String, charset: Charset, timeout: Long? = null): String


}