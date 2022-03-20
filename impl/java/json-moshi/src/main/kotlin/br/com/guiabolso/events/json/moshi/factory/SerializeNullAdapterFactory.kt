package br.com.guiabolso.events.json.moshi.factory

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

object SerializeNullAdapterFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<Any>? {
        return moshi.nextAdapter<Any>(this, type, annotations).serializeNulls()
    }
}
