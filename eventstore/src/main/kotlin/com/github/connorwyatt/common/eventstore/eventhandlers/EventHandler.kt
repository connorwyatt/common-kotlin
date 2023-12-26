package com.github.connorwyatt.common.eventstore.eventhandlers

import com.github.connorwyatt.common.eventstore.events.Event
import com.github.connorwyatt.common.eventstore.events.EventMetadata
import com.github.connorwyatt.common.eventstore.streams.StreamDescriptor
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

abstract class EventHandler {
    private var handlers = mapOf<KClass<out Event>, suspend (Event, EventMetadata) -> Unit>()

    abstract suspend fun streamPosition(
        subscriptionName: String,
        streamDescriptor: StreamDescriptor
    ): Long?

    abstract suspend fun updateStreamPosition(cursor: Cursor)

    fun subscriptionName() =
        this::class.findAnnotation<SubscriptionName>()?.name
            ?: throw Exception("Missing SubscriptionName annotation.")

    protected fun <TEvent : Event> handle(
        eventType: KClass<TEvent>,
        handler: suspend (TEvent, EventMetadata) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        handlers =
            handlers.plus(
                eventType as KClass<out Event> to handler as suspend (Event, EventMetadata) -> Unit
            )
    }

    protected inline fun <reified TEvent : Event> handle(
        noinline handler: suspend (TEvent, EventMetadata) -> Unit
    ) {
        handle(TEvent::class, handler)
    }

    internal suspend fun handleEvent(
        streamDescriptor: StreamDescriptor,
        event: Event,
        metadata: EventMetadata
    ) {
        handlers[event::class]?.invoke(event, metadata)
        updateStreamPosition(
            Cursor(
                subscriptionName(),
                streamDescriptor.streamName,
                metadata.aggregatedStreamPosition
            )
        )
    }

    data class Cursor(
        val subscriptionName: String,
        val streamName: String,
        val lastHandledStreamPosition: Long,
    )
}
