package br.com.guiabolso.events.json.moshi

import com.squareup.moshi.Moshi
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

inline fun <reified T> Moshi.nullSafeAdapterFor(): com.squareup.moshi.JsonAdapter<T> {
    return this.adapter<T>(typeOf<T>().javaType).nullSafe()
}

inline fun <reified T> Moshi.noNullAdapterFor(): com.squareup.moshi.JsonAdapter<T> {
    return this.adapter<T>(typeOf<T>().javaType).nonNull()
}
