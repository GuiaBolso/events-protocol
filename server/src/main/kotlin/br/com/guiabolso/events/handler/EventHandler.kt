package br.com.guiabolso.events.handler

import br.com.guiabolso.events.model.Event

interface EventHandler {

    fun handle(event: Event): Event

}