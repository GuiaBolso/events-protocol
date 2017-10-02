package br.com.guiabolso.events.utils;

import br.com.guiabolso.events.builder.EventBuilder;
import br.com.guiabolso.events.model.EventErrorType.Generic;
import br.com.guiabolso.events.model.EventErrorType.NotFound;
import br.com.guiabolso.events.model.ResponseEvent;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventsJavaUsabilityTest {

    @Test
    public void testUsability() {
        ResponseEvent event = createEvent("test:event:response");

        assertTrue(Events.isSuccess(event));
        assertFalse(Events.isError(event));
    }

    @Test
    public void testUsabilityWithError() {
        ResponseEvent event = createEvent("test:event:error");

        assertFalse(Events.isSuccess(event));
        assertTrue(Events.isError(event));
        assertEquals("error", Events.getErrorType(event).getTypeName());
        assertEquals(Generic.class, Events.getErrorType(event).getClass());
        assertEquals(Generic.INSTANCE, Events.getErrorType(event));
    }

    @Test
    public void testUsabilityWithNotFound() {
        ResponseEvent event = createEvent("test:event:notFound");

        assertFalse(Events.isSuccess(event));
        assertTrue(Events.isError(event));
        assertEquals("notFound", Events.getErrorType(event).getTypeName());
        assertEquals(NotFound.class, Events.getErrorType(event).getClass());
        assertEquals(NotFound.INSTANCE, Events.getErrorType(event));
    }

    private ResponseEvent createEvent(String name) {
        EventBuilder builder = new EventBuilder();
        builder.setId("id");
        builder.setFlowId("flowId");
        builder.setName(name);
        builder.setVersion(1);
        builder.setPayload(42);
        return builder.buildResponseEvent();
    }

}