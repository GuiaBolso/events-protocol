import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.gson.GsonJsonAdapter
import br.com.guiabolso.events.json.jackson.Jackson2JsonAdapter
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.slf4j.LoggerFactory

object ProjectConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> {
        return listOf(MapperHolderInitializer)
    }
}

object MapperHolderInitializer : BeforeProjectListener, AfterEachListener {

    private val logger = LoggerFactory.getLogger(MapperHolderInitializer::class.java)

    override suspend fun beforeProject() {
        MapperHolder.mapper = listOf(
            MoshiJsonAdapter(),
            GsonJsonAdapter(),
            Jackson2JsonAdapter { addModule(kotlinModule()) },
        ).random()
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        logger.info("\nNOTA: Teste executado com Mapper: ${MapperHolder.mapper.javaClass.simpleName}")
    }
}
