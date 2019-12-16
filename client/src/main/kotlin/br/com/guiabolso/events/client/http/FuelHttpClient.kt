package br.com.guiabolso.events.client.http

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.FailedDependencyException
import br.com.guiabolso.events.client.exception.TimeoutException
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import java.net.SocketTimeoutException
import java.nio.charset.Charset

class FuelHttpClient : HttpClientAdapter {

    override fun post(
        url: String,
        headers: Map<String, String>,
        payload: String,
        charset: Charset,
        timeout: Int
    ): String {
        val request = FuelManager.instance.request(Method.POST, url)

        headers.forEach { (k, v) -> request.header(k to v) }

        request.body(payload, charset)
        request.timeout(timeout)
        request.timeoutRead(timeout)

        val (_, _, result) = request.responseString()

        when (result) {
            is Result.Success -> {
                return result.getAs<String>()!!
            }
            is Result.Failure -> {
                val error: FuelError? = result.getAs()
                if (error?.exception is SocketTimeoutException) {
                    throw TimeoutException("Timeout calling $url. Error: $error", error.exception)
                } else {
                    throw FailedDependencyException("Failed dependency calling $url. Error: $error", error?.exception)
                }
            }
        }
    }

}