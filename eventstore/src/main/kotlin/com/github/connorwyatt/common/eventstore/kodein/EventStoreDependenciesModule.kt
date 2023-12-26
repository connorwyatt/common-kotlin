package com.github.connorwyatt.common.eventstore.kodein

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.github.connorwyatt.common.eventstore.EventStoreClientWrapper
import com.github.connorwyatt.common.eventstore.aggregates.Aggregate
import com.github.connorwyatt.common.eventstore.aggregates.AggregateMap
import com.github.connorwyatt.common.eventstore.aggregates.AggregateMapDefinition
import com.github.connorwyatt.common.eventstore.aggregates.AggregatesRepository
import com.github.connorwyatt.common.eventstore.configuration.EventStoreConfiguration
import com.github.connorwyatt.common.eventstore.eventhandlers.EventHandler
import com.github.connorwyatt.common.eventstore.eventhandlers.EventHandlerDefinition
import com.github.connorwyatt.common.eventstore.eventhandlers.EventHandlerMap
import com.github.connorwyatt.common.eventstore.eventhandlers.EventStoreSubscriptionsManager
import com.github.connorwyatt.common.eventstore.events.EventMap
import com.github.connorwyatt.common.eventstore.events.EventMapDefinition
import com.github.connorwyatt.common.eventstore.events.EventStoreEventsRepository
import com.github.connorwyatt.common.eventstore.events.EventsRepository
import com.github.connorwyatt.common.eventstore.events.InMemoryEventsRepository
import com.github.connorwyatt.common.eventstore.events.ResolvedEventMapper
import org.kodein.di.*

fun eventStoreDependenciesModule(eventStoreConfiguration: EventStoreConfiguration): DI.Module =
    DI.Module(name = ::eventStoreDependenciesModule.name) {
        bindSingletonOf(::AggregatesRepository)
        bindSingletonOf(::AggregateMap)
        bindSet<AggregateMapDefinition<Aggregate>>()

        if (!eventStoreConfiguration.useInMemoryEventStore) {
            bindProvider<EventsRepository> { new(::EventStoreEventsRepository) }
            bindSingleton<EventStoreDBClient> {
                val settings =
                    EventStoreDBConnectionString.parseOrThrow(
                        eventStoreConfiguration.connectionString
                            ?: throw Exception("EventStore connectionString is not set.")
                    )

                EventStoreDBClient.create(settings)
            }
            bindProviderOf(::EventStoreClientWrapper)
            bindSingletonOf(::EventStoreSubscriptionsManager)
            bindProviderOf(::ResolvedEventMapper)
        } else {
            bindSingleton<EventsRepository> { new(::InMemoryEventsRepository) }
        }

        bindSingletonOf(::EventMap)
        bindSet<EventMapDefinition>()

        bindSet<EventHandler>()
        bindSingletonOf(::EventHandlerMap)
        bindSet<EventHandlerDefinition>()
    }
