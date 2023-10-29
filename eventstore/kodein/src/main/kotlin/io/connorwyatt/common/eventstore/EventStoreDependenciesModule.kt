package io.connorwyatt.common.eventstore

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString
import io.connorwyatt.common.eventstore.aggregates.Aggregate
import io.connorwyatt.common.eventstore.aggregates.AggregateMap
import io.connorwyatt.common.eventstore.aggregates.AggregateMapDefinition
import io.connorwyatt.common.eventstore.aggregates.AggregatesRepository
import io.connorwyatt.common.eventstore.eventhandlers.EventHandler
import io.connorwyatt.common.eventstore.eventhandlers.EventHandlerDefinition
import io.connorwyatt.common.eventstore.eventhandlers.EventHandlerMap
import io.connorwyatt.common.eventstore.eventhandlers.EventStoreSubscriptionsManager
import io.connorwyatt.common.eventstore.events.EventMap
import io.connorwyatt.common.eventstore.events.EventMapDefinition
import io.connorwyatt.common.eventstore.events.EventStoreEventsRepository
import io.connorwyatt.common.eventstore.events.EventsRepository
import io.connorwyatt.common.eventstore.events.InMemoryEventsRepository
import io.connorwyatt.common.eventstore.events.ResolvedEventMapper
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
