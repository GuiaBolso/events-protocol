package br.com.guiabolso.events.validation

import br.com.guiabolso.events.exception.MissingRequiredParameterException
import br.com.guiabolso.events.json.MapperHolder.mapper
import com.google.gson.JsonNull.INSTANCE
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test


class ValidationsTest {

    @Test
    fun `should accept values on nullable properties`() {
        val json = createJson()
        val serialized = mapper.fromJson(json, Person::class.java)
        validateInput(serialized)
    }

    @Test
    fun `should accept null values on not null properties`() {
        val json = createJson(nickname = null, reference = null)
        val serialized = mapper.fromJson(json, Person::class.java)
        validateInput(serialized)
    }

    @Test
    fun `should not validate nested objects when they are not a data class or validatable`() {
        val nullDate = mapper.fromJson("{ \"daysSinceJose\": null }", YetAnotherDate::class.java)
        val json = createJson(birthDate = nullDate)
        val serialized = mapper.fromJson(json, Person::class.java)
        validateInput(serialized)
    }

    @Test
    fun `should throw exception on null address`() {
        val json = createJson(address = false)
        val serialized = mapper.fromJson(json, Person::class.java)


        val ex = assertThrows(MissingRequiredParameterException::class.java) {
            validateInput(serialized)
        }

        assertEquals("payload.address", ex.eventMessage.parameters["name"])
    }

    @Test
    fun `should throw exception on null street`() {
        val json = createJson(street = null)
        val serialized = mapper.fromJson(json, Person::class.java)

        val ex = assertThrows(MissingRequiredParameterException::class.java) {
            validateInput(serialized)
        }

        assertEquals("payload.address.street", ex.eventMessage.parameters["name"])
    }

    private fun createJson(
        name: String? = "José",
        birthDate: YetAnotherDate? = YetAnotherDate("3650"),
        nickname: String? = "Jovial",
        address: Boolean = true,
        street: String? = "I Would Do  Lima",
        number: String? = "1656",
        reference: String? = "Perto do metrô"
    ) = JsonObject().apply {
        add("name", if (name != null) JsonPrimitive(name) else INSTANCE)
        add("birthDate", if (birthDate != null) mapper.toJsonTree(birthDate) else INSTANCE)
        add("nickname", if (nickname != null) JsonPrimitive(nickname) else INSTANCE)
        add("address", if (address) JsonObject().apply {
            add("street", if (street != null) JsonPrimitive(street) else INSTANCE)
            add("number", if (number != null) JsonPrimitive(number) else INSTANCE)
            add("reference", if (reference != null) JsonPrimitive(reference) else INSTANCE)
        } else INSTANCE)
    }

    data class Person(
        val name: String,
        val birthDate: YetAnotherDate,
        val nickname: String?,
        val address: Address
    )

    @Validatable
    class Address(
        val street: String,
        val number: String,
        val reference: String?
    )

    class YetAnotherDate(
        val daysSinceJose: String
    )

}