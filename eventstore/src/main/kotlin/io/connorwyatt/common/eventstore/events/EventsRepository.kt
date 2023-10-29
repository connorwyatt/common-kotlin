package io.connorwyatt.common.eventstore.events

import io.connorwyatt.common.eventstore.streams.StreamDescriptor

interface EventsRepository {
    suspend fun readStream(streamDescriptor: StreamDescriptor): List<EventEnvelope<out Event>>

    suspend fun appendToStream(
        streamDescriptor: StreamDescriptor.Origin,
        events: List<Event>,
        expectedStreamVersion: Long?,
    )
}
