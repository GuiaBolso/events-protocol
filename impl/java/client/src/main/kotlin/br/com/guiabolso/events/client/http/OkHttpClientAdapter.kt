package br.com.guiabolso.events.client.http

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.FailedDependencyException
import br.com.guiabolso.events.client.exception.TimeoutException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class OkHttpClientAdapter(
    private val okHttpClient: OkHttpClient = okHttpClient(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HttpClientAdapter {
    private val clients = ConcurrentHashMap<Long, OkHttpClient>()

    override suspend fun suspendPost(
        url: String,
        headers: Map<String, String>,
        payload: String,
        charset: Charset,
        timeout: Int
    ): ByteArray {
        val client = getClientFor(timeout)
        val request = createRequest(url, payload, headers)

        return withContext(ioDispatcher) {
            try {
                client.newCall(request)
                    .execute()
                    .use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected response $response")
                        }
                        response.body!!.bytes()
                    }
            } catch (ex: Exception) {
                handleException(ex, url)
            }
        }
    }

    override fun post(
        url: String,
        headers: Map<String, String>,
        payload: String,
        charset: Charset,
        timeout: Int
    ): String {

        val client = getClientFor(timeout)
        val request = createRequest(url, payload, headers)

        return try {
            client
                .newCall(request)
                .execute()
                .use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected response $response")
                    }
                    response.body!!.string()
                }
        } catch (ex: Exception) {
            handleException(ex, url)
        }
    }

    private fun createRequest(url: String, payload: String, headers: Map<String, String>): Request =
        Request.Builder()
            .url(url)
            .post(payload.toRequestBody())
            .apply { headers.forEach { (key, value) -> this.addHeader(key, value) } }
            .build()

    private fun getClientFor(requestTimeout: Int): OkHttpClient =
        clients.computeIfAbsent(requestTimeout.toLong()) { timeout ->
            okHttpClient.newBuilder()
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

@Suppress("MagicNumber")
private fun okHttpClient(): OkHttpClient = OkHttpClient.Builder().dispatcher(
    Dispatcher().apply {
        maxRequestsPerHost = 32
    }
).build()
