package io.connorwyatt.common.eventstore

import io.connorwyatt.common.eventstore.aggregates.Aggregate
import io.connorwyatt.common.eventstore.aggregates.AggregateMapDefinition
import io.connorwyatt.common.eventstore.eventhandlers.EventHandler
import io.connorwyatt.common.eventstore.eventhandlers.EventHandlerDefinition
import io.connorwyatt.common.eventstore.events.Event
import io.connorwyatt.common.eventstore.events.EventMapDefinition
import io.connorwyatt.common.eventstore.events.VersionedEventType
import io.connorwyatt.common.eventstore.streams.StreamDescriptor
import org.kodein.di.*
import org.kodein.di.bindings.*

inline fun <reified TAggregate : Aggregate> DI.Builder.bindAggregateDefinition(
    category: String,
    noinline constructor: (String) -> TAggregate
) {
    inBindSet<AggregateMapDefinition<Aggregate>> {
        add { singleton { AggregateMapDefinition(category, TAggregate::class, constructor) } }
    }
}

inline fun <reified TEvent : Event> DI.Builder.bindEventDefinition(
    versionedEventType: VersionedEventType
) {
    inBindSet<EventMapDefinition> {
        add { singleton { EventMapDefinition(versionedEventType, TEvent::class) } }
    }
}

inline fun <reified TEventHandler : EventHandler> DI.Builder.bindEventHandler(
    streamDescriptors: Set<StreamDescriptor>,
    noinline constructor: NoArgBindingDI<*>.() -> TEventHandler,
) {
    inBindSet<EventHandler> { add { singleton { constructor() } } }
    inBindSet<EventHandlerDefinition> {
        streamDescriptors.forEach { streamDescriptor ->
            add { singleton { EventHandlerDefinition(streamDescriptor, TEventHandler::class) } }
        }
    }
}
