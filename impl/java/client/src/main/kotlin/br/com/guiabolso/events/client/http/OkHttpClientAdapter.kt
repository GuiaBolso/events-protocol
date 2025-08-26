package br.com.guiabolso.events.client.http

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.FailedDependencyException
import br.com.guiabolso.events.client.exception.TimeoutException
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class OkHttpClientAdapter(private val template: OkHttpClient = OkHttpClient.Builder().build()) : HttpClientAdapter {
    private val clients = ConcurrentHashMap<Long, OkHttpClient>()

    override fun post(
        url: String,
        headers: Map<String, String>,
        payload: String,
        charset: Charset,
        timeout: Int
    ): String {

        val client = getClientFor(timeout)
        val request = okhttp3.Request.Builder()
            .url(url)
            .post(payload.toRequestBody())
            .apply {
                headers.forEach { (key, value) ->
                    this.addHeader(key, value)
                }
            }
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected response $response")
                }
                response.body!!.string()
            }
        } catch (ex: Exception) {
            handleException(ex, url)
        }
    }

    private fun getClientFor(requestTimeout: Int): OkHttpClient =
        clients.computeIfAbsent(requestTimeout.toLong()) { timeout ->
            template.newBuilder()
                .callTimeout(timeout, TimeUnit.MILLISECONDS)
                .build()
        }

    private fun handleException(ex: Exception, url: String): Nothing {
        when (ex) {
            is SocketTimeoutException,
            is InterruptedIOException -> throw TimeoutException(
                "Timeout calling $url",
                ex
            )

            else -> throw FailedDependencyException(
                "Failed dependency calling $url", ex
            )
        }
    }
}
