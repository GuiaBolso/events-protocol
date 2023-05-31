package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonDataException
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.model.Event
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.exc.StreamReadException
import com.fasterxml.jackson.databind.DatabindException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import java.lang.reflect.Type

class Jackson2JsonAdapter(configuration: JsonMapper.Builder.() -> Unit) : JsonAdapter {

    private var mapper: JsonMapper

    init {
        mapper = JsonMapper.builder()
            .apply(configuration)
            .addModule(GuiabolsoJsonNodeModule)
            .addMixIn(Event::class.java, IgnoreEventGetters::class.java)
            .build()
    }

    internal constructor(mapper: JsonMapper) : this({}) {
        this.mapper = mapper
    }

    override fun <T> toJson(any: T?): String {
        return mapper.writeValueAsString(any)
    }

    override fun <T> toJson(any: T?, type: Type): String {
        return toJson(any)
    }

    override fun <T> toJsonTree(any: T?): JsonNode {
        return when (any) {
            null -> JsonNull
            is JsonNode -> any
            else -> mapper.wrapException { treeToValue(valueToTree(any), JsonNode::class.java) }
        }
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return mapper.wrapException {
            readValue(json, clazz)
        }
    }

    override fun <T> fromJson(json: String, type: Type): T {
        return mapper.wrapException {
            readValue(
                json,
                TypeFactory.defaultInstance().constructType(type),
            )
        }
    }

    override fun <T> fromJson(jsonNode: JsonNode, type: Type): T {
        return mapper.wrapException {
            treeToValue(
                valueToTree(jsonNode),
                TypeFactory.defaultInstance().constructType(type),
            )
        }
    }

    override fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T {
        return mapper.wrapException {
            treeToValue(valueToTree(jsonNode), clazz)
        }
    }

    private fun <T> JsonMapper.wrapException(block: JsonMapper.() -> T): T {
        val result = try {
            this.block()
        } catch (e: Exception) {
            throw when (e) {
                is StreamReadException,
                is DatabindException,
                is JsonProcessingException -> JsonDataException(e.message, e)

                else -> e
            }
        }

        return result ?: throw JsonDataException(message = "Unexpected null as a result from deserialization")
    }
}
