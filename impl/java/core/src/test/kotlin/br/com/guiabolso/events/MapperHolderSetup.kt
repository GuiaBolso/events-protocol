package br.com.guiabolso.events

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.gson.GsonParser
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class MapperHolderSetup : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext?) {
        MapperHolder.mapper = GsonParser()
    }
}
