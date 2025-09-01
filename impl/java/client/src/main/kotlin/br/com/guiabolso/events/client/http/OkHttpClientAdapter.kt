package br.com.guiabolso.events.client.http

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.FailedDependencyException
import br.com.guiabolso.events.client.exception.TimeoutException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.closeQuietly
import okio.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun Call.executeAsync(): Response =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { this.cancel() }
        this.enqueue(
            object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response) { _ -> response.closeQuietly() }
                }
            }
        )
    }

class OkHttpClientAdapter(private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()) : HttpClientAdapter {
    private val clients = ConcurrentHashMap<Long, OkHttpClient>()

    override suspend fun suspendPost(
        url: String,
        headers: Map<String, String>,
        payload: String,
        charset: Charset,
        timeout: Int
    ): String {
        val client = getClientFor(timeout)
        val request = createRequest(url, payload, headers)

        return try {
            client.newCall(request)
                .executeAsync()
                .use { handleResponse(it) }
        } catch (ex: Exception) {
            handleException(ex, url)
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
                .use { response -> handleResponse(response) }
        } catch (ex: Exception) {
            handleException(ex, url)
        }
    }

    private fun handleResponse(response: Response): String {
        if (!response.isSuccessful) {
            throw IOException("Unexpected response $response")
        }
        return response.body!!.string()
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
