package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.longOrNull
import br.com.guiabolso.events.json.stringOrNull
import br.com.guiabolso.events.json.treeNodeOrNull
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

    fun <T> payloadAs(clazz: Class<T>, jsonAdapter: JsonAdapter): T =
        this.payload.convertTo(clazz, jsonAdapter)

    inline fun <reified T> payloadAs(jsonAdapter: JsonAdapter): T =
        this.payload.convertTo(jsonAdapter)

    fun <T> identityAs(clazz: Class<T>, jsonAdapter: JsonAdapter): T =
        this.identity.convertTo(clazz, jsonAdapter)

    inline fun <reified T> identityAs(jsonAdapter: JsonAdapter): T =
        this.identity.convertTo(jsonAdapter)

    fun <T> authAs(clazz: Class<T>, jsonAdapter: JsonAdapter): T =
        this.auth.convertTo(clazz, jsonAdapter)

    inline fun <reified T> authAs(jsonAdapter: JsonAdapter): T = this.auth.convertTo(jsonAdapter)

    val user: User?
        get() = this.identity.withCheckedJsonNull("user") { node ->
            node["user"]?.treeNodeOrNull?.run {
                User(
                    id = this["id"]?.longOrNull,
                    type = this["type"]?.stringOrNull
                )
            }
        }

    val userId: Long?
        get() = with(this.identity) {
            this["userId"]?.longOrNull ?: user?.id
        }

    val userIdAsString: String?
        get() = with(this.identity) {
            this["userId"]?.stringOrNull ?: this["user"]?.treeNodeOrNull?.get("id")?.stringOrNull
        }

    val origin: String?
        get() = this.metadata.withCheckedJsonNull("origin") { node ->
            node["origin"]?.stringOrNull
        }

    inline fun <reified T> JsonNode.convertTo(jsonAdapter: JsonAdapter): T =
        jsonAdapter.fromJson(this, typeOf<T>().javaType)

    private fun <T> JsonNode.convertTo(clazz: Class<T>, jsonAdapter: JsonAdapter): T = jsonAdapter.fromJson(this, clazz)
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

data class User(val id: Long?, val type: String?)
