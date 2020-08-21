package br.com.guiabolso.events.json

import kotlinx.serialization.json.Json

object MapperHolder {

    @JvmField
    var mapper = Json {
        ignoreUnknownKeys = true
    }
}
