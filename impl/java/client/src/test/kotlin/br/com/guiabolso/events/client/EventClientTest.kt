package br.com.guiabolso.events.client

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.BadProtocolException
import br.com.guiabolso.events.client.exception.FailedDependencyException
import br.com.guiabolso.events.client.exception.TimeoutException
import br.com.guiabolso.events.client.model.Response
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.EventErrorType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventClientTest {

    @Test
    fun testSuccessResponse() {
        val httpClient = mockk<HttpClientAdapter>()
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilderForTest.buildResponseEvent()

        every {
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        } returns mapper.toJson(responseEvent)

        val response = eventClient.sendEvent("url", event, timeout = 1000)

        assertTrue(response is Response.Success)
        assertEquals(responseEvent, (response as Response.Success).event)
    }

    @Test
    fun testSuccessResponseWithCustomHeader() {
        val httpClient = mockk<HttpClientAdapter>()
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilderForTest.buildResponseEvent()

        every {
            httpClient.post(
                eq("url"),
                any(),
                eq(mapper.toJson(event)),
                eq(Charsets.UTF_8),
                eq(1000)
            )
        } returns mapper.toJson(responseEvent)

        val response = eventClient.sendEvent("url", event, mapOf("Test" to "some value"), 1000)

        assertTrue(response is Response.Success)
        assertEquals(responseEvent, (response as Response.Success).event)

        verify {
            httpClient.post(
                eq("url"),
                eq(mapOf("Content-Type" to "application/json", "Test" to "some value")),
                eq(mapper.toJson(event)),
                eq(Charsets.UTF_8),
                eq(1000)
            )
        }
    }

    @Test
    fun testRedirectResponse() {
        val httpClient = mockk<HttpClientAdapter>()
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilderForTest.buildRedirectEvent()

        every {
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        } returns mapper.toJson(responseEvent)

        val response = eventClient.sendEvent("url", event, timeout = 1000)

        assertTrue(response is Response.Redirect)
        assertEquals(responseEvent, (response as Response.Redirect).event)
    }

    @Test
    fun testErrorResponse() {
        val httpClient = mockk<HttpClientAdapter>()
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilderForTest.buildResponseEvent().copy(name = "${event.name}:error")

        every {
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        } returns mapper.toJson(responseEvent)

        val response = eventClient.sendEvent("url", event, timeout = 1000)

        assertTrue(response is Response.Error)
        assertEquals(EventErrorType.Generic, (response as Response.Error).errorType)
        assertEquals(responseEvent, response.event)
    }

    @Test
    fun testTimeout() {
        val httpClient = mockk<HttpClientAdapter>()
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()

        every {
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        } throws TimeoutException("timeout", cause = null)

        val response = eventClient.sendEvent("url", event, timeout = 1000)

        assertTrue(response is Response.Timeout)
        assertTrue((response as Response.Timeout).exception is TimeoutException)
    }

    @Test
    fun testInvalidResponse() {
        val httpClient = mockk<HttpClientAdapter>()
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()

        every {
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        } returns "something"

        val response = eventClient.sendEvent("url", event, timeout = 1000)

        assertTrue(response is Response.FailedDependency)
        assertEquals("something", (response as Response.FailedDependency).response)
        assertTrue(response.exception is BadProtocolException)
    }

    @Test
    fun testCannotConnect() {
        val httpClient = mockk<HttpClientAdapter>()
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()

        every {
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        } throws FailedDependencyException("Failed dependency", null)

        val response = eventClient.sendEvent("url", event, timeout = 1000)

        assertTrue(response is Response.FailedDependency)
        assertNull((response as Response.FailedDependency).response)
        assertTrue(response.exception is FailedDependencyException)
    }
}
