package io.connorwyatt.common.eventstore.events

import io.connorwyatt.common.eventstore.eventhandlers.EventHandler
import io.connorwyatt.common.eventstore.eventhandlers.EventHandlerMap
import io.connorwyatt.common.eventstore.streams.StreamDescriptor
import io.connorwyatt.common.time.clock.Clock
import java.time.Duration
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.time.withTimeout

class InMemoryEventsRepository(
    private val clock: Clock,
    private val eventMap: EventMap,
    private val eventHandlerMap: EventHandlerMap,
    private val eventHandlers: Set<EventHandler>,
) : EventsRepository {
    private var streams = emptyMap<StreamDescriptor, List<EventEnvelope<out Event>>>()
    private val streamUpdateMutex = Mutex()
    private val eventPropagationCoroutineScope = CoroutineScope(Dispatchers.Default)
    private val eventPropagationChannel =
        Channel<Pair<StreamDescriptor, EventEnvelope<out Event>>>()

    override suspend fun readStream(
        streamDescriptor: StreamDescriptor
    ): List<EventEnvelope<out Event>> = (streams[streamDescriptor] ?: emptyList())

    override suspend fun appendToStream(
        streamDescriptor: StreamDescriptor.Origin,
        events: List<Event>,
        expectedStreamVersion: Long?,
    ) {
        val stream = streams[streamDescriptor]

        if (expectedStreamVersion == null) {
            if (!stream.isNullOrEmpty()) {
                // TODO: Check if throwing here is right.
                throw Exception()
            }

            updateStreams(streamDescriptor, events)

            return
        }

        if (stream == null) {
            // TODO: Check if throwing here is right.
            throw Exception()
        }

        updateStreams(streamDescriptor, events)
    }

    fun startEventPropagation() {
        eventPropagationCoroutineScope.launch {
            for ((streamDescriptor, envelope) in eventPropagationChannel) {
                propagateEventToHandlers(streamDescriptor, envelope)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun waitForEmptyEventPropagationQueue(timeout: Duration) {
        withTimeout(timeout) {
            while (!eventPropagationChannel.isEmpty) {
                continue
            }
        }
    }

    private suspend fun updateStreams(
        originStreamDescriptor: StreamDescriptor.Origin,
        events: List<Event>
    ) {
        val now = clock.now()

        try {
            streamUpdateMutex.lock()

            val envelopes =
                events.flatMap { event ->
                    val versionedEventType = eventMap.versionedEventType(event)

                    val nextStreamPositionForOriginStream =
                        nextStreamPosition(originStreamDescriptor)

                    val categoryStreamDescriptor =
                        StreamDescriptor.Category(originStreamDescriptor.category)
                    val eventTypeStreamDescriptor = StreamDescriptor.EventType(versionedEventType)
                    val allStreamDescriptor = StreamDescriptor.All

                    listOf(
                            originStreamDescriptor,
                            categoryStreamDescriptor,
                            eventTypeStreamDescriptor,
                            allStreamDescriptor
                        )
                        .map {
                            val envelope =
                                EventEnvelope(
                                    event,
                                    EventMetadata(
                                        now,
                                        nextStreamPositionForOriginStream,
                                        nextStreamPosition(it)
                                    )
                                )

                            updateStream(it, envelope)

                            it to envelope
                        }
                }

            eventPropagationCoroutineScope.launch { enqueueEventsForPropagation(envelopes) }
        } finally {
            streamUpdateMutex.unlock()
        }
    }

    private fun updateStream(
        streamDescriptor: StreamDescriptor,
        envelope: EventEnvelope<out Event>
    ) {
        val stream = streams[streamDescriptor]

        streams =
            if (stream == null) {
                streams.plus(streamDescriptor to listOf(envelope))
            } else {
                streams.mapValues {
                    if (it.key != streamDescriptor) it.value else it.value.plus(envelope)
                }
            }
    }

    private fun nextStreamPosition(streamDescriptor: StreamDescriptor): Long {
        val lastStreamPositionInOriginStream =
            streams[streamDescriptor]?.lastOrNull()?.metadata?.streamPosition ?: -1
        return lastStreamPositionInOriginStream + 1
    }

    private suspend fun enqueueEventsForPropagation(
        envelopes: List<Pair<StreamDescriptor, EventEnvelope<Event>>>
    ) {
        envelopes.forEach { eventPropagationChannel.send(it) }
    }

    private suspend fun propagateEventToHandlers(
        streamDescriptor: StreamDescriptor,
        envelope: EventEnvelope<out Event>,
    ) {
        val jobs =
            eventHandlerMap
                .eventHandlersFor(streamDescriptor)
                .map { eventHandlerClazz ->
                    eventHandlers.single { it::class == eventHandlerClazz }
                }
                .map {
                    eventPropagationCoroutineScope.launch {
                        // TODO: Error handling.
                        it.handleEvent(streamDescriptor, envelope.event, envelope.metadata)
                    }
                }

        jobs.joinAll()
    }
}
