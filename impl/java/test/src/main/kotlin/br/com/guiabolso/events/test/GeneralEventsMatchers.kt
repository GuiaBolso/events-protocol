package br.com.guiabolso.events.test

import br.com.guiabolso.events.model.Event
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.json.shouldNotContainJsonKeyValue
import io.kotest.assertions.json.shouldNotMatchJson
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun Event.shouldHaveName(name: String) = this should haveName(name)
infix fun Event.shouldNotHaveName(name: String) = this shouldNot haveName(name)

fun haveName(name: String) = object : Matcher<Event> {
    override fun test(value: Event) = MatcherResult(
        value.name == name,
        { "Event name should be $name but was ${value.name}" },
        { "Event name should not be $name, but was" }
    )
}

infix fun Event.shouldHaveVersion(version: Int) = this should haveVersion(version)
infix fun Event.shouldNotHaveVersion(version: Int) = this shouldNot haveVersion(version)
fun haveVersion(version: Int) = object : Matcher<Event> {
    override fun test(value: Event) = MatcherResult(
        value.version == version,
        { "Event version should be $version but was ${value.version}" },
        { "Event version should not be $version, but was" }
    )
}

infix fun Event.shouldHaveId(id: String) = this should haveId(id)
infix fun Event.shouldNotHaveId(id: String) = this shouldNot haveId(id)

fun haveId(id: String) = object : Matcher<Event> {
    override fun test(value: Event) = MatcherResult(
        value.id == id,
        { "Event id should be $id but was ${value.id}" },
        { "Event id should not be $id, but was" }
    )
}

infix fun Event.shouldHaveFlowId(flowId: String) = this should haveFlowId(flowId)
infix fun Event.shouldNotHaveFlowId(flowId: String) = this shouldNot haveFlowId(flowId)

fun haveFlowId(flowId: String) = object : Matcher<Event> {
    override fun test(value: Event) = MatcherResult(
        value.flowId == flowId,
        { "Event flowId should be $flowId but was ${value.flowId}" },
        { "Event flowId should not be $flowId, but was" }
    )
}

inline fun <reified T> Event.shouldContainPayload(key: String, value: T) =
    payload.toString().shouldContainJsonKeyValue(key, value)

inline fun <reified T> Event.shouldNotContainPayload(key: String, value: T) =
    payload.toString().shouldNotContainJsonKeyValue(key, value)

infix fun Event.shouldHavePayload(map: Map<String, Any?>) = payload.toString().shouldMatchJson(map.toJson())
infix fun Event.shouldNotHavePayload(map: Map<String, Any?>) = payload.toString().shouldNotMatchJson(map.toJson())

inline fun <reified T> Event.shouldContainIdentity(key: String, value: T) =
    identity.toString().shouldContainJsonKeyValue(key, value)

inline fun <reified T> Event.shouldNotContainIdentity(key: String, value: T) =
    identity.toString().shouldNotContainJsonKeyValue(key, value)

infix fun Event.shouldHaveIdentity(map: Map<String, Any?>) = identity.toString().shouldMatchJson(map.toJson())
infix fun Event.shouldNotHaveIdentity(map: Map<String, Any?>) = identity.toString().shouldNotMatchJson(map.toJson())

inline fun <reified T> Event.shouldContainAuth(key: String, value: T) =
    auth.toString().shouldContainJsonKeyValue(key, value)

inline fun <reified T> Event.shouldNotContainAuth(key: String, value: T) =
    auth.toString().shouldNotContainJsonKeyValue(key, value)

infix fun Event.shouldHaveAuth(map: Map<String, Any?>) = auth.toString().shouldMatchJson(map.toJson())
infix fun Event.shouldNotHaveAuth(map: Map<String, Any?>) = auth.toString().shouldNotMatchJson(map.toJson())

inline fun <reified T> Event.shouldContainMetadata(key: String, value: T) =
    metadata.toString().shouldContainJsonKeyValue(key, value)

inline fun <reified T> Event.shouldNotContainMetadata(key: String, value: T) =
    metadata.toString().shouldNotContainJsonKeyValue(key, value)

infix fun Event.shouldHaveMetadata(map: Map<String, Any?>) = metadata.toString().shouldMatchJson(map.toJson())
infix fun Event.shouldNotHaveMetadata(map: Map<String, Any?>) = metadata.toString().shouldNotMatchJson(map.toJson())

infix fun Event.shouldHaveUserId(userId: Long) = this should haveUserId(userId)
infix fun Event.shouldNotHaveUserId(userId: Long) = this shouldNot haveUserId(userId)
fun haveUserId(userId: Long) = object : Matcher<Event> {
    override fun test(value: Event) = MatcherResult(
        value.userId == userId,
        { "Event identity.userId should be $userId but was ${value.userId}" },
        { "Event identity.userId should not be $userId, but was" }
    )
}

infix fun Event.shouldHaveOrigin(origin: String) = this should haveOrigin(origin)
infix fun Event.shouldNotHaveOrigin(origin: String) = this shouldNot haveOrigin(origin)
fun haveOrigin(origin: String) = object : Matcher<Event> {
    override fun test(value: Event) = MatcherResult(
        value.origin == origin,
        { "Event metadata.origin should be $origin but was ${value.origin}" },
        { "Event metadata.origin should not be $origin, but was" }
    )
}

inline fun <reified T> T?.toJson() = JsonAdapterHolder.mapper.toJson(this)
