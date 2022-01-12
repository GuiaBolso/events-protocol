package br.com.guiabolso.events.utils;

import br.com.guiabolso.events.builder.EventBuilder;
import br.com.guiabolso.events.model.EventErrorType.Generic;
import br.com.guiabolso.events.model.EventErrorType.NotFound;
import br.com.guiabolso.events.model.RequestEvent;
import br.com.guiabolso.events.model.ResponseEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventsJavaUsabilityTest {

    @Test
    public void testUsability() {
        ResponseEvent event = createResponseEvent("test:event:response");

        assertTrue(event.isSuccess());
        assertFalse(event.isError());
    }

    @Test
    public void testUsabilityWithError() {
        ResponseEvent event = createResponseEvent("test:event:error");

        assertFalse(event.isSuccess());
        assertTrue(event.isError());
        assertEquals("error", event.getErrorType().getTypeName());
        assertEquals(Generic.class, event.getErrorType().getClass());
        assertEquals(Generic.INSTANCE, event.getErrorType());
    }

    @Test
    public void testUsabilityWithNotFound() {
        ResponseEvent event = createResponseEvent("test:event:notFound");

        assertFalse(event.isSuccess());
        assertTrue(event.isError());
        assertEquals("notFound", event.getErrorType().getTypeName());
        assertEquals(NotFound.class, event.getErrorType().getClass());
        assertEquals(NotFound.INSTANCE, event.getErrorType());
    }

    @Test
    public void testUsabilityWithPayloadAs() {
        RequestEvent event = createRequestEvent("test:event");
        assertEquals(42L, event.payloadAs(Long.class).longValue());
    }

    private ResponseEvent createResponseEvent(String name) {
        EventBuilder builder = new EventBuilder();
        builder.setId("id");
        builder.setFlowId("flowId");
        builder.setName(name);
        builder.setVersion(1);
        builder.setPayload(42);
        return builder.buildResponseEvent();
    }

    private RequestEvent createRequestEvent(String name) {
        EventBuilder builder = new EventBuilder();
        builder.setId("id");
        builder.setFlowId("flowId");
        builder.setName(name);
        builder.setVersion(1);
        builder.setPayload(42);
        return builder.buildRequestEvent();
    }

}
