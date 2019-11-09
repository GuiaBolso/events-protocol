package br.com.guiabolso.events.doc.description.models

import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility

class KPropertyWrapper<out R>(
    override val name: String,
    override val returnType: KType
) : KProperty<R> {

    override val isAbstract = false
    override val isConst = false
    override val isFinal = true
    override val isOpen = false
    override val isLateinit = false
    override val isSuspend = false
    override val visibility = KVisibility.PUBLIC

    override val annotations = emptyList<Annotation>()
    override val parameters = emptyList<KParameter>()
    override val typeParameters = emptyList<KTypeParameter>()

    override val getter: KProperty.Getter<R>
        get() = throw NotImplementedError()

    override fun call(vararg args: Any?): R = throw NotImplementedError()
    override fun callBy(args: Map<KParameter, Any?>): R = throw NotImplementedError()

}