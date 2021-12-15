import br.com.guiabolso.events.json.moshi.factory.EventProtocolJsonAdapterFactory
import br.com.guiabolso.events.json.moshi.factory.JsonNodeFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val moshi: Moshi =
    Moshi.Builder()
        .add(JsonNodeFactory)
        .add(EventProtocolJsonAdapterFactory)
        .addLast(KotlinJsonAdapterFactory())
        .build()
