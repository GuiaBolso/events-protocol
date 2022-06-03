package br.com.guiabolso.events.json.kserialization

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonDataException
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.kserialization.serializers.ArrayNodeSerializer
import br.com.guiabolso.events.json.kserialization.serializers.EventMessageSerializer
import br.com.guiabolso.events.json.kserialization.serializers.EventSerializer
import br.com.guiabolso.events.json.kserialization.serializers.JsonLiteralSerializer
import br.com.guiabolso.events.json.kserialization.serializers.JsonNodeSerializer
import br.com.guiabolso.events.json.kserialization.serializers.JsonNullSerializer
import br.com.guiabolso.events.json.kserialization.serializers.PrimitiveNodeSerializer
import br.com.guiabolso.events.json.kserialization.serializers.RawEventSerializer
import br.com.guiabolso.events.json.kserialization.serializers.RedirectPayloadSerializer
import br.com.guiabolso.events.json.kserialization.serializers.RequestEventCreator
import br.com.guiabolso.events.json.kserialization.serializers.ResponseEventCreator
import br.com.guiabolso.events.json.kserialization.serializers.TreeNodeSerializer
import br.com.guiabolso.events.json.kserialization.serializers.UserSerializer
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import kotlinx.serialization.serializerOrNull
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

@OptIn(ExperimentalSerializationApi::class)
class KotlinSerializationJsonAdapter(builder: JsonBuilder.() -> Unit = {}) : JsonAdapter {
    private val jsonAdapter = Json {
        this.builder()
        this.serializersModule = this.serializersModule + SerializersModule {
            contextual(JsonNodeSerializer)
            contextual(ArrayNodeSerializer)
            contextual(TreeNodeSerializer)
            contextual(PrimitiveNodeSerializer)
            contextual(JsonNullSerializer)
            contextual(JsonLiteralSerializer)
            contextual(RawEventSerializer)
            contextual(UserSerializer)
            contextual(EventMessageSerializer)
            contextual(RedirectPayloadSerializer)

            val requestEventSerializer = EventSerializer("RequestEvent", RequestEventCreator)
            val responseEventSerializer = EventSerializer("ResponseEvent", ResponseEventCreator)
            contextual(requestEventSerializer)
            contextual(responseEventSerializer)
            polymorphic(Event::class) {
                subclass(RequestEvent::class, requestEventSerializer)
                subclass(ResponseEvent::class, responseEventSerializer)
            }
        }
    }

    override fun <T> toJson(any: T?): String {
        return when (any) {
            null -> JsonNull.toString()
            else -> jsonAdapter.execute { encodeToString(kSerializer(any.javaClass), any) }
        }
    }

    override fun <T> toJson(any: T?, type: Type): String {
        return jsonAdapter.execute { encodeToString(kSerializer(type), any) }
    }

    override fun <T> toJsonTree(any: T?): JsonNode {
        return when (any) {
            is JsonNode -> any
            null -> JsonNull
            else -> jsonAdapter.execute {
                val outputStream = ByteArrayOutputStream().apply {
                    encodeToStream(kSerializer(any.javaClass), any, this)
                }
                decodeFromStream(outputStream.toByteArray().inputStream())
            }
        }
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return jsonAdapter.execute { decodeFromString(kSerializer(clazz), json) }
    }

    override fun <T> fromJson(json: String, type: Type): T {
        return jsonAdapter.execute { decodeFromString(kSerializer(type), json) }
    }

    override fun <T> fromJson(jsonNode: JsonNode, type: Type): T {
        return jsonAdapter.execute {
            decodeFromJsonElement(kSerializer(type), encodeToJsonElement(jsonNode))
        }
    }

    override fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T {
        return jsonAdapter.execute {
            decodeFromJsonElement(kSerializer(clazz), encodeToJsonElement(jsonNode))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> kSerializer(clazz: Class<T>): KSerializer<T> {
        return (jsonAdapter.serializersModule.serializerOrNull(clazz) ?: serializer(clazz)) as KSerializer<T>
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> kSerializer(type: Type): KSerializer<T> {
        return (jsonAdapter.serializersModule.serializerOrNull(type) ?: serializer(type)) as KSerializer<T>
    }

    private fun <T> Json.execute(block: Json.() -> T): T {
        return try {
            this.block()
        } catch (e: SerializationException) {
            throw JsonDataException(message = e.message, cause = e)
        }
    }
}

fun Decoder.asJsonDecoder() = this as JsonDecoder
