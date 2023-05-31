package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule

internal object GuiabolsoJsonNodeModule : SimpleModule() {
    init {
        addDeserializer(JsonNode::class.java, JsonNodeDeserializer)
        addSerializer(JsonLiteral::class.java, PrimitiveNodeSerializer)
    }
}
