package com.github.connorwyatt.common.eventstore.events

data class EventEnvelope<TEvent : Event>(val event: TEvent, val metadata: EventMetadata)
