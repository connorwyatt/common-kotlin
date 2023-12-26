package com.github.connorwyatt.common.eventstore.streams

import com.github.connorwyatt.common.eventstore.events.VersionedEventType
import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class StreamDescriptorTests {
    internal class StreamDescriptorParseArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
            Stream.builder<Pair<String, StreamDescriptor>>()
                .apply {
                    add("examples-123" to StreamDescriptor.Origin("examples", "123"))

                    val emptyGuid = "00000000-0000-0000-0000-000000000000"
                    add("examples-$emptyGuid" to StreamDescriptor.Origin("examples", emptyGuid))

                    add("\$ce-examples" to StreamDescriptor.Category("examples"))

                    val eventType = VersionedEventType("examples.exampleEvent", 1)
                    add("\$et-$eventType" to StreamDescriptor.EventType(eventType))

                    add(("\$all" to StreamDescriptor.All))
                }
                .build()
                .map { (streamName, expectedStreamDescriptor) ->
                    Arguments.of(streamName, expectedStreamDescriptor)
                }
    }

    @ParameterizedTest
    @ArgumentsSource(StreamDescriptorParseArgumentsProvider::class)
    fun `parsing a stream name should return the correct StreamDescriptor`(
        streamName: String,
        expectedStreamDescriptor: StreamDescriptor,
    ) {
        expectThat(StreamDescriptor.parse(streamName)).isEqualTo(expectedStreamDescriptor)
    }
}
