package io.connorwyatt.common.eventstore.aggregates

import io.connorwyatt.common.eventstore.events.EventsRepository
import io.connorwyatt.common.eventstore.streams.StreamDescriptor
import kotlin.reflect.KClass

class AggregatesRepository(
    private val eventsRepository: EventsRepository,
    private val aggregateMap: AggregateMap
) {
    suspend fun <TAggregate : Aggregate> load(clazz: KClass<TAggregate>, id: String): TAggregate {
        val (category, _, constructor) = aggregateMap.definitionFor(clazz)
        val streamDescriptor = StreamDescriptor.Origin(category, id)

        val aggregate = constructor.invoke(id)

        val events = eventsRepository.readStream(streamDescriptor)

        aggregate.applyEvents(events)

        return aggregate
    }

    suspend inline fun <reified TAggregate : Aggregate> load(id: String): TAggregate {
        return load(TAggregate::class, id)
    }

    suspend fun <TAggregate : Aggregate> save(aggregate: TAggregate) {
        if (aggregate.unsavedEvents.isEmpty()) {
            return
        }

        val (category) = aggregateMap.definitionFor(aggregate::class)

        val streamDescriptor = StreamDescriptor.Origin(category, aggregate.id)

        eventsRepository.appendToStream(
            streamDescriptor,
            aggregate.unsavedEvents,
            aggregate.latestSavedEventVersion()
        )
    }
}
