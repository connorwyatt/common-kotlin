package com.github.connorwyatt.common.eventstore.events

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*

@OptIn(InternalSerializationApi::class)
internal val eventJson = Json {
    classDiscriminator = EVENT_CLASS_DISCRIMINATOR
    serializersModule = SerializersModule {
        polymorphicDefaultSerializer(Event::class) { instance ->
            @Suppress("UNCHECKED_CAST")
            instance::class.serializer() as SerializationStrategy<Event>
        }
    }
}
