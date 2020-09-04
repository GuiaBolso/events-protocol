package br.com.guiabolso.tracing.wrapper

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.utils.ExceptionUtils.doNotFail
import java.util.concurrent.Callable

class CallableWrapper<T>(
    managers: List<ThreadContextManager<*>>,
    private val callable: Callable<T>
) : Callable<T> {

    private val contexts = managers.map { it to it.extract()!! }.toMap()

    override fun call(): T {
        val states = contexts.mapNotNull { doNotFail { it.key.withUnsafeContext(it.value) } }
        try {
            return callable.call()
        } finally {
            states.forEach { doNotFail { it.close() } }
        }
    }
}
