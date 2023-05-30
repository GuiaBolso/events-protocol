package br.com.guiabolso.events.json.jackson

import com.fasterxml.jackson.annotation.JsonIgnore

internal abstract class IgnoreEventGetters {

    val userIdAsString: String
        @JsonIgnore
        get() = ""

    val user: String
        @JsonIgnore
        get() = ""

    val userId: String
        @JsonIgnore
        get() = ""

    val origin: String
        @JsonIgnore
        get() = ""

    @JsonIgnore
    abstract fun isSuccess()

    @JsonIgnore
    abstract fun isError()

    @JsonIgnore
    abstract fun isRedirect()

    @JsonIgnore
    abstract fun getErrorType()

    @JsonIgnore
    abstract fun user()
}
