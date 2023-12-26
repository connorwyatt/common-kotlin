package com.github.connorwyatt.common.rabbitmq.bus

import com.github.connorwyatt.common.rabbitmq.Command
import com.github.connorwyatt.common.rabbitmq.CommandEnvelope
import com.github.connorwyatt.common.rabbitmq.CommandMap
import com.github.connorwyatt.common.rabbitmq.routing.CommandRoutingRules
import com.github.connorwyatt.common.rabbitmq.routing.RoutingKeyUtilities
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Connection
import kotlin.reflect.KClass
import kotlinx.serialization.*
import kotlinx.serialization.json.*

internal class RabbitMQCommandBus(
    private val connection: Connection,
    private val exchangeName: String,
    private val commandMap: com.github.connorwyatt.common.rabbitmq.CommandMap,
    private val commandRoutingRules: CommandRoutingRules
) : CommandBus {
    override suspend fun send(
        commandEnvelope: com.github.connorwyatt.common.rabbitmq.CommandEnvelope
    ) {
        send(listOf(commandEnvelope))
    }

    override suspend fun send(
        commandEnvelopes: List<com.github.connorwyatt.common.rabbitmq.CommandEnvelope>
    ) {
        connection.createChannel().use { channel ->
            try {
                channel.txSelect()
                commandEnvelopes.forEach { commandEnvelope ->
                    // TODO: Fix this.
                    val commandClass =
                        commandEnvelope.command::class
                            as KClass<com.github.connorwyatt.common.rabbitmq.Command>

                    @OptIn(InternalSerializationApi::class)
                    val serializer = commandClass.serializer()

                    val serializedCommand =
                        Json.encodeToString(serializer, commandEnvelope.command).encodeToByteArray()

                    val destinationQueueName = commandRoutingRules.queueFor(commandClass)

                    channel.basicPublish(
                        exchangeName,
                        RoutingKeyUtilities.routingKeyFor("$exchangeName.$destinationQueueName"),
                        AMQP.BasicProperties.Builder()
                            .apply { type(commandMap.typeFor(commandClass)) }
                            .build(),
                        serializedCommand
                    )
                }
                channel.txCommit()
            } catch (exception: Exception) {
                channel.txRollback()
            }
        }
    }
}
