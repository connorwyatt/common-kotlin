package io.connorwyatt.common.eventstore.streams

import io.connorwyatt.common.eventstore.events.VersionedEventType

abstract class StreamDescriptor {
    abstract val streamName: String

    companion object {
        fun parse(streamName: String): StreamDescriptor {
            if (streamName.isBlank()) {
                throw Exception("Cannot parse empty/blank streamName.")
            }

            return when {
                streamName == StreamConstants.allStreamName -> All
                streamName.startsWith(StreamConstants.eventTypeStreamNamePrefix) ->
                    parseEventTypeStreamName(streamName)
                streamName.startsWith(StreamConstants.categoryStreamNamePrefix) ->
                    parseCategoryStreamName(streamName)
                streamName.startsWith('$') ->
                    throw Exception("Cannot parse streamName \"$streamName\".")
                else -> parseOriginStreamName(streamName)
            }
        }

        inline fun <reified TExpectedStreamDescriptor : StreamDescriptor> parseAs(
            streamName: String
        ): TExpectedStreamDescriptor {
            val streamDescriptor = parse(streamName)

            if (streamDescriptor !is TExpectedStreamDescriptor) {
                throw Exception(
                    "Stream descriptor was not of type ${TExpectedStreamDescriptor::class.simpleName}."
                )
            }

            return streamDescriptor
        }

        private fun parseEventTypeStreamName(streamName: String): EventType {
            val parts = streamName.split('-', limit = 2)

            if (parts.count() != 2) {
                throw Exception("Cannot parse streamName \"$streamName\".")
            }

            val (_, eventType) = parts

            return EventType(VersionedEventType.parse(eventType))
        }

        private fun parseCategoryStreamName(streamName: String): Category {
            val parts = streamName.split('-', limit = 2)

            if (parts.count() != 2) {
                throw Exception("Cannot parse streamName \"$streamName\".")
            }

            val (_, category) = parts

            return Category(category)
        }

        private fun parseOriginStreamName(streamName: String): Origin {
            val parts = streamName.split('-', limit = 2)

            if (parts.count() != 2) {
                throw Exception("Cannot parse streamName \"$streamName\".")
            }

            val (category, id) = parts

            return Origin(category, id)
        }
    }

    data object All : StreamDescriptor() {
        override val streamName: String
            get() = StreamConstants.allStreamName
    }

    data class EventType(val versionedEventType: VersionedEventType) : StreamDescriptor() {
        override val streamName: String
            get() = "${StreamConstants.eventTypeStreamNamePrefix}-$versionedEventType"
    }

    data class Category(val category: String) : StreamDescriptor() {
        override val streamName: String
            get() = "${StreamConstants.categoryStreamNamePrefix}-$category"
    }

    data class Origin(val category: String, val id: String) : StreamDescriptor() {
        override val streamName: String
            get() = "$category-$id"
    }
}
