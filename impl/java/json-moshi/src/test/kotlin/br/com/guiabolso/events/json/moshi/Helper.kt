package br.com.guiabolso.events.json.moshi

import br.com.guiabolso.events.json.moshi.factory.EventProtocolJsonAdapterFactory
import br.com.guiabolso.events.json.moshi.factory.JsonNodeFactory
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.buffer
import okio.sink
import okio.source
import java.io.ByteArrayOutputStream

val moshi: Moshi =
    Moshi.Builder()
        .add(JsonNodeFactory)
        .add(EventProtocolJsonAdapterFactory)
        .addLast(KotlinJsonAdapterFactory())
        .build()

fun String.jsonReader(): JsonReader = JsonReader.of(byteInputStream().source().buffer())

fun ByteArrayOutputStream.toJson() = toByteArray().toUtf8String()

fun ByteArrayOutputStream.jsonWriter(): JsonWriter = JsonWriter.of(sink().buffer())

fun ByteArray.toUtf8String() = this.toString(Charsets.UTF_8)

