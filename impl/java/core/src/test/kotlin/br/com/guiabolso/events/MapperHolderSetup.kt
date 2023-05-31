package br.com.guiabolso.events

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.gson.GsonJsonAdapter
import br.com.guiabolso.events.json.jackson.Jackson2JsonAdapter
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class MapperHolderSetup : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext?) {

        MapperHolder.mapper = listOf(
            MoshiJsonAdapter(),
            GsonJsonAdapter(),
            Jackson2JsonAdapter { addModule(kotlinModule()) },
        ).random()
    }
}
