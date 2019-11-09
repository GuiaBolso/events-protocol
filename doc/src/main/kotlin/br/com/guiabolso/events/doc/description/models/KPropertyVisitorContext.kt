package br.com.guiabolso.events.doc.description.models

data class KPropertyVisitorContext(
    val objectStack: List<String> = emptyList()
) {

    fun add(obj: String) = this.copy(objectStack = objectStack + obj)

    fun insideOf(objIdentity:String) = objectStack.contains(objIdentity)

}