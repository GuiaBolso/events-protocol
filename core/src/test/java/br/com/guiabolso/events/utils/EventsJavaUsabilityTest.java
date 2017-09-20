package br.com.guiabolso.events.utils;

import br.com.guiabolso.events.builder.EventBuilder;
import br.com.guiabolso.events.model.Event;
import br.com.guiabolso.events.model.EventErrorType.Generic;
import br.com.guiabolso.events.model.EventErrorType.NotFound;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventsJavaUsabilityTest {

    @Test
    public void testUsability() {
        Event event = createEvent("test:event:response");

        assertTrue(Events.isSuccess(event));
        assertFalse(Events.isError(event));
    }

    @Test
    public void testUsabilityWithError() {
        Event event = createEvent("test:event:error");

        assertFalse(Events.isSuccess(event));
        assertTrue(Events.isError(event));
        assertEquals("error", Events.getErrorType(event).getTypeName());
        assertEquals(Generic.class, Events.getErrorType(event).getClass());
    }

    @Test
    public void testUsabilityWithNotFound() {
        Event event = createEvent("test:event:notFound");

        assertFalse(Events.isSuccess(event));
        assertTrue(Events.isError(event));
        assertEquals("notFound", Events.getErrorType(event).getTypeName());
        assertEquals(NotFound.class, Events.getErrorType(event).getClass());
    }

    private Event createEvent(String name) {
        EventBuilder builder = new EventBuilder();
        builder.setName(name);
        builder.setVersion(1);
        builder.setPayload(42);
        return builder.build();
    }

}
