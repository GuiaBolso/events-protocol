package br.com.guiabolso.events.doc.description.visitor

object KPropertyVisitorFactory {

    fun createVisitor(): CompositeKPropertyVisitor = CompositeKPropertyVisitor().apply {
        register(PrimitiveValueVisitor())
        register(ArrayValueVisitor(this))
        register(MapValueVisitor(this))
        register(ObjectValueVisitor(this))
    }

}