package com.github.connorwyatt.common.eventstore.events

import com.eventstore.dbclient.AppendToStreamOptions
import com.eventstore.dbclient.EventDataBuilder
import com.eventstore.dbclient.ExpectedRevision
import com.eventstore.dbclient.ReadStreamOptions
import com.github.connorwyatt.common.eventstore.EventStoreClientWrapper
import com.github.connorwyatt.common.eventstore.streams.StreamDescriptor

class EventStoreEventsRepository(
    private val eventStoreClient: com.github.connorwyatt.common.eventstore.EventStoreClientWrapper,
    private val eventMap: EventMap,
    private val resolvedEventMapper: ResolvedEventMapper
) : EventsRepository {
    override suspend fun readStream(
        streamDescriptor: StreamDescriptor
    ): List<EventEnvelope<out Event>> {
        val result = eventStoreClient.readStream(streamDescriptor, readStreamOptions)

        return when (result) {
            is com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.ReadResult.Failure ->
                emptyList()
            is com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.ReadResult.Success ->
                result.events.map(resolvedEventMapper::map)
        }
    }

    override suspend fun appendToStream(
        streamDescriptor: StreamDescriptor.Origin,
        events: List<Event>,
        expectedStreamVersion: Long?,
    ) {
        if (events.isEmpty()) {
            return
        }

        val eventDataList =
            events.map {
                val versionedEventType = eventMap.versionedEventType(it)
                val serializedEvent =
                    eventJson.encodeToString(EventTransformingSerializer, it).toByteArray()
                EventDataBuilder.json(versionedEventType.toString(), serializedEvent).build()
            }

        val options =
            expectedStreamVersion?.let { appendToStreamOptions(it) }
                ?: appendToStreamOptionsNoStream

        val result = eventStoreClient.appendToStream(streamDescriptor, options, eventDataList)

        if (
            result
                is
                com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.WriteResult.Failure
        )
            throw result.exception
    }

    companion object {
        private val readStreamOptions by lazy { ReadStreamOptions.get().apply { fromStart() } }

        private val appendToStreamOptionsNoStream by lazy {
            AppendToStreamOptions.get().expectedRevision(ExpectedRevision.noStream())
        }

        private fun appendToStreamOptions(revision: Long): AppendToStreamOptions {
            return AppendToStreamOptions.get().expectedRevision(revision)
        }
    }
}
