package io.connorwyatt.common.eventstore.ktor

import io.connorwyatt.common.eventstore.configuration.EventStoreConfiguration
import io.connorwyatt.common.eventstore.eventhandlers.EventStoreSubscriptionsManager
import io.connorwyatt.common.eventstore.events.EventsRepository
import io.connorwyatt.common.eventstore.events.InMemoryEventsRepository
import io.ktor.server.application.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.configureEventStore(eventStoreConfiguration: EventStoreConfiguration) {
    if (!eventStoreConfiguration.useInMemoryEventStore) {
        val eventStoreSubscriptionsManager by closestDI().instance<EventStoreSubscriptionsManager>()

        eventStoreSubscriptionsManager.start()
    }

    val eventsRepository by closestDI().instance<EventsRepository>()

    (eventsRepository as? InMemoryEventsRepository)?.run { startEventPropagation() }
}
