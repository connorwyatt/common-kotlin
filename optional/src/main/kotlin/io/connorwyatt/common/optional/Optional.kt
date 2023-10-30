package io.connorwyatt.common.optional

import io.connorwyatt.common.optional.serialization.OptionalSerializer
import kotlinx.serialization.*

/**
 * Represents an optional value that may or may not be present.
 *
 * Useful for representing `undefined` in JSON.
 *
 * @param <T> The type of the value.
 */
@Serializable(with = OptionalSerializer::class)
sealed interface Optional<out T> {
    fun <R> ifPresent(block: (T) -> R): R? {
        if (this is Present) {
            return block(this.value)
        }

        return null
    }

    data class Present<out T>(val value: T) : Optional<T> {
        override fun toString() = value.toString()
    }

    data object Absent : Optional<Nothing> {
        override fun toString() = "<Absent>"
    }
}
