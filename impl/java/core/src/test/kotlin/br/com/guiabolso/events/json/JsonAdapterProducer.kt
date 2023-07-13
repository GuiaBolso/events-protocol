package br.com.guiabolso.events.json

import br.com.guiabolso.events.json.gson.GsonJsonAdapter
import br.com.guiabolso.events.json.jackson.Jackson2JsonAdapter
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import com.fasterxml.jackson.module.kotlin.kotlinModule

object JsonAdapterProducer {
    private val adapters = listOf(
        MoshiJsonAdapter(),
        GsonJsonAdapter(),
        Jackson2JsonAdapter { addModule(kotlinModule()) },
    )

    val mapper get() = adapters.random()
}
