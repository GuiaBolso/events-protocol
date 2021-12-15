package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.NumberNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.StringNode
import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.getValue
import br.com.guiabolso.events.json.withCheckedJsonNull
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

sealed class Event {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: JsonNode
    abstract val identity: TreeNode
    abstract val auth: TreeNode
    abstract val metadata: TreeNode

    fun <T> payloadAs(clazz: Class<T>): T = this.payload.convertTo(clazz)

    inline fun <reified T> payloadAs(): T = this.payload.convertTo()

    fun <T> identityAs(clazz: Class<T>): T = this.identity.convertTo(clazz)

    inline fun <reified T> identityAs(): T = this.identity.convertTo()

    fun <T> authAs(clazz: Class<T>): T = this.auth.convertTo(clazz)

    inline fun <reified T> authAs(): T = this.auth.convertTo()

    val userId: Long?
        get() = this.identity.withCheckedJsonNull("userId") { node ->
            (node.getValue("userId") as? NumberNode)?.value?.toLong()
        }

    val userIdAsString: String?
        get() = this.identity.withCheckedJsonNull("userId") { node ->
            (node.getValue("userId") as? PrimitiveNode<*>)?.value?.toString()
        }

    val origin: String?
        get() = this.metadata.withCheckedJsonNull("origin") { node ->
            (node.getValue("origin") as? StringNode)?.value
        }

    inline fun <reified T> JsonNode.convertTo(): T = mapper.fromJson(this, typeOf<T>().javaType)

    private fun <T> JsonNode.convertTo(clazz: Class<T>): T = mapper.fromJson(this, clazz)
}

data class ResponseEvent(
    override val name: String,
    override val version: Int,
    override val id: String,
    override val flowId: String,
    override val payload: JsonNode,
    override val identity: TreeNode,
    override val auth: TreeNode,
    override val metadata: TreeNode
) : Event() {

    fun isSuccess() = this.name.endsWith(":response")

    fun isRedirect() = this.name.endsWith(":redirect")

    fun isError() = !this.isSuccess() && !this.isRedirect()

    fun getErrorType(): EventErrorType {
        if (isSuccess()) throw IllegalStateException("This is not an error event.")
        return EventErrorType.getErrorType(this.name.substringAfterLast(":"))
    }
}

data class RequestEvent(

    override val name: String,

    override val version: Int,

    override val id: String,

    override val flowId: String,

    override val payload: JsonNode,

    override val identity: TreeNode,

    override val auth: TreeNode,

    override val metadata: TreeNode
) : Event()
