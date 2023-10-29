package io.connorwyatt.common.eventstore.eventhandlers

import io.connorwyatt.common.eventstore.streams.StreamDescriptor
import kotlin.reflect.KClass

class EventHandlerDefinition(
    internal val streamDescriptor: StreamDescriptor,
    internal val clazz: KClass<out EventHandler>
)
