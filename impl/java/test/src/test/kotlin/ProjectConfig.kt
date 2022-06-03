import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.kserialization.KotlinSerializationJsonAdapter
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeProjectListener
import org.slf4j.LoggerFactory

object ProjectConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> {
        return listOf(MapperHolderInitializer)
    }
}

object MapperHolderInitializer : BeforeProjectListener {
    private val logger = LoggerFactory.getLogger(MapperHolder::class.java)

    override suspend fun beforeProject() {
        MapperHolder.mapper = listOf(MoshiJsonAdapter(), KotlinSerializationJsonAdapter()).random().also {
            logger.info("### Using ${it.javaClass.simpleName} implementation. ###")
        }
    }
}
