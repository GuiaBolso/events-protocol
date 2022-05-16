import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.gson.GsonJsonAdapter
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeProjectListener

object ProjectConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> {
        return listOf(MapperHolderInitializer)
    }
}

object MapperHolderInitializer : BeforeProjectListener {
    override suspend fun beforeProject() {
        MapperHolder.mapper = listOf(
            MoshiJsonAdapter(),
            GsonJsonAdapter { serializeNulls() }
        ).random()
    }
}
