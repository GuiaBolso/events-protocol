package br.com.guiabolso.events.builder;

import br.com.guiabolso.events.model.Event;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventBuilderJavaUsabilityTest {

    @Test
    public void testUsability() {
        EventBuilder builder = new EventBuilder();
        builder.setName("test:event");
        builder.setId("id");
        builder.setFlowId("flowId");
        builder.setVersion(1);
        builder.setPayload(42);
        Event event = builder.build();

        EventBuilder responseBuilder = EventBuilder.javaResponseFor(event);
        responseBuilder.setPayload(84);
        Event response = responseBuilder.build();

        assertEquals("test:event", event.getName());
        assertEquals(1, event.getVersion());
        assertEquals(42, event.getPayload().getAsNumber());

        assertEquals("test:event:response", response.getName());
        assertEquals(1, response.getVersion());
        assertEquals(84, response.getPayload().getAsNumber());

        assertEquals(event.getId(), response.getId());
        assertEquals(event.getFlowId(), response.getFlowId());
    }

    @Test
    public void testNotFoundUsability() {
        EventBuilder builder = new EventBuilder();
        builder.setName("test:event");
        builder.setId("id");
        builder.setFlowId("flowId");
        builder.setVersion(1);
        builder.setPayload(42);
        Event event = builder.build();

        Event response = EventBuilder.notFoundFor(event);

        assertEquals("test:event:notFound", response.getName());
        assertEquals(1, response.getVersion());

        JsonObject message = response.getPayload().getAsJsonObject();
        assertEquals("NO_EVENT_HANDLER_FOUND", message.get("code").getAsString());
        assertEquals("test:event", message.get("parameters").getAsJsonObject().get("event").getAsString());
        assertEquals(1, message.get("parameters").getAsJsonObject().get("version").getAsNumber());
    }

}
