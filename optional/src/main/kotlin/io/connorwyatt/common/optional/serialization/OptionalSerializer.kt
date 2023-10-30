package io.connorwyatt.common.optional.serialization

import io.connorwyatt.common.optional.Optional
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

class OptionalSerializer<T>(private val valueSerializer: KSerializer<T>) :
    KSerializer<Optional<T>> {
    override val descriptor: SerialDescriptor = valueSerializer.descriptor

    override fun deserialize(decoder: Decoder): Optional<T> =
        Optional.Present(valueSerializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: Optional<T>) {
        if (value is Optional.Present) valueSerializer.serialize(encoder, value.value)
    }
}
