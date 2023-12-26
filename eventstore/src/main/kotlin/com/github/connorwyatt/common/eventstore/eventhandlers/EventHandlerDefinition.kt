package com.github.connorwyatt.common.eventstore.eventhandlers

import com.github.connorwyatt.common.eventstore.streams.StreamDescriptor
import kotlin.reflect.KClass

class EventHandlerDefinition(
    internal val streamDescriptor: StreamDescriptor,
    internal val clazz: KClass<out EventHandler>
)
