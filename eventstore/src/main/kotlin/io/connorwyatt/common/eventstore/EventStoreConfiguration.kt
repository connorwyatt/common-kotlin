package io.connorwyatt.common.eventstore

data class EventStoreConfiguration(
    val connectionString: String?,
    val useInMemoryEventStore: Boolean,
)
