package io.connorwyatt.common.eventstore.configuration

data class EventStoreConfiguration(
    val connectionString: String?,
    val useInMemoryEventStore: Boolean,
)
