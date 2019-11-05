package br.com.guiabolso.events.client

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.BadProtocolException
import br.com.guiabolso.events.client.exception.FailedDependencyException
import br.com.guiabolso.events.client.exception.TimeoutException
import br.com.guiabolso.events.client.model.Response
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventSunset
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.time.LocalDateTime

class EventClientTest {

    @Test
    fun testSuccessResponse() {
        val httpClient = mock(HttpClientAdapter::class.java)
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilderForTest.buildResponseEvent()

        whenever(
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                MapperHolder.mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        ).thenReturn(MapperHolder.mapper.toJson(responseEvent))

        val response = eventClient.sendEvent("url", event, 1000)

        assertTrue(response is Response.Success)
        assertEquals(responseEvent, (response as Response.Success).event)
        assertNull(response.event.sunset)
    }

    @Test
    fun testRedirectResponse() {
        val httpClient = mock(HttpClientAdapter::class.java)
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilderForTest.buildRedirectEvent()

        whenever(
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                MapperHolder.mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        ).thenReturn(MapperHolder.mapper.toJson(responseEvent))

        val response = eventClient.sendEvent("url", event, 1000)

        assertTrue(response is Response.Success)
        response as Response.Success
        assertEquals(responseEvent, response.event)
    }

    @Test
    fun testErrorResponse() {
        val httpClient = mock(HttpClientAdapter::class.java)
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilderForTest.buildResponseEvent().copy(name = "${event.name}:error")

        whenever(
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                MapperHolder.mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        ).thenReturn(MapperHolder.mapper.toJson(responseEvent))

        val response = eventClient.sendEvent("url", event, 1000)

        assertTrue(response is Response.Error)
        assertEquals(EventErrorType.Generic, (response as Response.Error).errorType)
        assertEquals(responseEvent, response.event)
    }

    @Test
    fun testTimeout() {
        val httpClient = mock(HttpClientAdapter::class.java)
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()

        whenever(
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                MapperHolder.mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        ).thenThrow(TimeoutException::class.java)

        val response = eventClient.sendEvent("url", event, 1000)

        assertTrue(response is Response.Timeout)
        assertTrue((response as Response.Timeout).exception is TimeoutException)
    }

    @Test
    fun testInvalidResponse() {
        val httpClient = mock(HttpClientAdapter::class.java)
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()

        whenever(
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                MapperHolder.mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        ).thenReturn("something")

        val response = eventClient.sendEvent("url", event, 1000)

        assertTrue(response is Response.FailedDependency)
        assertEquals("something", (response as Response.FailedDependency).response)
        assertTrue(response.exception is BadProtocolException)
    }

    @Test
    fun testCannotConnect() {
        val httpClient = mock(HttpClientAdapter::class.java)
        val eventClient = EventClient(httpClient)

        val event = EventBuilderForTest.buildRequestEvent()

        whenever(
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                MapperHolder.mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        ).thenThrow(FailedDependencyException::class.java)

        val response = eventClient.sendEvent("url", event, 1000)

        assertTrue(response is Response.FailedDependency)
        assertNull((response as Response.FailedDependency).response)
        assertTrue(response.exception is FailedDependencyException)
    }

    @Test
    fun testSuccessResponseWithSunset() {
        val httpClient = mock(HttpClientAdapter::class.java)
        val eventClient = EventClient(httpClient)
        val now = LocalDateTime.now().minusHours(1)

        val event = EventBuilderForTest.buildRequestEvent()
        val responseEvent = EventBuilder.responseFor(event) {
            payload = 42
            sunset = EventSunset(now, "Deprecated")
        }

        whenever(
            httpClient.post(
                "url",
                mapOf("Content-Type" to "application/json"),
                MapperHolder.mapper.toJson(event),
                Charsets.UTF_8,
                1000
            )
        ).thenReturn(MapperHolder.mapper.toJson(responseEvent))

        val response = eventClient.sendEvent("url", event, 1000)

        assertTrue(response is Response.Success)
        assertEquals(responseEvent, (response as Response.Success).event)
        assertEquals(EventSunset(now, "Deprecated"), response.event.sunset)
        //TODO: test logger warn and error
    }

}