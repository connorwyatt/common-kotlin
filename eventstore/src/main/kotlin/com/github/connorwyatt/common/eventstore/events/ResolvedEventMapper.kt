package com.github.connorwyatt.common.eventstore.events

import com.eventstore.dbclient.ResolvedEvent
import kotlinx.serialization.*

class ResolvedEventMapper(private val eventMap: EventMap) {
    fun map(resolvedEvent: ResolvedEvent): EventEnvelope<Event> {
        val type = VersionedEventType.parse(resolvedEvent.event.eventType)
        val eventClass = eventMap.eventClass(type)

        @OptIn(InternalSerializationApi::class) val serializer = eventClass.serializer()

        val event =
            eventJson.decodeFromString(serializer, resolvedEvent.event.eventData.decodeToString())

        return EventEnvelope(event, EventMetadata.fromResolvedEvent(resolvedEvent))
    }
}
