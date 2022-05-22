package br.com.guiabolso.events.json

import java.util.Locale

object JsonString {
    private const val ASCII_TABLE_SIZE = 128
    private val REPLACEMENT_CHARS = arrayOfNulls<String?>(ASCII_TABLE_SIZE)

    init {
        for (i in 0..0x1f) {
            REPLACEMENT_CHARS[i] = String.format(Locale.ROOT, "\\u%04x", i)
        }
        REPLACEMENT_CHARS['"'.code] = "\\\""
        REPLACEMENT_CHARS['\\'.code] = "\\\\"
        REPLACEMENT_CHARS['\t'.code] = "\\t"
        REPLACEMENT_CHARS['\b'.code] = "\\b"
        REPLACEMENT_CHARS['\n'.code] = "\\n"
        REPLACEMENT_CHARS['\r'.code] = "\\r"
        REPLACEMENT_CHARS['\u000C'.code] = "\\f"
    }

    @Suppress("LoopWithTooManyJumpStatements")
    fun escape(value: String): String {
        return buildString {
            var last = 0
            val length = value.length
            for (i in 0 until length) {
                val c = value[i]
                var replacement: String?
                when {
                    c.code < ASCII_TABLE_SIZE -> {
                        replacement = REPLACEMENT_CHARS[c.code]
                        if (replacement == null) continue
                    }
                    c == '\u2028' -> replacement = "\\u2028"
                    c == '\u2029' -> replacement = "\\u2029"
                    else -> continue
                }
                if (last < i) {
                    append(value, last, i)
                }
                append(replacement)
                last = i + 1
            }
            if (last < length) {
                append(value, last, length)
            }
        }
    }
}
