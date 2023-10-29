package io.connorwyatt.common.eventstore.events

import kotlin.reflect.KClass

class EventMap(private val definitions: Set<EventMapDefinition>) {
    init {
        checkForDuplicates()
    }

    internal fun eventClass(versionedEventType: VersionedEventType): KClass<out Event> {
        return definitions.singleOrNull { it.versionedEventType == versionedEventType }?.clazz
            ?: throw Exception(
                "Could not find Event class for VersionedEventType (${versionedEventType})."
            )
    }

    internal fun versionedEventType(event: Event): VersionedEventType {
        return versionedEventType(event::class)
    }

    internal fun versionedEventType(eventClass: KClass<out Event>): VersionedEventType {
        return definitions.singleOrNull { it.clazz == eventClass }?.versionedEventType
            ?: throw Exception(
                "Could not find VersionedEventType for Event class (${eventClass.simpleName})."
            )
    }

    private fun checkForDuplicates() {
        if (definitions.distinctBy { it.versionedEventType }.count() != definitions.count()) {
            throw Exception("Multiple EventMap entries registered for some VersionedEventType(s).")
        }
    }
}
