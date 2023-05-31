package br.com.guiabolso.events

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.gson.GsonJsonAdapter
import br.com.guiabolso.events.json.jackson.Jackson2JsonAdapter
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.slf4j.LoggerFactory

class MapperHolderSetup : BeforeAllCallback, TestExecutionListener {

    private val logger = LoggerFactory.getLogger(MapperHolderSetup::class.java)

    override fun beforeAll(context: ExtensionContext) {

        MapperHolder.mapper = listOf(
            MoshiJsonAdapter(),
            GsonJsonAdapter(),
            Jackson2JsonAdapter { addModule(kotlinModule()) },
        ).random()
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        logger.info("\nNOTA: Teste executado com Mapper: ${MapperHolder.mapper.javaClass.simpleName}")
    }
}
