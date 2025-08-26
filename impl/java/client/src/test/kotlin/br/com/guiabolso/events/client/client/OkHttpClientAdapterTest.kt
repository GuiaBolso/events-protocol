package br.com.guiabolso.events.client.client

import br.com.guiabolso.events.client.exception.FailedDependencyException
import br.com.guiabolso.events.client.exception.TimeoutException
import br.com.guiabolso.events.client.http.OkHttpClientAdapter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OkHttpClientAdapterTest {
    private lateinit var server: MockWebServer
    private val client = OkHttpClientAdapter()

    @BeforeEach
    fun before() {
        server = MockWebServer()
        server.start()
    }

    @Test
    fun `post and request timed out`() {
        val client = OkHttpClientAdapter()

        assertThrows<TimeoutException> {
            client.post(
                url = "http://localhost:${server.port}/events",
                headers = emptyMap(),
                payload = "",
                charset = Charsets.UTF_8,
                timeout = 1000
            )
        }
    }

    @Test
    fun `post and fail to connect`() {
        server.close()
        assertThrows<FailedDependencyException> {
            client.post(
                url = "http://localhost:${server.port}/events",
                headers = emptyMap(),
                payload = "",
                charset = Charsets.UTF_8,
                timeout = 1000
            )
        }
    }

    @Test
    fun `post and get a successfully response back`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("Deu bom!")
        )

        val response = client.post(
            url = "http://localhost:${server.port}/events",
            headers = emptyMap(),
            payload = "empty",
            charset = Charsets.UTF_8,
            timeout = 5000
        )

        assert(response == "Deu bom!")
    }
}
