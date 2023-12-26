package com.github.connorwyatt.common.eventstore.aggregates

import com.github.connorwyatt.common.eventstore.events.Event
import com.github.connorwyatt.common.eventstore.events.EventEnvelope
import kotlin.reflect.KClass

abstract class Aggregate(val id: String) {
    internal var unsavedEvents: List<Event> = emptyList()
        private set

    internal var savedEvents: List<EventEnvelope<out Event>> = emptyList()
        private set

    private var applyFunctions = emptyMap<KClass<out Event>, (Event) -> Unit>()

    internal fun applyEvents(events: List<EventEnvelope<out Event>>) {
        events.forEach {
            applyEvent(it.event)
            savedEvents = savedEvents.plus(it)
        }
    }

    internal fun latestSavedEventVersion(): Long? =
        savedEvents.lastOrNull()?.metadata?.streamPosition

    protected fun <TEvent : Event> handle(clazz: KClass<TEvent>, function: (TEvent) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        applyFunctions = applyFunctions.plus(clazz to function as (Event) -> Unit)
    }

    protected inline fun <reified TEvent : Event> handle(noinline function: (TEvent) -> Unit) {
        handle(TEvent::class, function)
    }

    protected fun <T : Event> raiseEvent(event: T) {
        applyEvent(event)

        unsavedEvents = unsavedEvents.plus(event)
    }

    private fun <T : Event> applyEvent(event: T) {
        val applyFunction =
            applyFunctions[event::class]
                ?: throw Exception(
                    "Apply function was not registered for type ${event::class.qualifiedName}."
                )

        applyFunction.invoke(event)
    }
}
