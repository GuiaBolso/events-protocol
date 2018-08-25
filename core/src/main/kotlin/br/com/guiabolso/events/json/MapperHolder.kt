package br.com.guiabolso.events.json

import com.google.gson.GsonBuilder

object MapperHolder {

    @JvmField
    var mapper = GsonBuilder()
            .serializeNulls()
            .create()!!

}