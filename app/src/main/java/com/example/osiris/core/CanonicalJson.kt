package com.example.osiris.core

import java.security.MessageDigest
import java.text.Normalizer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull

object CanonicalJson {

    val BIDI_CONTROL_CODEPOINTS = setOf(
        0x200E, // LRM
        0x200F, // RLM
        0x202A, // LRE
        0x202B, // RLE
        0x202C, // PDF
        0x202D, // LRO
        0x202E, // RLO
        0x2066, // LRI
        0x2067, // RLI
        0x2068, // FSI
        0x2069  // PDI
    )

    fun sha256Hex(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun canonicalSha256(bytes: ByteArray): String {
        return "sha256:${sha256Hex(bytes)}"
    }

    fun canonicalize(obj: Any?): ByteArray {
        val sb = StringBuilder()
        serializeRecursive(obj, sb)
        return sb.toString().toByteArray(Charsets.UTF_8)
    }

    private fun serializeRecursive(value: Any?, sb: StringBuilder) {
        when (value) {
            null -> sb.append("null")
            is Boolean -> sb.append(if (value) "true" else "false")
            is Byte, is Short, is Int, is Long -> sb.append(value.toString())
            is Float, is Double -> {
                throw SchemaValidationError(
                    "FLOAT_FORBIDDEN: Floats are prohibited in OSIRIS-CANONICAL-JSON-V1: $value"
                )
            }
            is String -> {
                sb.append(canonicalizeString(value))
            }
            is Map<*, *> -> {
                sb.append("{")
                val sortedKeys = value.keys.map { it.toString() }.sorted()
                var first = true
                for (k in sortedKeys) {
                    validateKey(k)
                    if (!first) sb.append(",")
                    first = false
                    sb.append(canonicalizeString(k))
                    sb.append(":")
                    serializeRecursive(value[k], sb)
                }
                sb.append("}")
            }
            is Collection<*> -> {
                sb.append("[")
                var first = true
                for (item in value) {
                    if (!first) sb.append(",")
                    first = false
                    serializeRecursive(item, sb)
                }
                sb.append("]")
            }
            is JsonElement -> {
                serializeJsonElement(value, sb)
            }
            else -> {
                // Fallback for custom objects or string representation
                sb.append(canonicalizeString(value.toString()))
            }
        }
    }

    private fun serializeJsonElement(element: JsonElement, sb: StringBuilder) {
        when (element) {
            is JsonNull -> sb.append("null")
            is JsonPrimitive -> {
                val boolVal = element.booleanOrNull
                if (boolVal != null) {
                    sb.append(if (boolVal) "true" else "false")
                    return
                }
                val content = element.content
                // Check if it's integer
                val longVal = content.toLongOrNull()
                if (longVal != null && !content.contains(".") && !content.contains("e", ignoreCase = true)) {
                    sb.append(content)
                } else if (content.toDoubleOrNull() != null && (content.contains(".") || content.contains("e", ignoreCase = true))) {
                    throw SchemaValidationError("FLOAT_FORBIDDEN: Floats are prohibited: $content")
                } else {
                    sb.append(canonicalizeString(content))
                }
            }
            is JsonArray -> {
                sb.append("[")
                var first = true
                for (item in element) {
                    if (!first) sb.append(",")
                    first = false
                    serializeJsonElement(item, sb)
                }
                sb.append("]")
            }
            is JsonObject -> {
                sb.append("{")
                val sortedKeys = element.keys.sorted()
                var first = true
                for (k in sortedKeys) {
                    validateKey(k)
                    if (!first) sb.append(",")
                    first = false
                    sb.append(canonicalizeString(k))
                    sb.append(":")
                    serializeJsonElement(element[k]!!, sb)
                }
                sb.append("}")
            }
        }
    }

    fun validateKey(key: String) {
        for (char in key) {
            if (char.code > 127) {
                throw SchemaValidationError("NON_ASCII_KEY: Object keys must be ASCII only, found '$char' in '$key'")
            }
        }
    }

    fun canonicalizeString(raw: String): String {
        // Enforce NFC normalization
        val normalized = Normalizer.normalize(raw, Normalizer.Form.NFC)

        // Check for bidirectional controls and control chars
        for (i in 0 until normalized.length) {
            val codePoint = normalized.codePointAt(i)
            if (codePoint in BIDI_CONTROL_CODEPOINTS) {
                throw SchemaValidationError(
                    "BIDI_CONTROL_FORBIDDEN: Bidi control codepoint U+%04X is forbidden".format(codePoint)
                )
            }
        }

        // Escape JSON string according to canonical rules
        val sb = StringBuilder("\"")
        for (i in 0 until normalized.length) {
            when (val c = normalized[i]) {
                '\"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> {
                    if (c.code < 0x20) {
                        sb.append("\\u%04x".format(c.code))
                    } else {
                        sb.append(c)
                    }
                }
            }
        }
        sb.append("\"")
        return sb.toString()
    }

    data class ValidationDiagnostic(
        val isValid: Boolean,
        val canonicalBytesLength: Int,
        val canonicalSha256: String,
        val errors: List<String>
    )

    fun validateAndCanonicalizeJson(raw: String): ValidationDiagnostic {
        val errors = mutableListOf<String>()

        if (raw.startsWith("\uFEFF")) {
            errors.add("BOM_FORBIDDEN: Byte Order Mark (BOM) is forbidden in OSIRIS-CANONICAL-JSON-V1.")
        }

        var canonicalBytes = ByteArray(0)
        var sha256 = ""

        try {
            val jsonElement = Json.parseToJsonElement(raw)
            canonicalBytes = canonicalize(jsonElement)
            sha256 = canonicalSha256(canonicalBytes)
        } catch (e: Exception) {
            errors.add(e.message ?: e.toString())
        }

        return ValidationDiagnostic(
            isValid = errors.isEmpty(),
            canonicalBytesLength = canonicalBytes.size,
            canonicalSha256 = sha256,
            errors = errors
        )
    }
}
