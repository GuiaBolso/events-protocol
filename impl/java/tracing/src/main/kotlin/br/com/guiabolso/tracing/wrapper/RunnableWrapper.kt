package br.com.guiabolso.tracing.wrapper

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.utils.ExceptionUtils.doNotFail

class RunnableWrapper(
    managers: List<ThreadContextManager<*>>,
    private val callable: Runnable
) : Runnable {

    private val contexts = managers.map { it to it.extract()!! }.toMap()

    override fun run() {
        val states = contexts.mapNotNull { doNotFail { it.key.withUnsafeContext(it.value) } }
        try {
            return callable.run()
        } finally {
            states.forEach { doNotFail { it.close() } }
        }
    }
}
