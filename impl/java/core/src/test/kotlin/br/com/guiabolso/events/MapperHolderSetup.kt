package br.com.guiabolso.events

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.kserialization.KotlinSerializationJsonAdapter
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.LoggerFactory

class MapperHolderSetup : BeforeAllCallback {
    private val logger = LoggerFactory.getLogger(MapperHolder::class.java)

    override fun beforeAll(context: ExtensionContext?) {
        MapperHolder.mapper = listOf(KotlinSerializationJsonAdapter(), MoshiJsonAdapter()).random().also {
           logger.info("### Using ${it.javaClass.simpleName} implementation. ###")
        }
    }
}
