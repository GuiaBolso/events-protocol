package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.asPrimitiveNumberNode
import br.com.guiabolso.events.json.asPrimitiveStringNode
import br.com.guiabolso.events.json.getValue
import br.com.guiabolso.events.json.withCheckedJsonNull

sealed class Event {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: JsonNode
    abstract val identity: TreeNode
    abstract val auth: TreeNode
    abstract val metadata: TreeNode

    fun <T> payloadAs(clazz: Class<T>): T = this.payload.toInstanceOf(clazz)

    fun <T> identityAs(clazz: Class<T>): T = this.identity.toInstanceOf(clazz)

    fun <T> authAs(clazz: Class<T>): T = this.auth.toInstanceOf(clazz)

    private fun <T> JsonNode.toInstanceOf(clazz: Class<T>): T {
        return MapperHolder.mapper.fromJson(this, clazz)
    }

    val userId: Long?
        get() = this.identity.withCheckedJsonNull("userId") { node ->
            node.getValue("userId").asPrimitiveNumberNode().value.toLong()
        }

    val userIdAsString: String?
        get() = this.identity.withCheckedJsonNull("userId") { node ->
            (node.getValue("userId") as JsonNode.PrimitiveNode<*>).value.toString()
        }

    val origin: String?
        get() = this.metadata.withCheckedJsonNull("origin") { node ->
            node.getValue("origin").asPrimitiveStringNode().value
        }
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
