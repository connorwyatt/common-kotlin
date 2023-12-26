package com.github.connorwyatt.common.eventstore

import com.eventstore.dbclient.*
import com.github.connorwyatt.common.eventstore.streams.StreamDescriptor
import kotlinx.coroutines.future.await

class EventStoreClientWrapper(private val eventStoreDBClient: EventStoreDBClient) {
    suspend fun readStream(
        streamDescriptor: StreamDescriptor,
        readStreamOptions: ReadStreamOptions,
    ): com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.ReadResult {
        val readResult =
            try {
                eventStoreDBClient
                    .readStream(streamDescriptor.streamName, readStreamOptions)
                    .await()
            } catch (exception: Exception) {
                return com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.ReadResult
                    .Failure(exception)
            }

        return com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.ReadResult.Success(
            readResult.events,
            readResult.lastStreamPosition
        )
    }

    suspend fun appendToStream(
        streamDescriptor: StreamDescriptor,
        options: AppendToStreamOptions,
        events: List<EventData>,
    ): com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.WriteResult {
        val writeResult =
            try {
                eventStoreDBClient
                    .appendToStream(streamDescriptor.streamName, options, *events.toTypedArray())
                    .await()
            } catch (exception: Exception) {
                return com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.WriteResult
                    .Failure(exception)
            }

        return com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.WriteResult.Success(
            writeResult.logPosition.commitUnsigned
        )
    }

    fun subscribeToStream(
        streamDescriptor: StreamDescriptor,
        onEvent: ((Subscription, ResolvedEvent) -> Unit)? = null,
        onError: ((Subscription, Throwable) -> Unit)? = null,
        onCancelled: ((Subscription) -> Unit)? = null,
        onConfirmation: ((Subscription) -> Unit)? = null,
        subscribeToStreamOptions: SubscribeToStreamOptions? = null
    ) {
        eventStoreDBClient.subscribeToStream(
            streamDescriptor.streamName,
            object : SubscriptionListener() {
                override fun onEvent(subscription: Subscription, event: ResolvedEvent) {
                    onEvent?.invoke(subscription, event)
                }

                override fun onError(subscription: Subscription, throwable: Throwable) {
                    onError?.invoke(subscription, throwable)
                }

                override fun onCancelled(subscription: Subscription) {
                    onCancelled?.invoke(subscription)
                }

                override fun onConfirmation(subscription: Subscription) {
                    onConfirmation?.invoke(subscription)
                }
            },
            subscribeToStreamOptions
        )
    }

    sealed interface ReadResult {
        data class Success(val events: List<ResolvedEvent>, val streamPosition: Long) :
            com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.ReadResult

        data class Failure(val exception: Exception) :
            com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.ReadResult
    }

    sealed interface WriteResult {
        data class Success(val streamPosition: Long) :
            com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.WriteResult

        data class Failure(val exception: Exception) :
            com.github.connorwyatt.common.eventstore.EventStoreClientWrapper.WriteResult
    }
}
